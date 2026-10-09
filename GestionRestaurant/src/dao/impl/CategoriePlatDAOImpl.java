package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CategoriePlat;

public class CategoriePlatDAOImpl implements DAO<CategoriePlat> {
    
    @Override
    public boolean create(CategoriePlat categorie) {
        String sql = "INSERT INTO CATEGORIE_PLAT (nom_categorie, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categorie.setIdCategorie(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public CategoriePlat read(int id) {
        String sql = "SELECT * FROM CATEGORIE_PLAT WHERE id_categorie = ?";
        CategoriePlat categorie = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categorie = new CategoriePlat();
                    categorie.setIdCategorie(rs.getInt("id_categorie"));
                    categorie.setNomCategorie(rs.getString("nom_categorie"));
                    categorie.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorie;
    }
    
    @Override
    public boolean update(CategoriePlat categorie) {
        String sql = "UPDATE CATEGORIE_PLAT SET nom_categorie = ?, description = ? WHERE id_categorie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setInt(3, categorie.getIdCategorie());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM CATEGORIE_PLAT WHERE id_categorie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<CategoriePlat> findAll() {
        List<CategoriePlat> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIE_PLAT ORDER BY nom_categorie";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CategoriePlat categorie = new CategoriePlat();
                categorie.setIdCategorie(rs.getInt("id_categorie"));
                categorie.setNomCategorie(rs.getString("nom_categorie"));
                categorie.setDescription(rs.getString("description"));
                
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    // Méthode pour obtenir le nombre de plats par catégorie
    public int getNombrePlats(int idCategorie) {
        String sql = "SELECT COUNT(*) as total FROM PLAT WHERE id_categorie = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCategorie);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}