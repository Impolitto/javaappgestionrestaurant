package model;

public class Plat {
    private int idPlat;
    private String nomPlat;
    private String description;
    private double prix;
    private int idCategorie;
    private boolean disponible;
    private int tempsPreparation;
    
    // Constructeurs
    public Plat() {}
    
    public Plat(String nomPlat, String description, double prix, 
                int idCategorie, boolean disponible, int tempsPreparation) {
        this.nomPlat = nomPlat;
        this.description = description;
        this.prix = prix;
        this.idCategorie = idCategorie;
        this.disponible = disponible;
        this.tempsPreparation = tempsPreparation;
    }
    
    // Getters et Setters
    public int getIdPlat() { return idPlat; }
    public void setIdPlat(int idPlat) { this.idPlat = idPlat; }
    
    public String getNomPlat() { return nomPlat; }
    public void setNomPlat(String nomPlat) { this.nomPlat = nomPlat; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    
    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public int getTempsPreparation() { return tempsPreparation; }
    public void setTempsPreparation(int tempsPreparation) { this.tempsPreparation = tempsPreparation; }
    
    @Override
    public String toString() {
        return nomPlat + " - " + prix + "€";
    }
}