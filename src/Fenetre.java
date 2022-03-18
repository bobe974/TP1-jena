import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.VCARD;

public class Fenetre extends JFrame {

    private JPanel panneau, panneau1, panneau2, panneau3;
    private JTabbedPane onglets;
    private JTextField textField, textField_1;
    private JLabel lbPrenom, lblName, lblsparsql;
    private JButton btnClear, btnSubmit, btnSparql;
    private Model model;
    private int launch = 0;
    //textarea
    private ArrayList < String > leseleves = new ArrayList < > ();
    private JTextArea jTextArea, jTextrdf, jtextsparql, jresultsparql;
    private JScrollPane sp, sp2;
    private JTable jTable;
    //En-têtes pour JTable
    private String[] columns = new String[] {
            "Nom",
            "Prénom",
            "URI"
    };
    private Object[][] data = new Object[20][20];

    public static void main(String[] args) {
        Fenetre window = new Fenetre();
        window.setSize(800, 500);
        window.setVisible(true);

    }

    public Fenetre() {
        super("tp1-jena");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //centrer la jframe
        //récuperer la taille de l'écran
        Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
        int height = tailleEcran.height;
        int width = tailleEcran.width;
        //taille est un demi la longueur et l'hauteur
        setSize(width/2, height/2);
        setLocationRelativeTo(null);
        // créer un modèle vide
        this.model = ModelFactory.createDefaultModel();
        initialize();

    }

