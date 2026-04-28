# 💻 Recueil de Codes V2 - Tous en Français

*Implémentation détaillée des classes pour V2 EQ9*

---

## 📑 Table des matières

1. Classe `EQ9_StrategieFixationPrix.java`
2. Classe `EQ9_GestionnaireMarques.java`
3. Classe `EQ9_MarquePrivee.java`
4. Modifications `Distributeur2Acteur.java`
5. Modifications `Distributeur2AcheteurCC.java`

---

## 1️⃣ Classe : EQ9_StrategieFixationPrix.java

**Fichier** : `/src/abstraction/eq9Distributeur2/EQ9_StrategieFixationPrix.java`

```java
package abstraction.eq9Distributeur2;

import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.general.Journal;

/**
 * Gère la fixation dynamique des prix de vente EQ9
 * Formule : Prix = Coût × Marge × Facteur_Demande × Facteur_Inventaire × Facteur_Compétition
 * 
 * @author Équipe EQ9 (Paul JUHEL, Anass OUISRANI, Paul ROSSIGNOL)
 * @version V2 - 28/04/2026
 */
public class EQ9_StrategieFixationPrix {
    
    private Journal journal;
    
    // Marges de base par gamme de produit
    private static final double MARGE_GAMME_BASSE = 12.0;      // Compétition serrée
    private static final double MARGE_GAMME_MOYENNE = 18.0;    // Équilibré
    private static final double MARGE_GAMME_HAUTE = 25.0;      // Moins d'élasticité
    private static final double MARGE_MARQUE_PROPRE = 35.0;    // Économies production
    
    // Seuils d'inventaire
    private static final double OBJECTIF_STOCK_KG = 50000.0;   // 50 tonnes cible
    private static final double SEUIL_ALERTE_BAS = 10000.0;    // 10 tonnes minimum
    private static final double SEUIL_SURSTOCK = 75000.0;      // 75 tonnes = surstock
    
    public EQ9_StrategieFixationPrix(Journal j) {
        this.journal = j;
    }
    
    /**
     * Calcule le prix de vente optimal pour un produit chocolaté
     * 
     * @param coutAchatEuroPT Coût d'achat en €/tonne
     * @param nomProduit Nom du produit (identifie marque)
     * @param inventaireKg Stock actuel en kg
     * @param demandeClients Demande clients estimée pour ce produit
     * @param prixConcurrentEuro Prix du concurrent direct (même gamme/marque)
     * 
     * @return Prix de vente recommandé en €/tonne
     */
    public double calculerPrixVente(
        double coutAchatEuroPT,
        String nomProduit,
        double inventaireKg,
        double demandeClients,
        double prixConcurrentEuro
    ) {
        // 1. DÉTERMINER LA MARGE DE BASE
        double margePercent = obtenirMargeBasePourProduit(nomProduit);
        double prixBase = coutAchatEuroPT * (1.0 + margePercent / 100.0);
        
        // 2. FACTEUR DEMANDE/OFFRE
        double offre = inventaireKg;
        double ratioDemandeOffre = (offre > 0) ? demandeClients / offre : 2.0;
        double facteurDemande = obtenirFacteurDemande(ratioDemandeOffre);
        
        // 3. FACTEUR INVENTAIRE
        double facteurInventaire = obtenirFacteurInventaire(inventaireKg);
        
        // 4. FACTEUR COMPÉTITION DIRECTE
        double facteurCompetition = obtenirFacteurCompetition(
            prixBase * facteurDemande * facteurInventaire, 
            prixConcurrentEuro
        );
        
        // 5. CALCUL FINAL
        double prixFinal = prixBase * facteurDemande * facteurInventaire * facteurCompetition;
        
        // LOG
        journal.ajouter("PRIX - " + nomProduit + " : " +
                       String.format("%.0f€/T", prixFinal) +
                       " (coût " + String.format("%.0f€", coutAchatEuroPT) +
                       ", ratio D/O %.2f, stock %.0fT)", 
                       ratioDemandeOffre, inventaireKg / 1000.0);
        
        return prixFinal;
    }
    
    /**
     * Détermine la marge de base selon le type de produit
     */
    private double obtenirMargeBasePourProduit(String nomProduit) {
        // Marque propre Carrefour = marge maximale
        if (nomProduit.contains("Carrefour") || nomProduit.contains("Sélection")) {
            return MARGE_MARQUE_PROPRE;  // 35%
        }
        
        // Heuristique gammes (à adapter selon structure réelle)
        if (nomProduit.contains("DÉLICE") || nomProduit.contains("Premium") 
            || nomProduit.contains("Deluxe")) {
            return MARGE_GAMME_HAUTE;    // 25%
        }
        
        if (nomProduit.contains("ÉCO") || nomProduit.contains("Budget") 
            || nomProduit.contains("Budget")) {
            return MARGE_GAMME_BASSE;    // 12%
        }
        
        // Par défaut = moyenne
        return MARGE_GAMME_MOYENNE;      // 18%
    }
    
    /**
     * Calcule le facteur demande/offre
     * 
     * Ratio = Demande / Offre
     *   > 1.5 : forte demande → prix haut
     *   1.0-1.5 : équilibre normal
     *   0.7-1.0 : légère suroffre → prix réduit
     *   < 0.7 : suroffre massive → prix très réduit
     */
    private double obtenirFacteurDemande(double ratio) {
        if (ratio > 1.5) {
            return 1.20;  // +20% prix (forte demande)
        } else if (ratio > 1.0) {
            return 1.0 + (ratio - 1.0) * 0.2;  // Interpolation +0% à +20%
        } else if (ratio > 0.7) {
            return 1.0 - (1.0 - ratio) * 0.05; // Interpolation -5%
        } else {
            return 0.85;  // -15% prix (surstock)
        }
    }
    
    /**
     * Calcule le facteur inventaire
     * Pénalise ou récompense selon stock vs objectif
     */
    private double obtenirFacteurInventaire(double stockKg) {
        if (stockKg > SEUIL_SURSTOCK) {
            // SURSTOCK massif → urgence déstockage
            return 0.90;  // -10% prix
        } else if (stockKg > OBJECTIF_STOCK_KG * 1.5) {
            // Surstock modéré
            return 0.95;  // -5% prix
        } else if (stockKg < SEUIL_ALERTE_BAS * 0.5) {
            // RUPTURE IMMINENTE → urgence réappro
            return 1.10;  // +10% prix
        } else if (stockKg < SEUIL_ALERTE_BAS) {
            // Seuil de sécurité atteint
            return 1.05;  // +5% prix
        } else {
            // Stock normal = pas d'ajustement
            return 1.0;
        }
    }
    
    /**
     * Calcule le facteur compétition
     * Compare notre prix avec concurrent direct
     */
    private double obtenirFacteurCompetition(double noPrix, double prixConcurrent) {
        if (prixConcurrent <= 0) {
            // Pas de concurrent identifié = on peut garder notre prix
            return 1.0;
        }
        
        double ratio = noPrix / prixConcurrent;
        
        if (ratio > 1.05) {
            // Nous sommes 5% plus chers → réduire pour rester compétitif
            return 0.98;  // -2%
        } else if (ratio > 1.02) {
            // Nous sommes 2% plus chers
            return 0.99;  // -1%
        } else {
            // Nous sommes compétitifs
            return 1.0;
        }
    }
    
    /**
     * Retourne la marge standard pour une gamme
     * Utile pour le monitoring/reporting
     */
    public static double getMargeStandard(String gamme) {
        switch (gamme.toUpperCase()) {
            case "BASSE":
                return MARGE_GAMME_BASSE;
            case "MOYENNE":
                return MARGE_GAMME_MOYENNE;
            case "HAUTE":
                return MARGE_GAMME_HAUTE;
            case "PROPRE":
                return MARGE_MARQUE_PROPRE;
            default:
                return MARGE_GAMME_MOYENNE;
        }
    }
}
```

