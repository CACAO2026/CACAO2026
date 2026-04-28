# 📊 Stratégie V2 EQ9 - Clarification Complète

## État Actuel (Fin Avril 2026)

### ✅ Implémenté
- **Coûts de stockage** : 120€/tonne/étape (16x producteur)
- **Réapprovisionnement** : automatique si stock < 10T (cible 50T)
- **Contrats cadres** : achat auprès transformateurs
- **Appels d'offres** : achat complémentaire si besoin

### ❌ À Clarifier/Implémenter
1. **Marques distinctes par transformateur** + demande propre
2. **Gammes différenciées** (Haute/Moyenne/Basse qualité)
3. **Système de pricing dynamique** (actuellement basique)
4. **Marque propre EQ9** (production/achat exclusif)

---

## 1️⃣ Marques Distinctes + Demande Propre

### État Actuel
```
Tous les transformateurs produisent des "ChocolatDeMarque"
MAIS : pas de distinction réelle de demande par marque
```

### État Désiré V2

#### Architecture
```
ChocolatDeMarque {
    nom: "Prontella Premium"           // EQ4
    marque: IFabricantChocolatDeMarque // référence EQ4
    gamme: Gamme.HAUTE
    prixCout: 150€/T
    demandeMoyenne: ???  // À CALCULER
}
```

#### Marques à supporter
```
EQ4 → Transformateur4Marque
  ├─ Prontella Premium (HAUTE)
  ├─ Prontella Classic (MOYENNE)
  └─ Prontella Éco (BASSE)

EQ5 → Transformateur5Marque
  ├─ ChocolatBrand Deluxe (HAUTE)
  ├─ ChocolatBrand Standard (MOYENNE)
  └─ ChocolatBrand Budget (BASSE)

[EQ6, EQ7 similaire]

EQ9 → CarrefourMarque (MARQUE PROPRE)
  ├─ Carrefour Sélection Délice (HAUTE)
  ├─ Carrefour Sélection Standard (MOYENNE)
  └─ Carrefour Sélection Éco (BASSE)
```

#### Demande Client pour Chaque Marque
```
Demande TOTALE marché = Σ(toutes marques)

Mais distribution client par préférence marque:
- 20% clients préfèrent EQ4 (haut de gamme)
- 30% clients préfèrent EQ5 (populaire)
- 15% clients préfèrent EQ6 (spécialité)
- 10% clients préfèrent EQ7 (niche)
- 25% clients acceptent marque propre EQ9 (prix)

→ Si demande totale = 1000T chocolat:
  EQ4 doit vendre : 200T (sa part)
  EQ5 doit vendre : 300T (sa part)
  ...
  EQ9 marque propre : 250T (sa part)
```

### Impact sur Pricing
```
Concurrent direct (même gamme) :
  Prontella Premium vs Carrefour Delice → Prix similaire
  
Différenciation de marque :
  Prontella Premium (EQ4) : +5% premium (marque prestige)
  Carrefour Delice (EQ9) : -5% (marque propre, volume)
  
→ Prix ne peut pas être arbitraire, doit rester compétitif VS concurrents
```

---

## 2️⃣ Gammes Différenciées

### Classification (existe partiellement: HQ, MQ, BQ)

```
Gamme BASSE :
  Qualité: économique
  Coût prod: 100€/T (référence)
  Marge EQ9: 12% (compétition serrée)
  Prix vente: 112€/T
  Durée vie: 6 étapes (péremption plus rapide)
  Demande client: 40% du marché

Gamme MOYENNE :
  Qualité: équilibré
  Coût prod: 150€/T
  Marge EQ9: 18%
  Prix vente: 177€/T
  Durée vie: 8 étapes
  Demande client: 40% du marché

Gamme HAUTE :
  Qualité: premium
  Coût prod: 200€/T
  Marge EQ9: 25%
  Prix vente: 250€/T
  Durée vie: 12 étapes
  Demande client: 20% du marché
```

### Stratégie d'Assortiment EQ9

#### Option 1 : Volume (Stratégie Actuelle?)
```
80% stock = Gamme BASSE/MOYENNE (demande élevée)
20% stock = Gamme HAUTE (prestige)
```

