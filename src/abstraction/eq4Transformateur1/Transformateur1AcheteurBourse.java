
/**@author Ewan Lefort */

package abstraction.eq4Transformateur1;

import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;

public class Transformateur1AcheteurBourse extends Transformateur1Acteur implements IAcheteurBourse {
    public void notificationBlackList(int dureeEnStep){

    }
    public void notificationAchat(Feve f, double quantiteEnT, double coursEnEuroParT){
        this.getStock().setStockFeves(f, quantiteEnT);
    }
    public double demande(Feve f, double cours){
        if (f.getGamme()==Gamme.MQ){
            return 80;
        }
        else{
            return 0;
        }
    }
}