---

## 2️⃣ Classe : EQ9_GestionnaireMarques.java

**Fichier** : `/src/abstraction/eq9Distributeur2/EQ9_GestionnaireMarques.java`

```java
package abstraction.eq9Distributeur2;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;

/**
 * Gère les différentes marques et leurs spécificités
 * Chaque transformateur a sa marque
 * EQ9 a sa marque propre Carrefour Sélection
 * 
 * @author Équipe EQ9
 * @version V2 - 28/04/2026
 */
public class EQ9_GestionnaireMarques {
    
    private Journal journal;
    
    // Marques reconnaissables par transformateur
    private Map<String, MarqueTransformateur> marques = new HashMap<>();
    
    // Stock par marque
    private Map<String, Double> stockParMarque = new HashMap<>();
    
    // Prix de vente par marque
    private Map<String, Double> prixParMarque = new HashMap<>();
    
    // Demande estimée par marque
    private Map<String, Double> demandeParMarque = new HashMap<>();
    
    public EQ9_GestionnaireMarques(Journal j) {
        this.journal = j;
        initialiserMarques();
    }
    
    /**
     * Initialise les marques de transformateurs connues
     */
    private void initialiserMarques() {
        // EQ4 - Prontella
        marques.put("Prontella", new MarqueTransformateur(
            "Prontella",
            "EQ4",
            0.20  // 20% de la demande marché
        ));
        
        // EQ5 - ChocolatBrand
        marques.put("ChocolatBrand", new MarqueTransformateur(
            "ChocolatBrand",
            "EQ5",
            0.30  // 30%
        ));
        
        // EQ6 - (à définir)
        marques.put("Marque_EQ6", new MarqueTransformateur(
            "Marque_EQ6",
            "EQ6",
            0.15  // 15%
        ));
        
        // EQ7 - (à définir)
        marques.put("Marque_EQ7", new MarqueTransformateur(
            "Marque_EQ7",
            "EQ7",
            0.10  // 10%
        ));
        
        // EQ9 - Carrefour Sélection (NOTRE MARQUE PROPRE)
        marques.put("CarrefourSelection", new MarqueTransformateur(
            "Carrefour Sélection",
            "EQ9",
            0.25  // 25% - notre part du marché
        ));
        
        journal.ajouter("Gestionnaire marques initialisé : " + marques.size() + " marques");
    }
    
    /**
     * Obtient les marques disponibles
     */
    public List<String> obtenirNomMarques() {
        return new ArrayList<>(marques.keySet());
    }
    
    /**
     * Enregistre le stock pour une marque
     */
    public void mettreAJourStock(String nomMarque, double quantiteKg) {
        if (marques.containsKey(nomMarque)) {
            stockParMarque.put(nomMarque, quantiteKg);
        } else {
            journal.ajouter("⚠️ Marque inconnue : " + nomMarque);
        }
    }
    
    /**
     * Enregistre le prix de vente pour une marque
     */
    public void mettreAJourPrix(String nomMarque, double prixEuroParTonne) {
        if (marques.containsKey(nomMarque)) {
            prixParMarque.put(nomMarque, prixEuroParTonne);
        }
    }
    
    /**
     * Enregistre la demande estimée pour une marque
     */
    public void mettreAJourDemande(String nomMarque, double demandeEstimeeKg) {
        if (marques.containsKey(nomMarque)) {
            demandeParMarque.put(nomMarque, demandeEstimeeKg);
        }
    }
    
    /**
     * Retourne le stock pour une marque
     */
    public double obtenirStock(String nomMarque) {
        return stockParMarque.getOrDefault(nomMarque, 0.0);
    }
    
    /**
     * Retourne la demande pour une marque
     */
    public double obtenirDemande(String nomMarque) {
        return demandeParMarque.getOrDefault(nomMarque, 0.0);
    }
    
    /**
     * Retourne la part de marché (%) pour une marque
     */
    public double obtenirPartMarche(String nomMarque) {
        MarqueTransformateur marque = marques.get(nomMarque);
        return (marque != null) ? marque.partMarchePercent : 0;
    }
    
    /**
     * Retourne le stock total toutes marques
     */
    public double obtenirStockTotal() {
        return stockParMarque.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
    
    /**
     * Rapport : composition stock par marque
     */
    public void afficherCompositionStock() {
        double total = obtenirStockTotal();
        journal.ajouter("=== COMPOSITION STOCK EQ9 ===");
        
        for (String marque : marques.keySet()) {
            double stock = obtenirStock(marque);
            double pct = (total > 0) ? (stock / total) * 100 : 0;
            journal.ajouter("  " + marque + " : " + 
                           String.format("%.0f", stock/1000) + "T (" + 
                           String.format("%.1f%%", pct) + ")");
        }
        
        journal.ajouter("  TOTAL : " + String.format("%.0f", total/1000) + "T");
    }
    
    /**
     * Classe interne : représente une marque
     */
    private static class MarqueTransformateur {
        String nom;
        String transformateur;  // EQ4, EQ5, etc.
        double partMarchePercent;  // % de la demande totale que cette marque représente
        
        public MarqueTransformateur(String n, String t, double p) {
            this.nom = n;
            this.transformateur = t;
            this.partMarchePercent = p;
        }
    }
}
```

