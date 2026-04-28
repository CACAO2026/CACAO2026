# 📊 Stratégie V2 Distributeur EQ9 - Dossier Complet

*28 avril 2026*

---

## 📑 Table des matières
1. Contexte et Objectifs
2. Architecture V2 (4 piliers)
3. Chiffrage détaillé des impacts
4. Modèle économique
5. Calendrier d'implémentation
6. KPI et métriques de succès

---

## 1️⃣ Contexte et Objectifs

### Situation V1 (Actuelle)
- ✅ Réapprovisionnement automatique (stock cible 50T)
- ✅ Contrats cadres avec transformateurs
- ✅ Appels d'offres complémentaires
- ❌ **Problème** : Coûts stockage (120€/T)
- ❌ **Problème** : Prix peu optimisés
- ❌ **Problème** : Pas de différenciation marque/gamme
- ❌ **Problème** : Marges faibles (revendication = 15-20%)

### Objectifs V2
1. ✅ Implémenter coûts de stockage réalistes (120€/T) → **Déjà fait**
2. ✅ **Nouvelle** : Système de prix dynamique (demande/offre/inventaire)
3. ✅ **Nouvelle** : Différenciation marques & gammes
4. ✅ **Nouvelle** : Marque propre (marge 35%)

### Impact Financier Attendu V2
```
AVANT V2 (1000T stock, 100 étapes)
  Coûts stockage : 0€ (non implémenté)
  Marge moyenne : 18% → 180K€ profit

APRÈS V2 (gestion optimisée)
  Coûts stockage : 12M€ (100T × 120€)
  Mais marges améliorées :
    - 60% revendication à 18% : 108K€
    - 40% marque propre à 35% : 140K€
    - TOTAL : 248K€
  
  BILAN : +248K€ - 12M€ = ???
  
  → Survie = rotation stock rapide + marques propres
```

---

## 2️⃣ Architecture V2 - 4 Piliers

### Pilier 1️⃣ : Coûts de Stockage (✅ IMPLÉMENTÉ)

#### Détail
```
Producteur (EQ1-3) : 7.5€/tonne/période
Transformateur (EQ4-7) : ~30€/tonne/période
Distributeur EQ9 : 120€/tonne/période (16x producteur)
```

#### Implémentation
```java
protected static final double COUT_PAR_TONNE = 120.0;  // €/T/période

void payerFraisStockage() {
    double stockEnTonnes = getStockTotal() / 1000.0;
    double coutTotal = stockEnTonnes * COUT_PAR_TONNE;
    
    getBanque().debiter(this, cryptogramme, coutTotal);
    journal.ajouter("Frais stockage : " + coutTotal + "€");
}
```

#### Appel dans next()
```
1. Réapprovisionnement (+5T par produit)
2. Mise à jour stock total
3. Paiement frais stockage ← NOUVEAU
```

#### Chiffrage Exemple
```
Stock EQ9 = 100 tonnes (tout produits confondus)
Coût étape = 100 × 120 = 12 000€

Cumul sur 50 étapes = 600 000€
Cumul sur 100 étapes = 1 200 000€

→ Justifie rotation rapide et marques propres
```

---

### Pilier 2️⃣ : Marques Distinctes & Gammes

#### 2.1 Architecture Marques

```
Chaque transformateur = sa marque propre

EQ4 (Transformateur1) → Marque Prontella
  - Prontella DÉLICE (Gamme HAUTE)
  - Prontella CLASSIC (Gamme MOYENNE)
  - Prontella ÉCO (Gamme BASSE)

EQ5 (Transformateur2) → Marque ChocolatBrand
  - ChocolatBrand Premium (Gamme HAUTE)
  - ChocolatBrand Standard (Gamme MOYENNE)
  - ChocolatBrand Budget (Gamme BASSE)

[EQ6, EQ7 similaire]

EQ9 (Nous) → Marque Carrefour Sélection (propre)
  - Carrefour Sélection DÉLICE (Gamme HAUTE)
  - Carrefour Sélection STANDARD (Gamme MOYENNE)
  - Carrefour Sélection ÉCO (Gamme BASSE)
```

