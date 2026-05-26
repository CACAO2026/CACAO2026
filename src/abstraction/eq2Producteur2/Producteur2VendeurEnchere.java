package abstraction.eq2Producteur2;

import java.util.List;
import java.util.ArrayList;
import abstraction.eqXRomu.encheres.Enchere;
import abstraction.eqXRomu.encheres.IVendeurAuxEncheres;
import abstraction.eqXRomu.encheres.MiseAuxEncheres;
import abstraction.eqXRomu.encheres.SuperviseurVentesAuxEncheres;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;

/**
 * @author Thomas
 */
public class Producteur2VendeurEnchere extends Producteur2VendeurAO implements IVendeurAuxEncheres {
    protected Journal journalEncheres;
    protected SuperviseurVentesAuxEncheres supEncheres;

    public Producteur2VendeurEnchere() {
        super();
        this.journalEncheres = new Journal("Journal Encheres Eq2", this);
    }

    @Override
    public void initialiser() {
        super.initialiser();
        this.supEncheres = (SuperviseurVentesAuxEncheres) Filiere.LA_FILIERE.getActeur("Sup.Encheres");
        this.journalEncheres.ajouter("Producteur2VendeurEnchere initialisé");
    }

    @Override
    public List<Journal> getJournaux() {
        List<Journal> res = super.getJournaux();
        res.add(this.journalEncheres);
        return res;
    }

    @Override
    public void next() {
        super.next();

        if (this.supEncheres != null) {
            this.journalEncheres.ajouter("=== Etape " + Filiere.LA_FILIERE.getEtape() + " : Vérification Enchères ===");

            for (Feve f : Feve.values()) {
                if (f.isEquitable()) {
                    continue;
                }

                double stockActuel = this.stockvar.get(f).getValeur(this.cryptogramme);
                double engage = this.restantDu(f);
                double disponible = stockActuel - engage;

                // Marge de sécurité
                double buffer = 100.0;
                double aVendre = disponible - buffer;

                if (aVendre >= MiseAuxEncheres.QUANTITE_MIN) {
                    double maxVente = 2000.0;
                    if (stockActuel > 100000.0) {
                        maxVente = stockActuel - 100000.0;
                    }
                    double quantiteAVendre = Math.min(aVendre, maxVente);

                    double prixMin = this.getPrixSeuilEnchere(f, quantiteAVendre);

                    this.journalEncheres.ajouter("Tentative de mise aux enchères: "
                            + quantiteAVendre + "T de " + f + " (prix seuil = " + prixMin + " €/T)");

                    Enchere e = this.supEncheres.vendreAuxEncheres(this, this.cryptogramme, f, quantiteAVendre);
                    if (e != null) {
                        this.journalEncheres.ajouter("Enchère REUSSIE : " + e.getQuantiteT()
                                + "T de " + f + " vendues à " + e.getPrixTonne() + " €/T à "
                                + e.getAcheteur().getNom());

                        // Retrait effectif du stock
                        this.retirerDuStock(f, e.getQuantiteT());
                        if (this.stocks.containsKey(f) && this.stockvar.containsKey(f)) {
                            this.stocks.get(f).setValeur(this, this.stockvar.get(f).getValeur());
                        }
                    } else {
                        this.journalEncheres.ajouter("Enchère ECHOUEE (aucune offre acceptable pour " + f + ")");
                    }
                }
            }
        }
    }

    /**
     * Calcule le prix de réserve (prix minimal acceptable) pour les enchères.
     */
    public double getPrixSeuilEnchere(Feve f, double quantite) {
        double coutProd = this.cout_unit_t.getOrDefault(f, 0.0);
        double stockTotalVal = this.stockTotal != null ? this.stockTotal.getValeur() : 0.0;
        double stockActuel = this.stockvar.containsKey(f) ? this.stockvar.get(f).getValeur(this.cryptogramme) : 0.0;

        // Marge par défaut (10% au-dessus du coût de production)
        double marge = 1.10;

        if (stockActuel > 100000.0) {
            marge = 0.50;
        } else if (stockTotalVal > 400000.0) {
            marge -= 0.20;
        } else if (stockTotalVal > 300000.0) {
            marge -= 0.15;
        } else if (stockTotalVal > 200000.0) {
            marge -= 0.10;
        } else if (stockTotalVal > 100000.0) {
            marge -= 0.05;
        }

        // Ajustement selon l'ancienneté (risque de péremption)
        int ageMax = this.getAgeAnciennete(f);
        if (f == Feve.F_HQ && ageMax >= 8) {
            marge -= 0.20;
        } else if (f == Feve.F_MQ && ageMax >= 18) {
            marge -= 0.15;
        } else if (f == Feve.F_BQ && ageMax >= 36) {
            marge -= 0.20;
        }

        // Sécurité de marge minimale (on évite de vendre pour rien sauf cas extrême)
        if (stockActuel <= 100000.0) {
            marge = Math.max(marge, 0.70);
        }

        double prixMin = coutProd * marge;

        // Pour les fèves non équitables, on s'aligne sur le cours de la bourse s'il est
        // bas
        try {
            abstraction.eqXRomu.bourseCacao.BourseCacao bourse = (abstraction.eqXRomu.bourseCacao.BourseCacao) Filiere.LA_FILIERE
                    .getActeur("BourseCacao");
            if (bourse != null && bourse.getCours(f) != null) {
                double cours = bourse.getCours(f).getValeur();
                // On accepte de vendre jusqu'à 10% en dessous du cours pour écouler le surplus
                prixMin = Math.min(prixMin, cours * 0.90);
            }
        } catch (Exception e) {
        }

        return Math.max(prixMin, 1.0);
    }

    /**
     * Sélectionne la meilleure enchère reçue.
     */
    @Override
    public Enchere choisir(List<Enchere> propositions) {
        if (propositions == null || propositions.isEmpty()) {
            return null;
        }

        Enchere meilleure = propositions.get(0);
        Feve f = (Feve) meilleure.getMiseAuxEncheres().getProduit();
        double quantite = meilleure.getMiseAuxEncheres().getQuantiteT();
        double prixMin = this.getPrixSeuilEnchere(f, quantite);

        this.journalEncheres.ajouter("Choix enchère: Meilleure proposition pour " + quantite + "T de " + f
                + " = " + meilleure.getPrixTonne() + " €/T (seuil exigé = " + prixMin + " €/T)");

        if (meilleure.getPrixTonne() >= prixMin) {
            return meilleure;
        }

        return null;
    }
}