---

## 3️⃣ Classe : EQ9_MarquePrivee.java

**Fichier** : `/src/abstraction/eq9Distributeur2/EQ9_MarquePrivee.java`

```java
package abstraction.eq9Distributeur2;

import java.util.HashMap;
import java.util.Map;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.general.Journal;

/**
 * Représente la marque propre EQ9 : "Carrefour Sélection"
 * 3 gammes : Délice (HAUTE), Standard (MOYENNE), Éco (BASSE)
 * Marge : 35% (vs 15-20% pour revendication)
 * Coût achat : 120€/T (accord exclusif EQ4)
 * 
 * @author Équipe EQ9
 * @version V2 - 28/04/2026
 */
public class EQ9_MarquePrivee {
    
    public static final String NOM_MARQUE = "Carrefour Sélection";
    public static final double COÛT_ACHAT_EURO_PAR_TONNE = 120.0;
    public static final double MARGE_PERCENT = 35.0;
    public static final double PRIX_VENTE_EURO_PAR_TONNE = 
        COÛT_ACHAT_EURO_PAR_TONNE * (1.0 + MARGE_PERCENT / 100.0);  // 162€/T
    
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
        // Carrefour Sélection DÉLICE (Gamme HAUTE)
        produits.put("Délice", new ProduitMarquePrivee(
            "Carrefour Sélection DÉLICE",
            "HAUTE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        // Carrefour Sélection STANDARD (Gamme MOYENNE)
        produits.put("Standard", new ProduitMarquePrivee(
            "Carrefour Sélection STANDARD",
            "MOYENNE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        // Carrefour Sélection ÉCO (Gamme BASSE)
        produits.put("Éco", new ProduitMarquePrivee(
            "Carrefour Sélection ÉCO",
            "BASSE",
            COÛT_ACHAT_EURO_PAR_TONNE,
            PRIX_VENTE_EURO_PAR_TONNE,
            MARGE_PERCENT
        ));
        
        journal.ajouter("Marque propre " + NOM_MARQUE + " initialisée (" + 
                       produits.size() + " gammes)");
    }
    
    /**
     * Retourne le prix unitaire de vente
     */
    public double obtenirPrixVente() {
        return PRIX_VENTE_EURO_PAR_TONNE;
    }
    
    /**
     * Retourne le coût unitaire
     */
    public double obtenirCoûtAchat() {
        return COÛT_ACHAT_EURO_PAR_TONNE;
    }
    
    /**
     * Retourne la marge (%)
     */
    public double obtenirMargePercent() {
        return MARGE_PERCENT;
    }
    
    /**
     * Vérifie si un nom de produit appartient à notre marque
     */
    public boolean estMarquePrivee(String nomProduit) {
        return nomProduit.contains("Carrefour") || 
               nomProduit.contains("Sélection") ||
               nomProduit.contains("Selection");
    }
    
    /**
     * Calcule le profit unitaire
     */
    public double calculerProfitUnitaire() {
        return PRIX_VENTE_EURO_PAR_TONNE - COÛT_ACHAT_EURO_PAR_TONNE;  // 42€/T
    }
    
    /**
     * Calcule le profit total pour un volume
     */
    public double calculerProfitTotal(double volumeTonnes) {
        return volumeTonnes * calculerProfitUnitaire();
    }
    
    /**
     * Classe interne : représente un produit marque propre
     */
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
        
        public String toString() {
            return nom + " (" + gamme + ")";
        }
    }
}
```

