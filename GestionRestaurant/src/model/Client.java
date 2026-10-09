package model;

public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private int nbVisites;
    
    public Client() {}
    
    public Client(String nom, String prenom, String telephone, String email, int nbVisites) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.nbVisites = nbVisites;
    }
    
    // Getters et Setters
    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getNbVisites() { return nbVisites; }
    public void setNbVisites(int nbVisites) { this.nbVisites = nbVisites; }
    
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}