    private void initialize() {

        onglets = new JTabbedPane(SwingConstants.TOP);
        onglets.setForeground(Color.BLACK);

        //panneau principal
        panneau = new JPanel();
        panneau.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        //panneau pour les onglets
        panneau1 = new JPanel();
        panneau1.setPreferredSize(new Dimension(500, 400));
        panneau2 = new JPanel();
        panneau3 = new JPanel();

        jTextArea = new JTextArea();
        jTextArea.setPreferredSize(new Dimension(500, 400));
        jTextrdf = new JTextArea();
        jTextrdf.setPreferredSize(new Dimension(500, 400));



        //scroll pane
        jTable = new JTable(data, columns);

        sp = new JScrollPane(jTable);
        sp2 = new JScrollPane(jTextrdf);
        sp.setPreferredSize(new Dimension(500, 400));
        sp2.setPreferredSize(new Dimension(500, 400));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setPreferredSize(new Dimension(400, 280));
        sp2.setPreferredSize(new Dimension(400, 280));


        panneau2.add(sp);
        panneau2.add(sp2);



        onglets.addTab("ajouter un contact", panneau1);
        onglets.addTab("liste des contacts", panneau2);
        onglets.addTab("requetes SPARQL", panneau3);


        panneau.add(onglets);
        add(panneau);
        //champs nom
        lblName = new JLabel("Nom");
        lblName.setBounds(250, 40, 60, 14);
        panneau1.add(lblName);

        textField = new JTextField();
        textField.setBounds(350, 28, 200, 40);
        panneau1.add(textField);
        textField.setColumns(10);

        //champs prénom
        lbPrenom = new JLabel("Prénom");
        lbPrenom.setBounds(250, 110, 60, 14);
        panneau1.add(lbPrenom);
        panneau1.setLayout(null);

        textField_1 = new JTextField();
        panneau1.add(textField_1);
        textField_1.setColumns(10);
        textField_1.setBounds(350, 100, 200, 40);

        btnClear = new JButton("Clear");
        btnClear.setBounds(300,200,100,50);
        panneau1.add(btnClear);

        btnSubmit = new JButton("valider");

        btnSubmit.setBackground(Color.BLUE);
        btnSubmit.setForeground(Color.BLUE);
        btnSubmit.setBounds(500,200,100,50);
        panneau1.add(btnSubmit);

        //panneau sparsql
        lblsparsql = new JLabel("saisir une requete SPARQL");
        lblsparsql.setBounds(300, 10, 200, 40);

        jtextsparql = new JTextArea();
        jtextsparql.setText("SELECT ?x ?fname\n" +
                "WHERE {?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  ?fname}");
//        jtextsparql.setPreferredSize(new Dimension(400, 100));
        jtextsparql.setBounds(190, 50, 450, 100);
        jresultsparql = new JTextArea();
        jresultsparql.setBounds(180, 170, 470, 170);
//        jresultsparql.setPreferredSize(new Dimension(400, 100));

        btnSparql = new JButton("Exécuter");
        btnSparql.setBounds(350, 350, 100,50);

        panneau3.setLayout(null);
        panneau3.add(lblsparsql);
        panneau3.add(jtextsparql);
        panneau3.add(jresultsparql);
        panneau3.add(btnSparql);


        //charge les données
        readRdfFile();
        loadRdf();

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField_1.setText(null);
                textField.setText(null);
            }
        });

        btnSparql.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requeteSparql(jtextsparql.getText());
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launch = 1;
                jTextArea.setText("");
                //recupere les saisies
                Vcard vcard = new Vcard(textField.getText(), textField_1.getText());
                addVcard(vcard, model);

                //TODO parcours de tout les fichier
                //ajout de tout les fichiers dans le model
                readRdfFile();
                loadRdf();
                jTable.setModel(new DefaultTableModel(data,columns));
                JOptionPane.showMessageDialog(null, "Vcard crée", "tp jena", JOptionPane.PLAIN_MESSAGE);
                textField_1.setText("");
                textField.setText("");
            }
        });
    }

    /**
     * ajoute une vcard dans le un model
     * @param vcard
     */
    public void addVcard(Vcard vcard, Model lemodel) {

        //créer la ressource et ajouter des propriétés en cascade
        lemodel = ModelFactory.createDefaultModel();
        lemodel.createResource("http://universite/personne/" + vcard.getNom() + vcard.getprenom())
                .addProperty(VCARD.FN, vcard.getfullName())
                .addProperty(VCARD.N,
                        lemodel.createResource()
                                .addProperty(VCARD.Given, vcard.getprenom())
                                .addProperty(VCARD.Family, vcard.getNom()));
        saveData(lemodel, vcard.getfullName());

    }

    /**
     * créer le fichier qui contient tout les ressources dans un répertoire dédié
     */
    public void saveData(Model unmodel, String filename) {
        //sauvegarde du modele dans un fichier
        String dir = System.getProperty("user.dir")+"/vcard/"+filename;
        //"/Users/etienne/Desktop/vcard/" + filename;

        File file = new File(dir);
        try {
            unmodel.write(new FileOutputStream(file), "RDF/XML-ABBREV");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * lit tous les fichiers rdf du repétoire et l'ajout dans le model
     */
    public void readRdfFile() {
        File dir = new File(System.getProperty("user.dir")+"/vcard/");
        // new File("/Users/etienne/Desktop/vcard/");
        if(!dir.exists()){
            System.out.println("création du répertoire");
            dir.mkdir();
        }

        File[] liste = dir.listFiles();
        for (File item: liste) {
            if (item.isFile()) {

                System.out.format("Nom du fichier: %s%n", item.getName());
                InputStream in = null;
                if (!(item.getName().equals(".DS_Store"))) {
                    try { in = new FileInputStream(item);
                        // lire le fichier RDF/XML
                        model.read( in , null);

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    if ( in == null) {
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
    public void loadRdf() {
        //afficher toutes les Vcards
        ResIterator iter = model.listResourcesWithProperty(VCARD.FN);
        ResIterator iter1 = model.listResourcesWithProperty(VCARD.Given);
        ResIterator iter2 = model.listResourcesWithProperty(VCARD.Family);
        if (iter.hasNext()) {
            System.out.println("The database contains vcards for:");
            int i = 0;
            while (iter.hasNext()) {

                Resource resource = iter.nextResource();
                String valeur = resource
                        .getRequiredProperty(VCARD.FN)
                        .getString();

                String uri = resource.getURI();
                //todo les espaces font crash l'appli
                //vérifie l'élément n'est pas deja dans la liste
                if (!leseleves.contains(valeur)) {
                    //alimente le tableau
                    leseleves.add(valeur);
                    data[i][0] = iter2.nextResource().getRequiredProperty(VCARD.Family).getString();
                    data[i][1] = iter1.nextResource().getRequiredProperty(VCARD.Given).getString();
                    data[i][2] = uri;
                    i++;
                }
            }
        } else {
            System.out.println("No vcards were found in the database");
        }

        // affiche le modèle comme RDF/XML
        String syntax = "RDF/XML-ABBREV";
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String result = out.toString();
        System.out.println(result);
        jTextrdf.setText(result);
        //TODO UPDATE JTABLE et textarea rdf

    }

    public void requeteSparql(String req) {

        Query query = QueryFactory.create(req);
        QueryExecution qe = QueryExecutionFactory.create(query, this.model);
        ResultSet rs = qe.execSelect();


        System.out.println("******************************");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ResultSetFormatter.out(baos, rs, query);
        String s = null;
        try {
            s = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jresultsparql.setText(s);
    }
}