---

## 4️⃣ Modifications : Distributeur2Acteur.java

**Sections à ajouter/modifier** :

```java
// ==================== NOUVELLES IMPORTATIONS ====================
import abstraction.eq9Distributeur2.EQ9_StrategieFixationPrix;
import abstraction.eq9Distributeur2.EQ9_GestionnaireMarques;
import abstraction.eq9Distributeur2.EQ9_MarquePrivee;


// ==================== NOUVEAUX ATTRIBUTS ====================
public class Distributeur2Acteur implements IActeur, IDistributeurChocolatDeMarque {
    
    // ... attributs existants ...
    
    // --- V2 : Gestion stratégique ---
    protected EQ9_StrategieFixationPrix strategieFixationPrix;
    protected EQ9_GestionnaireMarques gestionnaireMarques;
    protected EQ9_MarquePrivee marquePrivee;
    
    // Indicateurs V2
    protected Variable indicateurMargeMoyenne;
    protected Variable indicateurMixMarquePrivee;
    protected Variable indicateurProfitBrutEtape;


// ==================== INITIALISATION (initialiser) ====================
@Override
public void initialiser() {
    // ... appels super() et code existant ...
    
    // NOUVEAU V2
    this.strategieFixationPrix = new EQ9_StrategieFixationPrix(journal);
    this.gestionnaireMarques = new EQ9_GestionnaireMarques(journal);
    this.marquePrivee = new EQ9_MarquePrivee(journal);
    
    // Initialiser stocks marque propre (40% du total)
    double stockMarquePriveeParGamme = getStockTotal() / 1000.0 * 0.40 / 3;
    
    this.indicateurMargeMoyenne = new Variable("EQ9_marge_moyenne", this, 18.0);
    this.indicateurMixMarquePrivee = new Variable("EQ9_pct_marque_privee", this, 40.0);
    this.indicateurProfitBrutEtape = new Variable("EQ9_profit_brut", this, 0.0);
    
    journal.ajouter("V2 : Stratégie fixation prix activée");
    journal.ajouter("V2 : Gestionnaire marques initialisé");
    journal.ajouter("V2 : Marque propre Carrefour Sélection activée");
}


// ==================== CYCLE (next) ====================
// Ajouter après paiement frais de stockage (avant fin de next())

@Override
public void next() {
    int etape = Filiere.LA_FILIERE.getEtape();
    
    // ... code réapprovisionnement et frais stockage existants ...
    
    // --- NOUVEAU V2 : Ajustement dynamique prix ---
    ajusterPrixDynamiques();
    
    // --- NOUVEAU V2 : Rapports stratégiques ---
    afficherRapportStrategique();
}


// ==================== NOUVELLES MÉTHODES ====================

/**
 * Ajuste dynamiquement les prix de chaque produit
 * basé sur demande/offre et stratégie
 */
private void ajusterPrixDynamiques() {
    int etape = Filiere.LA_FILIERE.getEtape();
    
    List<ChocolatDeMarque> produits = Filiere.LA_FILIERE.getChocolatsProduits();
    
    if (produits != null) {
        double profitBrutTotal = 0;
        
        for (ChocolatDeMarque choco : produits) {
            // Obtenir infos produit
            double coutAchat = obtenirCoutAchat(choco);  // À implémenter
            double stock = this.stock.getOrDefault(choco, 0.0);
            double demande = estimerDemandeClients(choco);  // À implémenter
            double prixConcurrent = estimerPrixConcurrent(choco);  // À implémenter
            
            // Calculer prix optimal
            double prixOptimal = strategieFixationPrix.calculerPrixVente(
                coutAchat,
                choco.getNom(),
                stock,
                demande,
                prixConcurrent
            );
            
            // Mettre à jour prix
            this.prix.put(choco, prixOptimal);
            
            // Accumuler profit brut
            profitBrutTotal += (prixOptimal - coutAchat) * (stock / 1000.0);
        }
        
        // Mettre à jour indicateurs
        this.indicateurProfitBrutEtape.setValeur(this, profitBrutTotal);
    }
}

/**
 * Affiche rapport stratégique détaillé
 */
private void afficherRapportStrategique() {
    journal.ajouter("=== RAPPORT STRATÉGIQUE V2 ===");
    
    // Composition stock marques
    gestionnaireMarques.afficherCompositionStock();
    
    // Info marque propre
    double profitUnitaireCarrefour = marquePrivee.calculerProfitUnitaire();
    journal.ajouter("Profit unitaire marque propre : " + 
                   String.format("%.0f€", profitUnitaireCarrefour) + "/T");
}

/**
 * À implémenter : obtenir coût d'achat pour un produit
 * (à partir contrat cadre ou historique achat)
 */
private double obtenirCoutAchat(ChocolatDeMarque produit) {
    // TODO : implémenter selon contrats cadre réels
    // Par défaut : estimation
    if (produit.getNom().contains("Carrefour")) {
        return EQ9_MarquePrivee.COÛT_ACHAT_EURO_PAR_TONNE;
    }
    return 150.0;  // Estimation moyenne transformateurs
}

/**
 * À implémenter : estimer demande clients pour un produit
 */
private double estimerDemandeClients(ChocolatDeMarque produit) {
    // TODO : implémenter à partir historique ventes/Filière
    return 50000.0;  // Estimation : 50T/étape
}

/**
 * À implémenter : estimer prix concurrent direct
 */
private double estimerPrixConcurrent(ChocolatDeMarque produit) {
    // TODO : implémenter en scrappant info transformateurs
    return 0;  // 0 = pas de concurrent identifié
}
```

