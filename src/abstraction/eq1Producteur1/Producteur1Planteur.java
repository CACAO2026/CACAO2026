package abstraction.eq1Producteur1;
import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

/**
 * @author Elise Dossal
 */

public class Producteur1Planteur extends Producteur1Stock{

    private List<Plantation> plantations = new ArrayList<Plantation>();
    private double taille_totale=10000;

    public Producteur1Planteur(){
        super();
        // initialiser les plantation du début
    }

    public double getTaille(){
        return this. taille_totale;
    }

    public void planter(Feve f, double taille){

    }


    public void couper(int i){
        plantations.remove(i);
    }


    public void collecter(){ //On crée un lot de chauqe qualité qui regroupe plusieurs plantations pour ne pas avoir des lots qui ne différent que par la quantité
        double lot_HQ = 0;
        double lot_HQ_E = 0;
        double lot_MQ = 0;
        double lot_MQ_E = 0;
        double lot_BQ = 0;
        double lot_BQ_E = 0;

        for(int i=0; i<this.plantations.size(); i++){
            Plantation plantation = this.plantations.get(i);
            if(plantation.getEtat() == 10){ //vérifie si les arbres ne sont pas morts, sinon les coupe
                this.couper(i);
            }
            double cacao = plantation.collecte();
            Feve gamme = plantation.getGamme();

            if(gamme == Feve.F_HQ){
                lot_HQ += cacao;
            }

            if(gamme == Feve.F_HQ_E){
                lot_HQ_E += cacao;
            }

            if(gamme == Feve.F_MQ){
                lot_MQ += cacao;
            }

            if(gamme == Feve.F_MQ_E){
                lot_MQ_E += cacao;
            }

            if(gamme == Feve.F_BQ){
                lot_BQ += cacao;
            }

            if(gamme == Feve.F_BQ_E){
                lot_BQ_E += cacao;
            }
        }



        this.add_lot(Feve.F_HQ, lot_HQ);
        this.add_lot(Feve.F_HQ_E, lot_HQ_E);
        this.add_lot(Feve.F_MQ, lot_MQ);
        this.add_lot(Feve.F_MQ_E, lot_MQ_E);
        this.add_lot(Feve.F_BQ, lot_BQ);
        this.add_lot(Feve.F_BQ_E, lot_BQ_E);

    }

    public void next(){
        super.next();
        int etape = Filiere.LA_FILIERE.getEtape();
        if(etape%24 == 0){ //Une collecte tous les ans, a une dâte arbitraire pour l'instant
            this.collecter();
        }
    }

}
