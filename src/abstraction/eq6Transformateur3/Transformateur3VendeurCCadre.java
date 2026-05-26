package abstraction.eq6Transformateur3;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;

import abstraction.eqXRomu.general.Journal;

import abstraction.eqXRomu.produits.IProduit;

/** @author : Brevael Le Clezio */
public class Transformateur3VendeurCCadre extends Transformateur3AcheteurCCadre implements IVendeurContratCadre {

    protected Journal journalCCVente;
    protected List<ExemplaireContratCadre> contratsVendus;

    public Transformateur3VendeurCCadre() {
        super();

        this.journalCCVente = new Journal("Journal Vendeur Contrat Cadre EQ6", this);

        this.contratsVendus = new LinkedList<ExemplaireContratCadre>();
    }

    /* ===================================================== */
    /*                       VENTE                           */
    /* ===================================================== */

    public boolean vend(IProduit produit) {

        boolean vend =
                this.getStockProduit(produit) > 200
                && !produit.getType().equals("Feve");

        return vend;
    }

    /* ===================================================== */
    /*                 ENGAGEMENTS EN COURS                  */
    /* ===================================================== */

    public double totalEngagement(IProduit produit) {

        double total = 0.0;

        for (ExemplaireContratCadre c : this.contratsVendus) {

            if (c.getProduit().equals(produit)) {

                total += c.getQuantiteRestantALivrer();
            }
        }

        return total;
    }

    /* ===================================================== */
    /*              NEGOCIATION ECHEANCIER                  */
    /* ===================================================== */

    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {

        double stockDisponible =
                this.getStockProduit(contrat.getProduit())
                - totalEngagement(contrat.getProduit());

        if (stockDisponible >= contrat.getQuantiteTotale()) {

            this.journalCCVente.ajouter(
                    "Acceptation échéancier contrat "
                    + contrat.getNumero()
                    + " avec "
                    + contrat.getAcheteur().getNom()
                    + " pour "
                    + contrat.getQuantiteTotale()
                    + " T de "
                    + contrat.getProduit());

            return contrat.getEcheancier();

        } else {

            this.journalCCVente.ajouter(
                    "Refus échéancier contrat "
                    + contrat.getNumero()
                    + " : stock insuffisant ("
                    + stockDisponible
                    + " T disponibles)");

            return null;
        }
    }

    /* ===================================================== */
    /*                    PRIX VENDEUR                       */
    /* ===================================================== */

    public double propositionPrix(ExemplaireContratCadre contrat) {

        double prix = 0.0;

        String produit = contrat.getProduit().toString();

        if (produit.contains("HQ")) {

            prix = 18000.0;

        } else {

            prix = 11000.0;
        }

        this.journalCCVente.ajouter(
                "Proposition prix vendeur pour contrat "
                + contrat.getNumero()
                + " : "
                + prix
                + " €/T");

        return prix;
    }

    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {

        double prixInitial = contrat.getListePrix().get(0);
        double prixActuel = contrat.getPrix();

        double prix;

        if (prixActuel >= 0.95 * prixInitial) {

            prix = prixActuel;

        } else {

            prix = 0.98 * prixInitial;
        }

        this.journalCCVente.ajouter(
                "Contre-proposition prix vendeur contrat "
                + contrat.getNumero()
                + " : "
                + prix
                + " €/T");

        return prix;
    }

    /* ===================================================== */
    /*             SIGNATURE D'UN CONTRAT                    */
    /* ===================================================== */

    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {

        this.contratsVendus.add(contrat);

        this.journalCCVente.ajouter(
                "NOUVEAU CONTRAT SIGNE : "
                + contrat.getNumero()
                + " | Acheteur = "
                + contrat.getAcheteur().getNom()
                + " | Produit = "
                + contrat.getProduit()
                + " | Quantité = "
                + contrat.getQuantiteTotale()
                + " T"
                + " | Prix = "
                + contrat.getPrix()
                + " €/T");
    }

    /* ===================================================== */
    /*                     LIVRAISON                         */
    /* ===================================================== */

