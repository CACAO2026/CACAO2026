package abstraction.eq1Producteur1;

import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.encheres.Enchere;
import abstraction.eqXRomu.encheres.IVendeurAuxEncheres;
import abstraction.eqXRomu.encheres.MiseAuxEncheres;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

/** 
 * @author Elise Dossal & Théophile Trillat
 */
public class Producteur1VendeurBourse extends Producteur1AcheteurBourse implements IVendeurAuxEncheres{
///*
    protected List<Enchere> propositions;

    public Producteur1VendeurBourse(){
        super();

    }

    public void next(){
        super.next();
        Feve feve = Feve.F_MQ  ;
        MiseAuxEncheres mise = new MiseAuxEncheres(this, feve , 120.0, true);


    }

    public Enchere choisir(List<Enchere> propositions){
        return this.propositions.get(0);

    }
//*/
}