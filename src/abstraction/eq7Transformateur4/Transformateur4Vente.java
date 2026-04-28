package abstraction.eq7Transformateur4;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

//Auteur -> Aymeric et Paul
public class Transformateur4Vente extends Transformateur4Production implements IVendeurContratCadre {

    @Override
    public boolean vend(IProduit produit) {
        return this.getChocolatsProduits().contains(produit);
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        // TODO Auto-generated method stub
        return contrat.getEcheancier();//echeance;
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        // TODO Auto-generated method stub
        return 0.;
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        // TODO Auto-generated method stub
        return 0.;
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        //TODO Auto-generated method stub
    
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        // TODO Auto-generated method stub
        return 0.;
    }
    
}