#### Option 2 : Marges (Stratégie Recommandée V2)
```
Marque propre Carrefour :
  30% = Gamme BASSE (attire clients prix)
  50% = Gamme MOYENNE (profit équilibré)
  20% = Gamme HAUTE (prestige, fidélisation)

Revendication marques premium :
  Surtout HAUTE et MOYENNE (peu de BASSE)
```

---

## 3️⃣ Système de Modification des Prix

### État Actuel : **À CLARIFIER**

**Questions** :
- Comment les prix sont-ils fixés actuellement?
- À partir de quel coût?
- Avec quelle marge?
- Adaptés comment au marché?

### État Désiré V2

#### Formule Tarifation EQ9

```
Prix_Vente = Coût_Achat × (1 + Marge%) × Facteur_Demande × Facteur_Inventaire × Facteur_Gamme
```

**Détail par composante** :

**1. Coût_Achat** (€/T)
```
Pour Prontella Premium : 150€/T (prix contrat CC avec EQ4)
Pour Carrefour Delice (propre) : 140€/T (accord exclusif EQ4)
```

**2. Marge%** (selon gamme)
```
Basse gamme : 12%  (compétition intense)
Moyenne : 18%      (équilibré)
Haute : 25%        (moins d'élasticité)
Marque propre : 35% (économies production + fidélité)
```

**3. Facteur_Demande** (dynamique)
```
Ratio = Demande_Clients / Offre_Disponible

Si Ratio > 1.5 : facteur = 1.20  (+20% prix, forte demande)
Si Ratio 1.0-1.5 : facteur = 1.0  (équilibre)
Si Ratio 0.7-1.0 : facteur = 0.95 (-5% prix, légère suroffre)
Si Ratio < 0.7 : facteur = 0.85   (-15% prix, surstock urgent)
```

**4. Facteur_Inventaire** (surstock?)
```
Si Stock > Objectif × 1.5 : facteur = 0.90  (-10% pour déstockage)
Si Stock normal : facteur = 1.0
Si Stock < Seuil_Alerte : facteur = 1.10   (+10% urgence réappro)
```

**5. Facteur_Gamme** (compétition)
```
Si on est plus cher que concurrent direct de même gamme:
  facteur = 0.98  (-2% pour rester compétitif)
```

#### Exemple Concret

**Produit** : Prontella Premium (Gamme HAUTE)
```
Coût achat : 150€/T
Marge standard : 25%
→ Prix base = 150 × 1.25 = 187.50€/T

Facteur demande = 1.15 (demande bonne)
→ Prix = 187.50 × 1.15 = 215.63€/T

Facteur inventaire = 1.0 (stock normal)
→ Prix = 215.63€/T

Facteur gamme = 1.0 (on est compétitif)
→ **Prix final = 215€/T**

Avec remise fidélité client (-5%) :
→ Prix client = 215 × 0.95 = 204€/T
```

#### Implémentation Code

```java
class Distributeur2StrategieFixationPrix {
    
    public double calculerPrixVente(
        ChocolatDeMarque choco,
        double coutAchat,
        Gamme gamme,
        double stockActuel,
        double demandeMarche
    ) {
        // 1. Base
        double margePercent = getMargeParGamme(gamme);
        double prixBase = coutAchat * (1 + margePercent / 100.0);
        
        // 2. Facteur demande
        double offre = stockActuel;
        double ratio = demandeMarche / Math.max(offre, 1);
        double facteurDemande = getFacteurDemande(ratio);
        
        // 3. Facteur inventaire
        double facteurInventaire = getFacteurInventaire(stockActuel, 50000);
        
        // 4. Facteur gamme (compétition)
        double facteurGamme = getFacteurCompetition(choco);
        
        double prixFinal = prixBase * facteurDemande * facteurInventaire * facteurGamme;
        return prixFinal;
    }
    
    private double getMargeParGamme(Gamme gamme) {
        switch (gamme) {
            case BASSE: return 12.0;
            case MOYENNE: return 18.0;
            case HAUTE: return 25.0;
            default: return 15.0;
        }
    }
    
    private double getFacteurDemande(double ratio) {
        if (ratio > 1.5) return 1.20;
        if (ratio > 1.0) return 1.0 + (ratio - 1.0) * 0.2;
        if (ratio > 0.7) return 1.0 - (1.0 - ratio) * 0.05;
        return 0.85;
    }
    
    private double getFacteurInventaire(double stock, double objectif) {
        if (stock > objectif * 1.5) return 0.90;
        if (stock < objectif * 0.5) return 1.10;
        return 1.0;
    }
    
    private double getFacteurCompetition(ChocolatDeMarque choco) {
        // Comparer prix avec concurrent direct (même gamme)
        double prixConcurrent = estimerPrixConcurrent(choco);
        double prixCalcule = /* ... */;
        
        if (prixCalcule > prixConcurrent * 1.05) {
            return 0.98;  // Réduire pour rester compétitif
        }
        return 1.0;
    }
}
```

