package abstraction.eq1Producteur1;

public class Producteur1Banquier extends Producteur1Acteur{
    
    private double solde = 0;

    public Producteur1Banquier(){
        super();
    }

    public double getSolde(){
        return this.solde;
    }

    public void vente(double amount){
        if (amount>0){
            this.solde = this.solde + amount;
        }
        else{
            this.solde = this.solde - amount;
        }
    }

    public boolean achat(double amount){
        if (amount<this.solde){
            if (amount >0){
                this.solde = this.solde - amount;
            }
            else{
                this.solde = this.solde + amount;
            }
            return true;
        }
        return false;
    }
}
