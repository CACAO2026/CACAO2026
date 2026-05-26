package abstraction.eq7Transformateur4;

import abstraction.eqXRomu.appelDOffre.AppelDOffre;
import abstraction.eqXRomu.appelDOffre.IVendeurAO;
import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;


//Auteur : Paul

public class Transformateur4VendeurAppelDOffre extends Transformateur4VendeurAuxEncheres implements IVendeurAO{

    public Transformateur4VendeurAppelDOffre() {
        super();
    }

	public OffreVente proposerVente(AppelDOffre offre) {
        IProduit p = offre.getProduit();
        
        if (!(p instanceof ChocolatDeMarque)) {
            return null;
        }
        
        ChocolatDeMarque cdm = (ChocolatDeMarque) p;
        String marque = cdm.getMarque().toLowerCase();

        if (!marque.contains("cacao+")) {
            return null;
        }
        double quantiteT= offre.getQuantiteT();
        double stockDispo = this.get_StockChoco_BQ().getValeur()+this.get_StockChoco_MQ().getValeur()+this.get_StockChoco_HQ().getValeur();
        if (stockDispo < offre.getQuantiteT()) {
            quantiteT = stockDispo/1.5; 
        }

        BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));

        AppelDOffre nouv_offre = new AppelDOffre(offre.getAcheteur(),offre.getProduit(), quantiteT, offre.getTeteGondole() );
        if (offre.getAcheteur().getNom().equals("EQXD")){
            this.journal_vente_AO.ajouter("Proposition de vente de "+offre.getQuantiteT()+" T de "+cdm+" à "+bourse.getCours(Feve.F_BQ).getValeur()*1.75+" €/T par "+offre.getAcheteur().getNom());
            return new OffreVente(nouv_offre, this, cdm, bourse.getCours(Feve.F_BQ).getValeur()*1.74);
        }
        else {
            this.journal_vente_AO.ajouter("Proposition de vente de "+offre.getQuantiteT()+" T de "+cdm+" à "+6000+" €/T par "+offre.getAcheteur().getNom());
            return new OffreVente(nouv_offre, this, cdm, 6000);
        }
    }

			


	public void notifierVenteAO(OffreVente propositionRetenue){
        this.get_StockChoco_BQ().retirer(this, propositionRetenue.getQuantiteT());
        this.journal_vente_AO.ajouter("[OFFRE RETENUE] Retenue de l'offre: "+propositionRetenue.toString()+ "\n");
    }

	public void notifierPropositionNonRetenueAO(OffreVente propositionRefusee){
        this.journal_vente_AO.ajouter("[OFFRE REFUSEE] Refus de l'offre: "+propositionRefusee.toString()+ "\n");
    }
}