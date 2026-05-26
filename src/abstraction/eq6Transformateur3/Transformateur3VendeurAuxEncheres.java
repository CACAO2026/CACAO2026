package abstraction.eq6Transformateur3;

import abstraction.eqXRomu.encheres.IVendeurAuxEncheres;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.awt.Color;

import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.encheres.Enchere;
import abstraction.eqXRomu.encheres.SuperviseurVentesAuxEncheres;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;
/** @author Le Clézio Brevael */  

public class Transformateur3VendeurAuxEncheres extends Transformateur3VendeurCCadre implements IVendeurAuxEncheres{

    private HashMap<ChocolatDeMarque, List<Double>> prixRetenus;
	private SuperviseurVentesAuxEncheres supEncheres;
	protected Journal journalEncheres;

    public Transformateur3VendeurAuxEncheres() {
        super();
        this.journalEncheres = new Journal(" journal Encheres Eq6", this);
    }

    public void initialiser() {
        super.initialiser();
        this.supEncheres = (SuperviseurVentesAuxEncheres)(Filiere.LA_FILIERE.getActeur("Sup.Encheres"));
        this.prixRetenus = new HashMap<ChocolatDeMarque, List<Double>>();
        for (IProduit p : this.stockchocomarque.keySet()) {
            if (p instanceof ChocolatDeMarque) {
                this.prixRetenus.put((ChocolatDeMarque) p, new LinkedList<Double>());
            }
        }
    }

    public void next() {
        super.next();
        this.journalEncheres.ajouter("Etape "+Filiere.LA_FILIERE.getEtape());
        for (IProduit p : this.stockchocomarque.keySet()) {
            if (p instanceof ChocolatDeMarque) {
                ChocolatDeMarque cm = (ChocolatDeMarque) p;
                
                double stockDispo = this.getStockProduit(cm);
                double engagement = totalEngagement(cm); // On déduit ce qu'on doit déjà livrer en Contrat Cadre
                double stockLibre = stockDispo - engagement;

                // Si on a un beau surplus, on en met 30% aux enchères
                if (stockLibre > 500.0) {
                    double quantiteAVendre = stockLibre * 0.30;
                    this.journalEncheres.ajouter("   Je mets aux enchères " + quantiteAVendre + " T de " + cm);
                    
                    // Lancement de l'enchère via le superviseur
                    Enchere enchereRetenue = supEncheres.vendreAuxEncheres(this, cryptogramme, cm, quantiteAVendre);

                    if (enchereRetenue != null) {
                        String acheteur = enchereRetenue.getAcheteur().getNom();
                        this.journalEncheres.ajouter(Color.GREEN, Color.BLACK,  "  SUCCÈS : Enchère remportée par " + acheteur + " à " + enchereRetenue.getPrixTonne() + " €/T !");
                        // Mise à jour de notre stock de chocolat
                        this.setStockProduit(cm, this.getStockProduit(cm) - quantiteAVendre);
                    } else {
                        this.journalEncheres.ajouter(Color.RED, Color.BLACK, "   ÉCHEC : Aucune offre n'a été retenue pour cette enchère.");
                    }
                }
            }
        }
    }

    public double prixMoyen(ChocolatDeMarque cm) {
        if (prixRetenus.get(cm).size()>0) {
            double somme = 0;
            for (double d : prixRetenus.get(cm)) {
                somme+=d;
            }
            return somme/prixRetenus.get(cm).size();
        } else {
            return 0;
        }
    }

    public Enchere choisir(List<Enchere> encheres) {
        if (encheres == null || encheres.isEmpty()) {
            return null;
        }

        // 1. Isoler l'offre la plus généreuse
        Enchere meilleureEnchere = encheres.get(0);
        for (Enchere e : encheres) {
            if (e.getPrixTonne() > meilleureEnchere.getPrixTonne()) {
                meilleureEnchere = e;
            }
        }
    

        // 2. Définir votre prix plancher de sécurité
        double prixPlancher = 11000.0; // Prix de base pour le MQ_E (Chocoenbien)
        if (meilleureEnchere.getMiseAuxEncheres().getProduit().toString().contains("HQ")) {
            prixPlancher = 18000.0; // Prix plus exigeant pour le HQ_E (Lamborghini)
        }

        // 3. Rendre le verdict
        if (meilleureEnchere.getPrixTonne() >= prixPlancher) {
            this.journalEncheres.ajouter("Succès : Enchère acceptée à " + meilleureEnchere.getPrixTonne() + " €/T");
        
            // Optionnel : sauvegarder le prix pour vos statistiques
            ChocolatDeMarque cm = (ChocolatDeMarque) meilleureEnchere.getMiseAuxEncheres().getProduit();
            this.prixRetenus.get(cm).add(meilleureEnchere.getPrixTonne());
        
            return meilleureEnchere;
        } else {
            this.journalEncheres.ajouter("Echec : Meilleure offre à " + meilleureEnchere.getPrixTonne() + " €/T (trop bas).");
            return null;
        }
    }

    public List<Journal> getJournaux() {
        List<Journal> j = super.getJournaux();
        j.add(this.journalEncheres);
        return j;
    }
}
