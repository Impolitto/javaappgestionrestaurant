package model;

public class DetailCommande {
    private int idDetail;
    private int idCommande;
    private int idPlat;
    private int quantite;
    private double prixUnitaire;
    private double sousTotal;
    private String notesSpeciales;
    
    // Constructeurs
    public DetailCommande() {
    }
    
    public DetailCommande(int idPlat, int quantite, double prixUnitaire) {
        this.idPlat = idPlat;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }
    
    // Getters et Setters
    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }
    
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }
    
    public int getIdPlat() { return idPlat; }
    public void setIdPlat(int idPlat) { this.idPlat = idPlat; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { 
        this.quantite = quantite;
        calculerSousTotal();
    }
    
    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { 
        this.prixUnitaire = prixUnitaire;
        calculerSousTotal();
    }
    
    public double getSousTotal() { return sousTotal; }
    public void setSousTotal(double sousTotal) { this.sousTotal = sousTotal; }
    
    public String getNotesSpeciales() { return notesSpeciales; }
    public void setNotesSpeciales(String notesSpeciales) { this.notesSpeciales = notesSpeciales; }
    
    // Méthode pour calculer le sous-total
    public void calculerSousTotal() {
        this.sousTotal = quantite * prixUnitaire;
    }
}
