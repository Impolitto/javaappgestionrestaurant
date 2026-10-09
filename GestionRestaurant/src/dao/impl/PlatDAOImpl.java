package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Plat;

public class PlatDAOImpl implements DAO<Plat> {
    
    @Override
    public boolean create(Plat plat) {
        String sql = "INSERT INTO PLAT (nom_plat, description, prix, id_categorie, disponible, temps_preparation) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, plat.getNomPlat());
                pstmt.setString(2, plat.getDescription());
                pstmt.setDouble(3, plat.getPrix());
                pstmt.setInt(4, plat.getIdCategorie());
                pstmt.setBoolean(5, plat.isDisponible());
                pstmt.setInt(6, plat.getTempsPreparation());
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            plat.setIdPlat(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL lors de la création du plat: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public Plat read(int id) {
        String sql = "SELECT * FROM PLAT WHERE id_plat = ?";
        Plat plat = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        plat = new Plat();
                        plat.setIdPlat(rs.getInt("id_plat"));
                        plat.setNomPlat(rs.getString("nom_plat"));
                        plat.setDescription(rs.getString("description"));
                        plat.setPrix(rs.getDouble("prix"));
                        plat.setIdCategorie(rs.getInt("id_categorie"));
                        plat.setDisponible(rs.getBoolean("disponible"));
                        plat.setTempsPreparation(rs.getInt("temps_preparation"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plat;
    }
    
    @Override
    public boolean update(Plat plat) {
        String sql = "UPDATE PLAT SET nom_plat = ?, description = ?, prix = ?, id_categorie = ?, disponible = ?, temps_preparation = ? WHERE id_plat = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, plat.getNomPlat());
                pstmt.setString(2, plat.getDescription());
                pstmt.setDouble(3, plat.getPrix());
                pstmt.setInt(4, plat.getIdCategorie());
                pstmt.setBoolean(5, plat.isDisponible());
                pstmt.setInt(6, plat.getTempsPreparation());
                pstmt.setInt(7, plat.getIdPlat());
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL lors de la mise à jour du plat: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM PLAT WHERE id_plat = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL lors de la suppression du plat: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public List<Plat> findAll() {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT * FROM PLAT ORDER BY nom_plat";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return plats;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Plat plat = new Plat();
                    plat.setIdPlat(rs.getInt("id_plat"));
                    plat.setNomPlat(rs.getString("nom_plat"));
                    plat.setDescription(rs.getString("description"));
                    plat.setPrix(rs.getDouble("prix"));
                    plat.setIdCategorie(rs.getInt("id_categorie"));
                    plat.setDisponible(rs.getBoolean("disponible"));
                    plat.setTempsPreparation(rs.getInt("temps_preparation"));
                    
                    plats.add(plat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL lors du chargement des plats: " + e.getMessage());
        }
        return plats;
    }
    
    // Méthode supplémentaire pour les plats disponibles
    public List<Plat> findAvailablePlats() {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT * FROM PLAT WHERE disponible = true ORDER BY nom_plat";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return plats;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Plat plat = new Plat();
                    plat.setIdPlat(rs.getInt("id_plat"));
                    plat.setNomPlat(rs.getString("nom_plat"));
                    plat.setDescription(rs.getString("description"));
                    plat.setPrix(rs.getDouble("prix"));
                    plat.setIdCategorie(rs.getInt("id_categorie"));
                    plat.setDisponible(rs.getBoolean("disponible"));
                    plat.setTempsPreparation(rs.getInt("temps_preparation"));
                    
                    plats.add(plat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL lors du chargement des plats disponibles: " + e.getMessage());
        }
        return plats;
    }
}