---

## 5️⃣ Modifications : Distributeur2AcheteurCC.java

**Section à ajouter dans next()** :

```java
@Override
public void next() {
    int etape = Filiere.LA_FILIERE.getEtape();
    // ... code existant ...
    
    // AVANT : Frais de stockage
    payerFraisStockage();
    
    // NOUVEAU V2 : Ajustement prix (délégué à Distributeur2Acteur)
    ajusterPrixDynamiques();
    
    this.indicateurStockTotal.setValeur(this, getStockTotal());
    journal.ajouter("Stock total : " + (getStockTotal()/1000) + " tonnes");
}
```

---

## 📋 Checklist Intégration

- [ ] Créer `EQ9_StrategieFixationPrix.java`
- [ ] Créer `EQ9_GestionnaireMarques.java`
- [ ] Créer `EQ9_MarquePrivee.java`
- [ ] Ajouter attributs Distributeur2Acteur
- [ ] Ajouter initialisation V2
- [ ] Implémenter `ajusterPrixDynamiques()`
- [ ] Implémenter `obtenirCoutAchat()` (liens contrats)
- [ ] Implémenter `estimerDemandeClients()` (liens Filière)
- [ ] Implémenter `estimerPrixConcurrent()` (liens transformateurs)
- [ ] Intégrer `ajusterPrixDynamiques()` dans Distributeur2AcheteurCC.next()
- [ ] Tests calculs prix
- [ ] Tests marges et profits
- [ ] Simulation 50 étapes

---

## 🔗 Dépendances Extérieures

Ces implémentations supposent l'existence de :

1. **ChocolatDeMarque** : avec champs marque + gamme
2. **Filière.LA_FILIERE** : accès aux acteurs et produits
3. **Contrats cadres existants** : pour lier coûts

Si ces structures changent, adapter les codes accordément.

---

*Recueil complet de codes V2 - Tous les fichiers à créer/modifier*
