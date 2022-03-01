
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;



public class Fenetre extends JFrame {

    private JFrame frame;
    private JTextField textField;
    private JTextField textField_1;
    private Model model;
    DefaultListModel<String> lesEleves = new DefaultListModel<>();
    private JList<String> jList;






    public static void main(String[] args) {
        Fenetre window = new Fenetre();
        window.setSize(600,400);
        window.setVisible(true);


    }

    public Fenetre() {
        super("tp1-jena");
        initialize();
        // créer un modèle vide
        this.model = ModelFactory.createDefaultModel();

    }

    /**
     * ajoute une vcard dans le un model
     * @param vcard
     */
    public void addVcard(Vcard vcard){
        //créer la ressource et ajouter des propriétés en cascade
        Resource etudiant = model.createResource("http://universite/personne")
                .addProperty(VCARD.FN, vcard.getNom())
                .addProperty(VCARD.N,
                        model.createResource()
                                .addProperty(VCARD.Given,vcard.getprenom())
                                .addProperty(VCARD.Family, vcard.getNom()));
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
        JPanel panneau2 = new JPanel();
        lesEleves.addElement("test");
        lesEleves.addElement("testtttt");
        jList = new JList<>(lesEleves);
        //ajoute la liste dans le panneau 2
        panneau2.add(jList);



        onglets.addTab("ajouter un éléve",panneau1);
        onglets.addTab("liste des éléves",panneau2);


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

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField_1.setText(null);
                textField.setText(null);
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //recupere les saisies
                Vcard vcard = new Vcard(textField.getText(),textField_1.getText());
                addVcard(vcard);

                // liste des déclarations dans le modèle
                StmtIterator iter = model.listStatements();

                // affiche l'objet, le prédicat et le sujet de chaque déclaration   A VIRER
                while (iter.hasNext()) {
                    Statement stmt      = iter.nextStatement();  // obtenir la prochaine déclaration
                    Resource  subject   = stmt.getSubject();     // obtenir le sujet
                    Property  predicate = stmt.getPredicate();   // obtenir le prédicat
                    RDFNode   object    = stmt.getObject();      // obtenir l'objet

                    lesEleves.addElement(object.toString());
                    System.out.print(subject.toString());
                    System.out.print(" " + predicate.toString() + " ");
                    if (object instanceof Resource) {
                        System.out.print(object.toString());
                    } else {
                        // l'objet est un littéral
                        System.out.print(" \"" + object.toString() + "\"");
                    }

                    System.out.println(" .");
                }

                //TODO lire le fichier rdf model et lister les propriétés


                //sauvegarde du modele dans un fichier
                String dir = System.getProperty("user.dir")+"/model";
                System.out.println(dir);
                File file = new File(dir);
                try {
                    model.write(new FileOutputStream(file),"RDF/XML-ABBREV");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
}