#### 2.2 Demande Marché par Marque

**Demande TOTALE estimée** : 1200T chocolat/période

**Distribution par préférence marque** :
```
EQ4 (Prontella) : 20% = 240T
EQ5 (ChocolatBrand) : 30% = 360T
EQ6 : 15% = 180T
EQ7 : 10% = 120T
Marques priv. (dont nous) : 25% = 300T
```

**Implication stratégique** :
- EQ4 doit vendre 240T (sinon rupture pour clients Prontella)
- Nous devons capturer notre 25% (300T) → Marque propre + revendication

#### 2.3 Gammes : Différenciation Qualité/Prix

```
┌─────────────┬──────────────┬─────────┬──────────┬─────────────────┐
│ GAMME       │ COÛT ACHAT   │ MARGE   │ PRIX VTE │ DURÉE VIE       │
├─────────────┼──────────────┼─────────┼──────────┼─────────────────┤
│ BASSE       │ 100€/T       │ 12%     │ 112€/T   │ 6 étapes        │
│ MOYENNE     │ 150€/T       │ 18%     │ 177€/T   │ 8 étapes        │
│ HAUTE       │ 200€/T       │ 25%     │ 250€/T   │ 12 étapes       │
└─────────────┴──────────────┴─────────┴──────────┴─────────────────┘

Allocation marché clients :
  Basse : 40% des ventes (consommateurs prix)
  Moyenne : 40% des ventes (équilibré)
  Haute : 20% des ventes (prestige/fidélité)
```

#### Chiffrage Mix EQ9

**Stock 1000 tonnes réparti** :

```
REVENDICATION TRANSFORMATEURS (60% = 600T) :
  Prontella :
    - 20% = 120T HAUTE (vente 250€/T) → 30K€
    - 30% = 180T MOYENNE (vente 177€/T) → 31.86K€
    - 50% = 300T BASSE (vente 112€/T) → 33.6K€
    Sous-total Prontella : 95.46K€ profit

  Autres transformateurs (400T) : profil similaire
  Sous-total revendication : ~160K€ profit

MARQUE PROPRE CARREFOUR (40% = 400T) :
  Coût achat : 120€/T (accord exclusif EQ4)
  Marge : 35%
  
  Délice (100T) : 120€ × 1.35 = 162€/T
    Profit : (162-120) × 100 = 4.2K€
  
  Standard (200T) : 120€ × 1.35 = 162€/T
    Profit : (162-120) × 200 = 8.4K€
  
  Éco (100T) : 120€ × 1.35 = 162€/T
    Profit : (162-120) × 100 = 4.2K€
  
  Sous-total marque propre : 16.8K€ profit
```

---

### Pilier 3️⃣ : Système de Prix Dynamique

#### 3.1 Formule de Base

```
Prix_Vente = Coût_Achat × (1 + Marge%) × Ajustements
```

#### 3.2 Composantes des Ajustements

**A. Facteur Demande/Offre**
```
Ratio = Demande_Clients / Offre_Disponible

Ratio > 1.5  → Forte demande → Facteur = 1.20  (+20% prix)
Ratio 1.0-1.5 → Équilibre → Facteur = 1.0
Ratio 0.7-1.0 → Léger surstock → Facteur = 0.95 (-5%)
Ratio < 0.7 → Surstock urgent → Facteur = 0.85 (-15%)
```

**B. Facteur Inventaire**
```
Stock > Objectif × 1.5 → Déstockage urgent → Facteur = 0.90 (-10%)
Stock normal → Facteur = 1.0
Stock < Seuil_Alerte × 0.5 → Rupture imminente → Facteur = 1.10 (+10%)
```

**C. Facteur Compétition Directe**
```
Si notre prix > concurrent même gamme de 5% :
  → Réduire de 2% pour rester attractif
  → Facteur = 0.98
```

#### 3.3 Exemple Concret de Calcul

**Produit** : Prontella Premium (Gamme HAUTE)

