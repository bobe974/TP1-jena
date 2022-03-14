import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;

public class Vcard{

    private String nom, prenom, fullname;

    public Vcard(String nom, String prenom){
        this.nom = nom;
        this.prenom = prenom;
        fullname = this.prenom+ "" +this.nom;

    }

    public String getNom(){
        return this.nom;
    }

    public String getprenom(){
        return this.prenom;
    }
    public String getfullName(){
        return this.fullname;
    }




}