package model;

import java.util.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;

public class Commande {
    private int idCommande;
    private int idTable;
    private int idClient;
    private Date dateCommande;
    private Time heureCommande;
    private String descriptionCommande;
    private String statutCommande; // EN_COURS, PREPARATION, SERVIE, REGLEE
    private double montantTotal;
    
    // Liste des détails de commande (pour l'interface)
    private List<DetailCommande> details;
    
    // Constructeurs
    public Commande() {
        this.details = new ArrayList<>();
    }
    
    public Commande(int idTable, int idClient, Date dateCommande, Time heureCommande, String statutCommande) {
        this.idTable = idTable;
        this.idClient = idClient;
        this.dateCommande = dateCommande;
        this.heureCommande = heureCommande;
        this.statutCommande = statutCommande;
        this.details = new ArrayList<>();
    }
    
    // Getters et Setters
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }
    
    public int getIdTable() { return idTable; }
    public void setIdTable(int idTable) { this.idTable = idTable; }
    
    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }
    
    public Date getDateCommande() { return dateCommande; }
    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }
    
    public Time getHeureCommande() { return heureCommande; }
    public void setHeureCommande(Time heureCommande) { this.heureCommande = heureCommande; }

    public String getDescriptionCommande() { return descriptionCommande; }
    public void setDescriptionCommande(String descriptionCommande) { this.descriptionCommande = descriptionCommande; }
    
    public String getStatutCommande() { return statutCommande; }
    public void setStatutCommande(String statutCommande) { 
        // Validation des statuts possibles
        if (statutCommande.equals("EN_COURS") || 
            statutCommande.equals("PREPARATION") || 
            statutCommande.equals("SERVIE") || 
            statutCommande.equals("REGLEE")) {
            this.statutCommande = statutCommande;
        } else {
            this.statutCommande = "EN_COURS";
        }
    }
    
    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    
    // Méthodes pour gérer les détails
    public List<DetailCommande> getDetails() { return details; }
    public void setDetails(List<DetailCommande> details) { this.details = details; }
    
    public void addDetail(DetailCommande detail) {
        this.details.add(detail);
    }
    
    public void removeDetail(DetailCommande detail) {
        this.details.remove(detail);
    }
    
    // Méthode pour calculer le total
    public double calculerTotal() {
        double total = 0;
        for (DetailCommande detail : details) {
            total += detail.getQuantite() * detail.getPrixUnitaire();
        }
        this.montantTotal = total;
        return total;
    }
    
    // Méthode pour obtenir le statut en français
    public String getStatutEnFrancais() {
        switch(statutCommande) {
            case "EN_COURS": return "En cours";
            case "PREPARATION": return "En préparation";
            case "SERVIE": return "Servie";
            case "REGLEE": return "Règlée";
            default: return "Statut inconnu";
        }
    }
    
    @Override
    public String toString() {
        return "Commande #" + idCommande + " - " + getStatutEnFrancais() + " - " + montantTotal + "€";
    }
}