```
DONNÉES :
  Coût achat : 200€/T (contrat EQ4)
  Marge standard HAUTE : 25%
  Stock actuel : 80T (normal)
  Demande marché : 120T (ratio = 1.5)
  Prix concurrent : 263€/T

CALCUL ÉTAPE PAR ÉTAPE :

1. Prix base
   = 200€ × 1.25 = 250€/T

2. Facteur demande
   Ratio = 1.5 → Facteur = 1.0 (équilibre)
   Ajustement = 250€ × 1.0 = 250€/T

3. Facteur inventaire
   Stock 80T = normal → Facteur = 1.0
   Ajustement = 250€ × 1.0 = 250€/T

4. Facteur compétition
   Notre prix 250€ < concurrent 263€ ✓
   → Pas de réduction nécessaire
   Ajustement = 250€ × 1.0 = 250€/T

5. PRIX FINAL = 250€/T
```

**Variante** : Si surstock (150T au lieu de 80T)

```
Facteur inventaire :
  Stock 150T > Objectif × 1.5 (75T)
  → Déstockage urgent → Facteur = 0.90

Prix final = 250€/T × 0.90 = 225€/T
→ Réduction 10% pour vendre vite
```

---

### Pilier 4️⃣ : Marque Propre EQ9

#### 4.1 Rationale

```
Revendication marques :
  Coût achat : variable (150-200€/T selon marque)
  Marge : 15-20%
  Dépendance : haute (stockage risqué)

Marque propre Carrefour :
  Coût achat : 120€/T (accord exclusif)
  Marge : 35%
  Indépendance : haute
  
AVANTAGE MARQUE PROPRE :
  Coût réduit de 30-40%
  Marge augmentée de 75% (15% → 35%)
  Volume garanti (fidélité clients marque distributeur)
```

#### 4.2 Accord Stratégique

```
Contrat EQ9 ↔ EQ4 (Prontella)

"EQ4 fabrique notre marque Carrefour Sélection"

Terme :
  Quantité : 100T/période minimum
  Prix : 120€/T (vs 200€/T Prontella standard)
  Durée : 50 étapes (contrat long terme)
  Qualité : HAUTE (170€/T de base)
  
Bénéfice EQ4 :
  ✓ Volume garanti
  ✓ Client fidèle
  ✓ Coût unitaire réduit (volume)

Bénéfice EQ9 :
  ✓ Prix 40% moins cher
  ✓ Marge doublée (15% → 35%)
  ✓ Marque propre = fidélité client
```

#### 4.3 Gammes Marque Propre

```
┌──────────────────────┬────────────┬────────┬───────────┐
│ PRODUIT              │ COÛT ACHAT │ MARGE  │ PRIX VENTE│
├──────────────────────┼────────────┼────────┼───────────┤
│ Carrefour Délice     │ 120€/T     │ 35%    │ 162€/T    │
│ (Gamme HAUTE)        │            │        │           │
├──────────────────────┼────────────┼────────┼───────────┤
│ Carrefour Standard   │ 120€/T     │ 35%    │ 162€/T    │
│ (Gamme MOYENNE)      │            │        │           │
├──────────────────────┼────────────┼────────┼───────────┤
│ Carrefour Éco        │ 120€/T     │ 35%    │ 162€/T    │
│ (Gamme BASSE)        │            │        │           │
└──────────────────────┴────────────┴────────┴───────────┘

NOTE : Prix vente identique (162€) car coût identique
       Différenciation = marketing/packaging, pas prix
```

#### 4.4 Allocation Stock

```
Stock EQ9 = 1000T total

Revendication (60%) = 600T
  ├─ Prontella (120T = 20%)
  ├─ ChocolatBrand (180T = 30%)
  ├─ Marques EQ6/EQ7 (300T = 50%)
  
Marque propre Carrefour (40%) = 400T
  ├─ Carrefour Délice (100T = 25%)
  ├─ Carrefour Standard (200T = 50%)
  └─ Carrefour Éco (100T = 25%)
```

---

