package model;

public class TableRestaurant {
    private int idTable;
    private String numeroTable;
    private int capacite;
    private String emplacement;
    private String statut; // LIBRE, OCCUPEE, RESERVEE
    
    public TableRestaurant() {}
    
    public TableRestaurant(String numeroTable, int capacite, String emplacement, String statut) {
        this.numeroTable = numeroTable;
        this.capacite = capacite;
        this.emplacement = emplacement;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getIdTable() { return idTable; }
    public void setIdTable(int idTable) { this.idTable = idTable; }
    
    public String getNumeroTable() { return numeroTable; }
    public void setNumeroTable(String numeroTable) { this.numeroTable = numeroTable; }
    
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    
    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    @Override
    public String toString() {
        return "Table " + numeroTable + " (" + statut + ")";
    }
}