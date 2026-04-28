package abstraction.eq9Distributeur2;

import java.util.HashMap;
import java.util.Map;
import abstraction.eqXRomu.general.Journal;

/**
 * Représente la marque propre EQ9 : "EQ9"
 * 3 gammes : HAUTE, MOYENNE, BASSE
 * Marge : 35% (vs 15-20% pour revendication)
 * Coût achat : 120€/T ? **a vérifier**
 * 
 * @author Paul JUHEL
 */
public class EQ9_MarquePrivee {
    
    public static final String NOM_MARQUE = "EQ9";
    public static final double COÛT_ACHAT_EURO_PAR_TONNE = 120.0;
    public static final double MARGE_PERCENT = 35.0;
    public static final double PRIX_VENTE_EURO_PAR_TONNE = COÛT_ACHAT_EURO_PAR_TONNE * (1.0 + MARGE_PERCENT / 100.0);  // 162€/T
    
    private Journal journal;
    
    // Produits de marque propre
    private Map<String, ProduitMarquePrivee> produits = new HashMap<>();
    
    public EQ9_MarquePrivee(Journal j) {
        this.journal = j;
        initialiserProduits();
    }
    
    /**
     * Crée les 3 produits de marque propre
     */
    private void initialiserProduits() {
        // Gamme HAUTE
        produits.put("Delice", new ProduitMarquePrivee(
            "EQ9 DÉLICE",
            "HAUTE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        // Gamme MOYENNE
        produits.put("Standard", new ProduitMarquePrivee(
            "EQ9 STANDARD",
            "MOYENNE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        // Gamme BASSE
        produits.put("Eco", new ProduitMarquePrivee(
            "EQ9 ÉCO",
            "BASSE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        journal.ajouter("Marque propre " + NOM_MARQUE + " initialisée (" + 
                       produits.size() + " gammes)");
    }
    
    public double obtenirPrixVente() {
        return PRIX_VENTE_EURO_PAR_TONNE;
    }
    
    public double obtenirCoûtAchat() {
        return COÛT_ACHAT_EURO_PAR_TONNE;
    }
    
    public double obtenirMargePercent() {
        return MARGE_PERCENT;
    }
    
    public double calculerProfitUnitaire() {
        return PRIX_VENTE_EURO_PAR_TONNE - COÛT_ACHAT_EURO_PAR_TONNE;  // 42€/T
    }
    
    public double calculerProfitTotal(double volumeTonnes) {
        return volumeTonnes * calculerProfitUnitaire();
    }
    
    public ProduitMarquePrivee obtenirProduit(String cleProduit) {
        return produits.get(cleProduit);
    }
    
    public static class ProduitMarquePrivee {
        public String nom;
        public String gamme;
        public double coûtAchatEuroT;
        public double prixVenteEuroT;
        public double margePercent;
        public double stockKg = 0;
        
        public ProduitMarquePrivee(String n, String g, double c, double p, double m) {
            this.nom = n;
            this.gamme = g;
            this.coûtAchatEuroT = c;
            this.prixVenteEuroT = p;
            this.margePercent = m;
        }
        
        @Override
        public String toString() {
            return nom + " (" + gamme + ")";
        }
    }
}
