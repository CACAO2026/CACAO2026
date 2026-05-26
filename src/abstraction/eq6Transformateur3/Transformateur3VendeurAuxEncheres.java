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
        
        // Vérification que le superviseur est bien initialisé
        if (this.supEncheres == null) {
            this.journalEncheres.ajouter(Color.RED, Color.BLACK, "ERREUR : Superviseur des enchères non initialisé !");
            return;
        }
        
        for (IProduit p : this.stockchocomarque.keySet()) {
            if (p instanceof ChocolatDeMarque) {
                ChocolatDeMarque cm = (ChocolatDeMarque) p;
                
                double stockDispo = this.getStockProduit(cm);
                double engagement = totalEngagement(cm); // On déduit ce qu'on doit déjà livrer en Contrat Cadre
                double stockLibre = stockDispo - engagement;
                
                this.journalEncheres.ajouter("   " + cm + " : stock dispo=" + stockDispo + " T, engagement=" + engagement + " T, libre=" + stockLibre + " T");
                
                double quantiteAVendre = 0.0;

                if (cm.equals(LamborghiniduCacao) && stockLibre > 200.0) {
                    quantiteAVendre = stockLibre * 0.25; 
                }
                else if (cm.equals(Chocoenbien) && stockLibre > 1000.0) {
                    quantiteAVendre = stockLibre * 0.15; 
                }
                if (quantiteAVendre > 0.0) {
                    this.journalEncheres.ajouter("   Je mets aux enchères " + quantiteAVendre + " T de " + cm);
                    try {
                        Enchere enchereRetenue = supEncheres.vendreAuxEncheres(this, cryptogramme, cm, quantiteAVendre);

                        if (enchereRetenue != null) {
                            String acheteur = enchereRetenue.getAcheteur().getNom();
                            this.journalEncheres.ajouter(Color.GREEN, Color.BLACK,  "  SUCCÈS : Enchère remportée par " + acheteur + " à " + enchereRetenue.getPrixTonne() + " €/T !");
                            this.setStockProduit(cm, this.getStockProduit(cm) - quantiteAVendre);
                            this.Eq6TotalStock.retirer(this, quantiteAVendre, this.cryptogramme);
                        } else {
                            this.journalEncheres.ajouter(Color.RED, Color.BLACK, "   ÉCHEC : Aucune offre n'a été retenue pour cette enchère.");
                        }
                    } catch (Exception e) {
                        this.journalEncheres.ajouter(Color.RED, Color.BLACK, "   ERREUR lors de la mise aux enchères : " + e.getMessage());
                        e.printStackTrace();
                    }
                } 
                else {
                    this.journalEncheres.ajouter("   Stock insuffisant ou non prioritaire pour mettre aux enchères (" + stockLibre + " T libres)");
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
            this.journalEncheres.ajouter("Participation aux enchères : aucune enchère disponible");
            return null;
        }

        this.journalEncheres.ajouter("Participation aux enchères : " + encheres.size() + " offre(s) reçue(s)");
        
        // 1. Isoler l'offre la plus généreuse
        Enchere meilleureEnchere = encheres.get(0);
        for (Enchere e : encheres) {
            if (e.getPrixTonne() > meilleureEnchere.getPrixTonne()) {
                meilleureEnchere = e;
            }
        }
        
        this.journalEncheres.ajouter("  Meilleure offre : " + meilleureEnchere.getPrixTonne() + " €/T de " + meilleureEnchere.getVendeur().getNom() + " pour " + meilleureEnchere.getMiseAuxEncheres().getProduit());

        // 2. Définir prix plancher de sécurité
        BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
        double coursMQ = bourse.getCours(Feve.F_MQ).getValeur();
        
        // On demande environ le double du prix de la fève (pour couvrir transport, transfo et marge)
        double prixPlancher = coursMQ * 2.5; 
        
        if (meilleureEnchere.getMiseAuxEncheres().getProduit().toString().contains("HQ")) {
            prixPlancher = coursMQ * 3.5; // Marge plus forte pour le HQ
        }

        ChocolatDeMarque cm = (ChocolatDeMarque) meilleureEnchere.getMiseAuxEncheres().getProduit();
        if (this.getStockProduit(cm) > 5000) {
            prixPlancher = prixPlancher * 0.75; // Rabais de 25% en cas de surstock
            this.journalEncheres.ajouter("   Alerte Surstock : Prix plancher bradé à " + prixPlancher);
        }

        if (meilleureEnchere.getPrixTonne() >= prixPlancher) {
            this.journalEncheres.ajouter(Color.GREEN, Color.BLACK, "ACHAT : Enchère acceptée à " + meilleureEnchere.getPrixTonne() + " €/T");
        
            this.prixRetenus.get(cm).add(meilleureEnchere.getPrixTonne());
        
            return meilleureEnchere;
        } else {
            this.journalEncheres.ajouter(Color.RED, Color.BLACK, "REFUS : Meilleure offre à " + meilleureEnchere.getPrixTonne() + " €/T (prix plancher : " + prixPlancher + " €/T)");
            return null;
        }
    }

    public List<Journal> getJournaux() {
        List<Journal> j = super.getJournaux();
        j.add(this.journalEncheres);
        return j;
    }
}
