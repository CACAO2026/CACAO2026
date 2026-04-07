package abstraction.eq5Transformateur2;

import java.applet.Applet;
import java.awt.Color;
import java.util.List;

import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Chocolat; 
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.bourseCacao.BourseCacao;


/**
 * @author Pierre GUTTIEREZ
 */
 
public class Transformateur2VendeurCC extends Transformateur2ProductionChocolat implements IVendeurContratCadre{

    public Transformateur2VendeurCC() {
        super();
    }

	public boolean vend(IProduit produit){
		if (produit instanceof Chocolat){
			Chocolat c = (Chocolat) produit;
			if (c.isEquitable()){
				return false;
			} else {
			return true;
			}
		} else {
			return false;
		}
	}
	

	public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat){
		return contrat.getEcheancier();
	}
	
	public double propositionPrix(ExemplaireContratCadre contrat){
		return 1.35*contrat.getQuantiteTotale()*(prix_MP+2);
	}

	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat){
		return ((contrat.getPrix() + contrat.getListePrix().get(contrat.getListePrix().size() - 2))/2)*1.20;
	}
	

	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat){
		this.getJournaux().get(4).ajouter(contrat.toString()+ "\n");
	}

	public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat){
		if (produit instanceof Chocolat){
			Chocolat c = (Chocolat) produit;
			Feve F;
			if (c == Chocolat.C_BQ){
				F = Feve.F_BQ;
			} if (c == Chocolat.C_MQ) {
				F = Feve.F_MQ;
			} else {
				F = Feve.F_HQ;
			}
			if (this.getStock_chocolat(produit) >= quantite){
				this.remove_chocolat(quantite, c);
				return quantite;
			} else {
				this.ProductionChocolat(F,0.45,quantite - this.getStock_chocolat(produit));
			this.remove_chocolat(this.getStock_chocolat(produit), c);
			return quantite;
			}
		} else {
			return 0;
		}
	}
}