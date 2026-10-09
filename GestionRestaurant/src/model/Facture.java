package model;

import java.util.Date;

public class Facture {
    private int idFacture;
    private int idCommande;
    private Date dateFacture;
    private double montantHT; // Hors Taxe
    private double tauxTVA;
    private double montantTVA;
    private double montantTTC; // Toutes Taxes Comprises
    private String statut; // EMISE, PAYEE, ANNULEE
    private String notes;
    
    // Constructeurs
    public Facture() {
    }
    
    public Facture(int idCommande, Date dateFacture, double montantHT, double tauxTVA, String statut) {
        this.idCommande = idCommande;
        this.dateFacture = dateFacture;
        this.montantHT = montantHT;
        this.tauxTVA = tauxTVA;
        this.statut = statut;
        calculerMontants();
    }
    
    // Getters et Setters
    public int getIdFacture() { return idFacture; }
    public void setIdFacture(int idFacture) { this.idFacture = idFacture; }
    
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }
    
    public Date getDateFacture() { return dateFacture; }
    public void setDateFacture(Date dateFacture) { this.dateFacture = dateFacture; }
    
    public double getMontantHT() { return montantHT; }
    public void setMontantHT(double montantHT) { 
        this.montantHT = montantHT;
        calculerMontants();
    }
    
    public double getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(double tauxTVA) { 
        this.tauxTVA = tauxTVA;
        calculerMontants();
    }
    
    public double getMontantTVA() { return montantTVA; }
    public void setMontantTVA(double montantTVA) { this.montantTVA = montantTVA; }
    
    public double getMontantTTC() { return montantTTC; }
    public void setMontantTTC(double montantTTC) { this.montantTTC = montantTTC; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) {
        if (statut.equals("EMISE") || statut.equals("PAYEE") || statut.equals("ANNULEE")) {
            this.statut = statut;
        } else {
            this.statut = "EMISE";
        }
    }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Méthode pour calculer les montants
    public void calculerMontants() {
        this.montantTVA = montantHT * (tauxTVA / 100);
        this.montantTTC = montantHT + montantTVA;
    }
    
    @Override
    public String toString() {
        return "Facture #" + idFacture + " - Commande #" + idCommande + " (" + statut + ")";
    }
}