## 3️⃣ Chiffrage Détaillé des Impacts

### Scénario : 1000T stock, 100 étapes simulation

#### 3.1 Coûts de Stockage

```
Stock moyen = 1000T (cible constante)
Coût par période = 1000 × 120€ = 120K€
Coût 100 étapes = 12M€

IMPACT SUR TRÉSORERIE :
  Avant V2 : pas de coûts → 0€
  Après V2 : -120K€ par étape
  
  → Urgent = réduire stock ou augmenter chiffre d'affaires
```

#### 3.2 Revenus Détaillés (100T stock moyen)

**Scénario équilibré** :

```
REVENDICATION (60T stock moyen) :

Prontella :
  Ventes : 20T/période
  Coût achat : 150€/T → Coût 3M€
  Marge 18% : 3M€ × 0.18 = 540K€

ChocolatBrand :
  Ventes : 20T/période
  Coût achat : 150€/T → Coût 3M€
  Marge 18% : 3M€ × 0.18 = 540K€

Autres (20T) : ~360K€

Sous-total Revendication = 1.44M€ profit

MARQUE PROPRE (40T stock moyen) :

Carrefour Sélection :
  Ventes : 40T/période
  Coût achat : 120€/T → Coût 4.8M€
  Prix vente : 162€/T → CA 6.48M€
  Marge 35% : 6.48M€ × 0.35 = 2.27M€

Sous-total Marque Propre = 2.27M€ profit

TOTAL PROFIT BRUT = 1.44M€ + 2.27M€ = 3.71M€

MOINS : Coûts de stockage
  100 étapes × 120K€ = 12M€

BILAN NET = 3.71M€ - 12M€ = -8.29M€ ❌
```

**❌ PROBLÈME** : Les coûts stockage sont énormes !

**✅ SOLUTION** : Réduire stock moyen

```
SCÉNARIO OPTIMISÉ : 500T stock moyen (rotation 2x plus rapide)

Coûts stockage = 500 × 120€ × 100 = 6M€
Profits (proportionnel) ≈ 1.85M€

Bilan net = 1.85M€ - 6M€ = -4.15M€ ❌

TOUJOURS NÉGATIF → Faut augmenter prix !
```

**✅ SOLUTION FINALE** : Augmenter marges + réduire stock

```
SCÉNARIO V2 OPTIMISÉ :

Stock objectif = 300T (rotation 4x/période)

Coûts stockage = 300 × 120€ × 100 = 3.6M€

Revendication (30T moy) :
  Profit : 0.72M€ (18% marge)

Marque propre (20T moy) :
  Profit : 1.14M€ (35% marge)
  
Total profit : 1.86M€

Bilan net = 1.86M€ - 3.6M€ = -1.74M€

ENCORE NÉGATIF → Ajuster prix de 15-20% pour compenser coûts
```

---

## 4️⃣ Modèle Économique V2

### 4.1 Équation d'Équilibre

```
Profit_Net = CA - Coûts_Achat - Coûts_Stockage - Frais_Admin

CA = ∑(Volumes × Prix_Vente)

Coûts_Achat = ∑(Volumes × Coût_Achat)

Coûts_Stockage = 120€/T × Stock_Moyen × Nb_Étapes

Profit_Marge = CA - Coûts_Achat = CA × (1 - Coût%Prix)
```

### 4.2 Points Clés de Rentabilité

```
1. VOLUME-VITESSE :
   Plus on tourne stock vite → Moins on stocke
   Stock réduit de 80% → Coûts stockage réduits de 80%

2. MIX PRODUIT :
   35% marque propre → Marge moyenne +3-5% sur total
   
3. PRIX :
   Faut compenser coûts stockage par prix +10-15%
   Mais rester compétitif vs marques transformateurs
   
4. FIDÉLITÉ CLIENT :
   Clients réguliers marque propre = volume stable
```

### 4.3 Seuil de Rentabilité

