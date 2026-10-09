package model;

public class CategoriePlat {
    private int idCategorie;
    private String nomCategorie;
    private String description;
    
    // Constructeurs
    public CategoriePlat() {}
    
    public CategoriePlat(String nomCategorie, String description) {
        this.nomCategorie = nomCategorie;
        this.description = description;
    }
    
    // Getters et Setters
    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }
    
    public String getNomCategorie() { return nomCategorie; }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie = nomCategorie; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return nomCategorie;
    }
}