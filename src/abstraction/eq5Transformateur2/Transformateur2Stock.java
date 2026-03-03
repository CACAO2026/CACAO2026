package abstraction.eq5Transformateur2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.lang.Integer;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Transformateur2Stock extends Transformateur2Acteur{

    // Attributs
    private ArrayList<Integer> stock_feve;
    private ArrayList<Integer> stock_chocolat;
    
    // Constructeur
    public Transformateur2Stock(ArrayList<Integer> myStock_feve, ArrayList<Integer> myStock_chocolat){
        this.stock_feve = myStock_feve;
        this.stock_chocolat = myStock_chocolat;
    }

    // Méthodes

    public void add_feve(int n, Feve Q){
        if (Q == Feve.F_BQ){
            this.stock_feve.add(0, n);
        }
            
    }
}
