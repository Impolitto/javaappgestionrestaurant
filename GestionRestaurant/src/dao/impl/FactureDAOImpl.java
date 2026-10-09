package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Facture;

public class FactureDAOImpl implements DAO<Facture> {
    
    @Override
    public boolean create(Facture facture) {
        String sql = "INSERT INTO FACTURE (id_commande, date_facture, montant_ht, taux_tva, montant_tva, montant_ttc, statut, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, facture.getIdCommande());
                pstmt.setDate(2, new java.sql.Date(facture.getDateFacture().getTime()));
                pstmt.setDouble(3, facture.getMontantHT());
                pstmt.setDouble(4, facture.getTauxTVA());
                pstmt.setDouble(5, facture.getMontantTVA());
                pstmt.setDouble(6, facture.getMontantTTC());
                pstmt.setString(7, facture.getStatut());
                pstmt.setString(8, facture.getNotes());
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            facture.setIdFacture(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Facture read(int id) {
        String sql = "SELECT * FROM FACTURE WHERE id_facture = ?";
        Facture facture = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        facture = new Facture();
                        facture.setIdFacture(rs.getInt("id_facture"));
                        facture.setIdCommande(rs.getInt("id_commande"));
                        facture.setDateFacture(rs.getDate("date_facture"));
                        facture.setMontantHT(rs.getDouble("montant_ht"));
                        facture.setTauxTVA(rs.getDouble("taux_tva"));
                        facture.setMontantTVA(rs.getDouble("montant_tva"));
                        facture.setMontantTTC(rs.getDouble("montant_ttc"));
                        facture.setStatut(rs.getString("statut"));
                        facture.setNotes(rs.getString("notes"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facture;
    }
    
    @Override
    public boolean update(Facture facture) {
        String sql = "UPDATE FACTURE SET id_commande = ?, date_facture = ?, montant_ht = ?, taux_tva = ?, montant_tva = ?, montant_ttc = ?, statut = ?, notes = ? WHERE id_facture = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, facture.getIdCommande());
                pstmt.setDate(2, new java.sql.Date(facture.getDateFacture().getTime()));
                pstmt.setDouble(3, facture.getMontantHT());
                pstmt.setDouble(4, facture.getTauxTVA());
                pstmt.setDouble(5, facture.getMontantTVA());
                pstmt.setDouble(6, facture.getMontantTTC());
                pstmt.setString(7, facture.getStatut());
                pstmt.setString(8, facture.getNotes());
                pstmt.setInt(9, facture.getIdFacture());
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM FACTURE WHERE id_facture = ?";
        
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
        }
        return false;
    }
    
    @Override
    public List<Facture> findAll() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM FACTURE ORDER BY date_facture DESC";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données échouée");
                return factures;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Facture facture = new Facture();
                    facture.setIdFacture(rs.getInt("id_facture"));
                    facture.setIdCommande(rs.getInt("id_commande"));
                    facture.setDateFacture(rs.getDate("date_facture"));
                    facture.setMontantHT(rs.getDouble("montant_ht"));
                    facture.setTauxTVA(rs.getDouble("taux_tva"));
                    facture.setMontantTVA(rs.getDouble("montant_tva"));
                    facture.setMontantTTC(rs.getDouble("montant_ttc"));
                    facture.setStatut(rs.getString("statut"));
                    facture.setNotes(rs.getString("notes"));
                    
                    factures.add(facture);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factures;
    }
}
