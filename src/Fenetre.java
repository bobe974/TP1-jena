
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;



public class Fenetre extends JFrame {

    private JTextField textField;
    private JTextField textField_1;
    private Model model;
    //textarea
    ArrayList<String> leseleves = new ArrayList<>();
    private JTextArea jTextArea;
    //liste
//    DefaultListModel<String> lesEleves = new DefaultListModel<>();
//    private JList<String> jList;


    public static void main(String[] args) {
        Fenetre window = new Fenetre();
        window.setSize(600,400);
        window.setVisible(true);


    }

    public Fenetre() {
        super("tp1-jena");
        // créer un modèle vide
        this.model = ModelFactory.createDefaultModel();
        initialize();


    }

    private void initialize() {

        JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
        onglets.setForeground(Color.BLACK);

        //panneau principal
        JPanel panneau = new JPanel();
        panneau.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

        //panneau pour les onglets
        JPanel panneau1= new JPanel();
        panneau1.setPreferredSize(new Dimension(500,300));
        JPanel panneau2 = new JPanel();;
        jTextArea = new JTextArea();
        jTextArea.setPreferredSize(new Dimension(400,280));
//        jList = new JList<>(lesEleves);
        //ajoute la liste dans le panneau 2
//        panneau2.add(jList);
        panneau2.add(jTextArea);



        onglets.addTab("ajouter un contact",panneau1);
        onglets.addTab("liste des contacts",panneau2);


        panneau.add(onglets);
        add(panneau);
        //champs nom
        JLabel lblName = new JLabel("Nom");
        lblName.setBounds(200, 31, 60, 14);
        panneau1.add(lblName);

        textField = new JTextField();
        textField.setBounds(270, 28, 200, 40);
        panneau1.add(textField);
        textField.setColumns(10);

        //champs prénom
        JLabel lbPrenom= new JLabel("Prénom");
        panneau1.add(lbPrenom);

        textField_1 = new JTextField();
        panneau1.add(textField_1);
        textField_1.setColumns(10);

        JButton btnClear = new JButton("Clear");
        panneau1.add(btnClear);

        JButton btnSubmit = new JButton("valider");

        btnSubmit.setBackground(Color.BLUE);
        btnSubmit.setForeground(Color.BLUE);
        panneau1.add(btnSubmit);

        //charge les données
        readRdfFile();
        loadRdf();

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField_1.setText(null);
                textField.setText(null);
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jTextArea.setText("");
                //recupere les saisies
                Vcard vcard = new Vcard(textField.getText(),textField_1.getText());
                addVcard(vcard, model);

                // utiliser le FileManager pour trouver le fichier d'entrée
                //String modelname = "xxxaa";
                //recupere le fichier
                //TODO parcours de tout les fichier
                //ajout de tout les fichiers dans le model
                readRdfFile();
                loadRdf();
               //readRdf();

            }
        });

    }

    /**
     * ajoute une vcard dans le un model
     * @param vcard
     */
    public void addVcard(Vcard vcard, Model lemodel){

        //créer la ressource et ajouter des propriétés en cascade
        lemodel =  ModelFactory.createDefaultModel();
        lemodel.createResource("http://universite/personne/"+vcard.getNom()+vcard.getprenom())
                .addProperty(VCARD.FN, vcard.getfullName())
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given,vcard.getprenom())
                                .addProperty(VCARD.Family, vcard.getNom()));
        saveData(lemodel, vcard.getfullName());

    }

    /**
     * créer le fichier qui contient tout les ressources dans un répertoire dédié
     */
    public void saveData(Model unmodel, String filename){
        //sauvegarde du modele dans un fichier
        String dir = System.getProperty("user.dir")+"/vcard/"+filename;
        File file = new File(dir);
        try {
            unmodel.write(new FileOutputStream(file),"RDF/XML-ABBREV");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * lit tous les fichiers rdf du repétoire et l'ajout dans le model
     */
    public void readRdfFile() {
        File dir = new File(System.getProperty("user.dir")+"/vcard/");
        File[] liste = dir.listFiles();
        for (File item : liste) {
            if (item.isFile()) {

                System.out.format("Nom du fichier: %s%n", item.getName());
                InputStream in = null ;
                if(!(item.getName().equals(".DS_Store"))){
                    try {
                        in = new FileInputStream(item);
                        // lire le fichier RDF/XML
                        model.read(in, null);

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    if (in == null) {
                        throw new IllegalArgumentException(
                                "Fichier: " + item + " non trouvé");
                    }
                }

            }
        }
    }


    /**
     * affichage des données dans un jtextArea
     */
    public void loadRdf(){
        //afficher toutes les Vcards
        ResIterator iter = model.listResourcesWithProperty(VCARD.FN);
        if (iter.hasNext()) {
            System.out.println("The database contains vcards for:");

            while (iter.hasNext()) {

                String valeur = iter.nextResource()
                        .getRequiredProperty(VCARD.FN)
                        .getString();
                //todo les espaces font crash l'appli
                //vérifie l'élément n'est pas deja dans la liste
                if(!leseleves.contains(valeur)){
                    leseleves.add(valeur);
                }
            }
        } else {
            System.out.println("No vcards were found in the database");
        }

        //ajout dans le textarea
        for( String str : leseleves){
            jTextArea.append(str+"\n");
        }
        // affiche le modèle comme RDF/XML
        model.write(System.out, "RDF/XML-ABBREV");
    }

    //    public void readRdf(){
//        // liste des déclarations dans le modèle
//        StmtIterator iter = this.model.listStatements();
//
//        // affiche l'objet, le prédicat et le sujet de chaque déclaration
//        while (iter.hasNext()) {
//            Statement stmt      = iter.nextStatement();  // obtenir la prochaine déclaration
//            Resource  subject   = stmt.getSubject();     // obtenir le sujet
//            Property  predicate = stmt.getPredicate();   // obtenir le prédicat
//            RDFNode   object    = stmt.getObject();      // obtenir l'objet
//
//            System.out.print(subject.toString());
//            System.out.print(" " + predicate.toString() + " ");
//            if (object instanceof Resource) {
//                System.out.print(object.toString());
//            } else {
//                // l'objet est un littéral
//                System.out.print(" \"" + object.toString() + "\"");
//            }
//
//            System.out.println(" .");
//        }
//    }
}