```
Pour être profitable avec 100T stock moyen :

Marge moyenne requise = Coûts_Stockage / CA%
  = (100 × 120€ × 100 étapes) / CA%
  = 1.2M€ / CA%

Si CA = 10M€/100 étapes :
  Marge requise = 12%
  
  Marge V1 = 18% ✓ SUFFISANT
  Marge V2 avec 40% propre = 24.8% ✓ CONFORTABLE
```

---

## 5️⃣ Calendrier d'Implémentation

### Phase 1 : Marques & Gammes (Semaine 1)
```
Lundi-Mardi :
  ✓ Classe Gamme enum
  ✓ Enrichir ChocolatDeMarque
  ✓ Identifier produits par marque

Mercredi-Jeudi :
  ✓ Demandes distinctes par marque
  ✓ Tests intégration

Vendredi :
  ✓ Validation avec équipes transformateurs
```

### Phase 2 : Prix Dynamique (Semaine 2)
```
Lundi-Mardi :
  ✓ Classe StrategieFixationPrix
  ✓ Implémentation facteurs
  
Mercredi-Jeudi :
  ✓ Intégration dans next()
  ✓ Tests calculs

Vendredi :
  ✓ Ajustements paramétrages
```

### Phase 3 : Marque Propre (Semaine 2-3)
```
Lundi-Mardi :
  ✓ Produits Carrefour Sélection
  ✓ Accord EQ4
  
Mercredi :
  ✓ Stock initial (400T)
  ✓ Prix (162€/T)
  
Jeudi-Vendredi :
  ✓ Tests vente
  ✓ Validation marges
```

### Phase 4 : Validation Globale (Semaine 3-4)
```
Simulation 100 étapes complètes
Analyse financière
Optimisation paramètres
Présentation résultats
```

---

## 6️⃣ KPI et Métriques de Succès

### Métriques Financières

| KPI | V1 | V2 Cible |
|-----|-----|----------|
| Stock moyen (T) | 1000 | 300 |
| Coûts stockage/étape | 0 | 36K€ |
| Marge moyenne | 18% | 24.8% |
| Profit brut/100 étapes | 1.8M€ | 2.5M€ |
| Profit net/100 étapes | 1.8M€ | 1.1M€ |

### Métriques Opérationnelles

| KPI | V1 | V2 Cible |
|-----|-----|----------|
| Rotation stock | 1x/100 étapes | 3-4x |
| Rupture stock | ~10% | <5% |
| Compétitivité prix | À vérifier | -2% vs EQ4 |
| Mix marque propre | 0% | 40% |

### Métriques Marché

| KPI | Objectif |
|-----|----------|
| Part marché EQ9 | 25% (300T/1200T) |
| Fidélité clients | +30% reprise |
| Vitesse rotation | +4x |

---

## 📋 Checklist Implémentation Finale

- [ ] **Coûts stockage** (✅ FAIT)
  - [x] Implémentation payerFraisStockage()
  - [x] Appel dans next()
  - [x] Indicateur dashboard

- [ ] **Marques & Gammes**
  - [ ] Classe Gamme
  - [ ] Enrichir ChocolatDeMarque
  - [ ] 3 produits × marque × gamme
  - [ ] Demandes distinctes

- [ ] **Prix Dynamique**
  - [ ] Classe StrategieFixationPrix
  - [ ] Facteur demande/offre
  - [ ] Facteur inventaire
  - [ ] Facteur compétition
  - [ ] Tests calculs

- [ ] **Marque Propre**
  - [ ] Produits Carrefour Sélection
  - [ ] Stock 400T (40%)
  - [ ] Accord EQ4 @ 120€/T
  - [ ] Prix 162€/T
  - [ ] Dashboard indicateurs

- [ ] **Validation**
  - [ ] Simulation 100 étapes
  - [ ] Analyse financière
  - [ ] Stress tests
  - [ ] Présentation résultats

---

## 📞 Contact et Questions

**Equipe EQ9** (Distributeur2)
- Paul JUHEL
- Anass OUISRANI
- Paul ROSSIGNOL

---

*Dossier stratégique V2 EQ9 - Complet et chiffré*