    public double livrer(
            IProduit produit,
            double quantite,
            ExemplaireContratCadre contrat) {

        double disponible = this.getStockProduit(produit);

        if (disponible <= 0) {

            this.journalCCVente.ajouter(
                    "Livraison impossible contrat "
                    + contrat.getNumero()
                    + " : stock vide");

            return 0.0;
        }

        double livrable = Math.min(disponible, quantite);

        this.setStockProduit(
                produit,
                disponible - livrable);

        if (livrable < quantite) {

            this.journalCCVente.ajouter(
                    "Livraison PARTIELLE contrat "
                    + contrat.getNumero()
                    + " : "
                    + livrable
                    + " T livrées sur "
                    + quantite
                    + " demandées");

        } else {

            this.journalCCVente.ajouter(
                    "Livraison contrat "
                    + contrat.getNumero()
                    + " : "
                    + livrable
                    + " T de "
                    + produit
                    + " | Stock restant = "
                    + (disponible - livrable));
        }

        return livrable;
    }

    /* ===================================================== */
    /*                         NEXT                          */
    /* ===================================================== */

    public void next() {

        super.next();

        this.journalCCVente.ajouter(
                "========== ETAPE "
                + Filiere.LA_FILIERE.getEtape()
                + " ==========");

        SuperviseurVentesContratCadre sup =
                (SuperviseurVentesContratCadre)
                (Filiere.LA_FILIERE.getActeur("Sup.CCadre"));

        List<IAcheteurContratCadre> acheteurs =
                sup.getAcheteurs(LamborghiniduCacao);

        /* ========================= */
        /*       LAMBORGHINI         */
        /* ========================= */

        double stockLambo =
                this.getStockProduit(LamborghiniduCacao);

        double engagementLambo =
                totalEngagement(LamborghiniduCacao);

        double stockLibreLambo =
                stockLambo - engagementLambo;

        if (stockLibreLambo > 500 && !acheteurs.isEmpty()) {

            IAcheteurContratCadre acheteur = acheteurs.get(0);

            if (acheteur instanceof IDistributeurChocolatDeMarque) {

                double quantite = stockLibreLambo * 0.4;

                Echeancier e =
                        new Echeancier(
                                Filiere.LA_FILIERE.getEtape() + 1,
                                4,
                                quantite / 4);

                this.journalCCVente.ajouter(
                        "Demande vendeur envoyée à "
                        + acheteur.getNom()
                        + " pour "
                        + quantite
                        + " T de "
                        + LamborghiniduCacao);

                sup.demandeVendeur(
                        acheteur,
                        this,
                        LamborghiniduCacao,
                        e,
                        cryptogramme,
                        false);
            }
        }

        /* ========================= */
        /*        CHOCOENBIEN        */
        /* ========================= */

        double stockChoco =
                this.getStockProduit(Chocoenbien);

        double engagementChoco =
                totalEngagement(Chocoenbien);

        double stockLibreChoco =
                stockChoco - engagementChoco;

        if (stockLibreChoco > 800 && !acheteurs.isEmpty()) {

            IAcheteurContratCadre acheteur = acheteurs.get(0);

            if (acheteur instanceof IDistributeurChocolatDeMarque) {

                double quantite = stockLibreChoco * 0.4;

                Echeancier e =
                        new Echeancier(
                                Filiere.LA_FILIERE.getEtape() + 1,
                                4,
                                quantite / 4);

                this.journalCCVente.ajouter(
                        "Demande vendeur envoyée à "
                        + acheteur.getNom()
                        + " pour "
                        + quantite
                        + " T de "
                        + Chocoenbien);

                sup.demandeVendeur(
                        acheteur,
                        this,
                        Chocoenbien,
                        e,
                        cryptogramme,
                        false);
            }
        }

        /* ========================= */
        /*      ARCHIVAGE CC         */
        /* ========================= */

        List<ExemplaireContratCadre> termines =
                new LinkedList<ExemplaireContratCadre>();

        for (ExemplaireContratCadre c : this.contratsVendus) {

            if (c.getQuantiteRestantALivrer() == 0.0
                    && c.getMontantRestantARegler() <= 0.0) {

                termines.add(c);
            }
        }

        for (ExemplaireContratCadre c : termines) {

            this.journalCCVente.ajouter(
                    "Archivage contrat "
                    + c.getNumero());

            this.contratsVendus.remove(c);
        }
    }

    /* ===================================================== */
    /*                      JOURNAUX                         */
    /* ===================================================== */

    public List<Journal> getJournaux() {

        List<Journal> res =
                new ArrayList<Journal>(super.getJournaux());

        res.add(this.journalCCVente);

        return res;
    }
}