---

## 4️⃣ Marque Propre EQ9

### Rationale
- **Marge** : 35% vs 15-20% revendication
- **Indépendance** : moins dépendant des transformateurs
- **Volume** : justifie fidélité clients (remises attractives)
- **Marque** : contrôle image

### Modèle Opérationnel

#### Production / Sourcing
```
Option A : Partenariat exclusif
  → Contrat avec 1 transformateur (ex: EQ4)
  → "Fabriquez notre marque Carrefour"
  → Coût : 140€/T (15% moins cher qu'achat spot)
  → Engagement : 100T/mois minimum

Option B : Faire valoir production interne
  → EQ9 fabrique son chocolat
  → Économies encore meilleures
  → BUT : complexe à modéliser
```

#### Gammes de Marque Propre
```
Carrefour Sélection DÉLICE (HAUTE)
  Coût: 140€/T (partenariat)
  Marge: 35%
  Prix: 189€/T
  Fidélisation: clients premium attirés par marque distribuée

Carrefour Sélection STANDARD (MOYENNE)
  Coût: 90€/T
  Marge: 35%
  Prix: 121.50€/T
  Fidélisation: forte (meilleur rapport qualité/prix)

Carrefour Sélection ÉCO (BASSE)
  Coût: 60€/T
  Marge: 35%
  Prix: 81€/T
  Fidélisation: clients budget
```

#### Stratégie de Mix
```
Stock EQ9 (1000T total) :

Revendication marques transformateurs : 60%
  - Prontella (EQ4) : 20%
  - Autres : 40%
  → Profit : 15-20% marge

Marque propre Carrefour : 40%
  - Délice : 10%
  - Standard : 20%
  - Éco : 10%
  → Profit : 35% marge (majorité!)

Résultat :
  Profit moyen = 0.60 × 18% + 0.40 × 35% = 10.8% + 14% = 24.8%
  (vs 18% si que revendication)
```

---

## 📋 Priorité d'Implémentation V2

### Sprint 1 (Immédiat) - Marques & Gammes
- [ ] Implémenter classe `Gamme` avec énumération (BASSE/MOYENNE/HAUTE)
- [ ] Enrichir `ChocolatDeMarque` avec marque + gamme
- [ ] Identifier 3 produits par gamme par transformateur
- [ ] Mettre en place demandes distinctes par marque

### Sprint 2 - Prix Dynamique
- [ ] Classe `StrategieFixationPrix`
- [ ] Intégrer facteur demande
- [ ] Intégrer facteur inventaire
- [ ] Tests formules tarifaires

### Sprint 3 - Marque Propre
- [ ] Créer produits Carrefour Sélection
- [ ] Initialiser stock (30% du total)
- [ ] Contrat exclusif avec transformateur
- [ ] Adapter pricing (marge 35%)

---

## 🎯 Questions à Clarifier avec vous

1. **Marques transformateurs** :
   - Quels prix actuels pour chaque?
   - Quelles quantités achetées?

2. **Demand clients par marque** :
   - Données historiques disponibles?
   - Simulation ou données réelles?

3. **Marque propre** :
   - Avec quel transformateur partenaire?
   - À quel coût?

4. **Temps pour implémenter** :
   - Tous les sprints cette semaine?
   - Puis tester/valider?

---

*Document de stratégie V2 EQ9 - À discuter et valider avant implémentation*
