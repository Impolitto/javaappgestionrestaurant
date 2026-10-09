package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Commande;
import model.DetailCommande;

public class CommandeDAOImpl implements DAO<Commande> {
    private String heureColumnName;
    private String descriptionColumnName;

    private String getHeureColumn(Connection conn) throws SQLException {
        if (heureColumnName != null) {
            return heureColumnName;
        }

        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
                   + "WHERE TABLE_SCHEMA = DATABASE() "
                   + "AND TABLE_NAME = 'COMMANDE' "
                   + "AND COLUMN_NAME IN ('heure_commande', 'heure')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String column = rs.getString("COLUMN_NAME");
                if ("heure_commande".equalsIgnoreCase(column)) {
                    heureColumnName = "heure_commande";
                    return heureColumnName;
                }
                if ("heure".equalsIgnoreCase(column)) {
                    heureColumnName = "heure";
                }
            }
        }

        if (heureColumnName != null) {
            return heureColumnName;
        }

        throw new SQLException("La table COMMANDE ne contient ni 'heure_commande' ni 'heure'.");
    }

    private String getDescriptionColumn(Connection conn) throws SQLException {
        if (descriptionColumnName != null) {
            return descriptionColumnName;
        }

        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS "
                   + "WHERE TABLE_SCHEMA = DATABASE() "
                   + "AND TABLE_NAME = 'COMMANDE' "
                   + "AND COLUMN_NAME IN ('description_commande', 'description')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String column = rs.getString("COLUMN_NAME");
                if ("description_commande".equalsIgnoreCase(column)) {
                    descriptionColumnName = "description_commande";
                    return descriptionColumnName;
                }
                if ("description".equalsIgnoreCase(column)) {
                    descriptionColumnName = "description";
                }
            }
        }

        if (descriptionColumnName != null) {
            return descriptionColumnName;
        }

        // Create a dedicated description column once if it does not exist yet.
        // Avoid "IF NOT EXISTS" for compatibility with older MySQL/MariaDB versions.
        String alterSql = "ALTER TABLE COMMANDE ADD COLUMN description_commande VARCHAR(255) NULL";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(alterSql);
        } catch (SQLException e) {
            // Ignore duplicate-column race condition if another execution created it first.
            if (e.getErrorCode() != 1060) {
                throw e;
            }
        }

        descriptionColumnName = "description_commande";
        return descriptionColumnName;
    }
    
    @Override
    public boolean create(Commande commande) {
        if (commande == null) {
            throw new IllegalArgumentException("Commande invalide: objet null");
        }
        if (commande.getIdTable() <= 0) {
            throw new IllegalArgumentException("Commande invalide: table non selectionnee");
        }
        if (commande.getIdClient() <= 0) {
            throw new IllegalArgumentException("Commande invalide: client non selectionne");
        }
        if (commande.getDateCommande() == null) {
            throw new IllegalArgumentException("Commande invalide: date manquante");
        }
        if (commande.getHeureCommande() == null) {
            throw new IllegalArgumentException("Commande invalide: heure manquante");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             ) {
            conn.setAutoCommit(false);
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "INSERT INTO COMMANDE (id_table, id_client, date_commande, " + heureColumn + ", " + descriptionColumn + ", statut_commande, montant_total) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                pstmt.setInt(1, commande.getIdTable());
                pstmt.setInt(2, commande.getIdClient());
                pstmt.setDate(3, new java.sql.Date(commande.getDateCommande().getTime()));
                pstmt.setTime(4, commande.getHeureCommande());
                pstmt.setString(5, commande.getDescriptionCommande());
                pstmt.setString(6, commande.getStatutCommande());
                pstmt.setDouble(7, commande.getMontantTotal());
            
                int rows = pstmt.executeUpdate();
            
                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            commande.setIdCommande(rs.getInt(1));
                            // Sauvegarder les détails uniquement s'il y en a.
                            saveDetails(commande, conn);
                        }
                    }
                    conn.commit();
                    return true;
                }
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                "Creation commande impossible (SQLState=" + e.getSQLState() + ", Code=" + e.getErrorCode() + "): " + e.getMessage(),
                e
            );
        }
        return false;
    }
    
    private void saveDetails(Commande commande, Connection conn) throws SQLException {
        if (commande.getDetails() == null || commande.getDetails().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO DETAIL_COMMANDE (id_commande, id_plat, quantite, prix_unitaire, sous_total, notes_speciales) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (DetailCommande detail : commande.getDetails()) {
                pstmt.setInt(1, commande.getIdCommande());
                pstmt.setInt(2, detail.getIdPlat());
                pstmt.setInt(3, detail.getQuantite());
                pstmt.setDouble(4, detail.getPrixUnitaire());
                pstmt.setDouble(5, detail.getSousTotal());
                pstmt.setString(6, detail.getNotesSpeciales());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    @Override
    public Commande read(int id) {
        Commande commande = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "SELECT id_commande, id_table, id_client, date_commande, " + heureColumn + " AS heure_value, " + descriptionColumn + " AS description_value, statut_commande, montant_total FROM COMMANDE WHERE id_commande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        commande = new Commande();
                        commande.setIdCommande(rs.getInt("id_commande"));
                        commande.setIdTable(rs.getInt("id_table"));
                        commande.setIdClient(rs.getInt("id_client"));
                        commande.setDateCommande(rs.getDate("date_commande"));
                        commande.setHeureCommande(rs.getTime("heure_value"));
                        commande.setDescriptionCommande(rs.getString("description_value"));
                        commande.setStatutCommande(rs.getString("statut_commande"));
                        commande.setMontantTotal(rs.getDouble("montant_total"));
                    
                        // Charger les détails
                        commande.setDetails(loadDetails(id, conn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commande;
    }
    
    private List<DetailCommande> loadDetails(int idCommande, Connection conn) throws SQLException {
        List<DetailCommande> details = new ArrayList<>();
        String sql = "SELECT * FROM DETAIL_COMMANDE WHERE id_commande = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCommande);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailCommande detail = new DetailCommande();
                    detail.setIdDetail(rs.getInt("id_detail"));
                    detail.setIdCommande(rs.getInt("id_commande"));
                    detail.setIdPlat(rs.getInt("id_plat"));
                    detail.setQuantite(rs.getInt("quantite"));
                    detail.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    detail.setNotesSpeciales(rs.getString("notes_speciales"));
                    
                    details.add(detail);
                }
            }
        }
        return details;
    }
    
    @Override
    public boolean update(Commande commande) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "UPDATE COMMANDE SET id_table = ?, id_client = ?, date_commande = ?, " + heureColumn + " = ?, " + descriptionColumn + " = ?, statut_commande = ?, montant_total = ? WHERE id_commande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setInt(1, commande.getIdTable());
                pstmt.setInt(2, commande.getIdClient());
                pstmt.setDate(3, new java.sql.Date(commande.getDateCommande().getTime()));
                pstmt.setTime(4, commande.getHeureCommande());
                pstmt.setString(5, commande.getDescriptionCommande());
                pstmt.setString(6, commande.getStatutCommande());
                pstmt.setDouble(7, commande.getMontantTotal());
                pstmt.setInt(8, commande.getIdCommande());
            
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Supprimer d'abord les détails (à cause de la clé étrangère)
            String deleteDetails = "DELETE FROM DETAIL_COMMANDE WHERE id_commande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDetails)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Puis supprimer la commande
            String deleteCommande = "DELETE FROM COMMANDE WHERE id_commande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteCommande)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                
                conn.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    @Override
    public List<Commande> findAll() {
        List<Commande> commandes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "SELECT id_commande, id_table, id_client, date_commande, " + heureColumn + " AS heure_value, " + descriptionColumn + " AS description_value, statut_commande, montant_total FROM COMMANDE ORDER BY date_commande DESC, " + heureColumn + " DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
            
                while (rs.next()) {
                    Commande commande = new Commande();
                    commande.setIdCommande(rs.getInt("id_commande"));
                    commande.setIdTable(rs.getInt("id_table"));
                    commande.setIdClient(rs.getInt("id_client"));
                    commande.setDateCommande(rs.getDate("date_commande"));
                    commande.setHeureCommande(rs.getTime("heure_value"));
                    commande.setDescriptionCommande(rs.getString("description_value"));
                    commande.setStatutCommande(rs.getString("statut_commande"));
                    commande.setMontantTotal(rs.getDouble("montant_total"));
                
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    // Méthodes spécifiques
    public List<Commande> findByStatus(String status) {
        List<Commande> commandes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "SELECT id_commande, id_table, id_client, date_commande, " + heureColumn + " AS heure_value, " + descriptionColumn + " AS description_value, statut_commande, montant_total FROM COMMANDE WHERE statut_commande = ? ORDER BY date_commande DESC, " + heureColumn + " DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setString(1, status);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commande commande = new Commande();
                        commande.setIdCommande(rs.getInt("id_commande"));
                        commande.setIdTable(rs.getInt("id_table"));
                        commande.setIdClient(rs.getInt("id_client"));
                        commande.setDateCommande(rs.getDate("date_commande"));
                        commande.setHeureCommande(rs.getTime("heure_value"));
                        commande.setDescriptionCommande(rs.getString("description_value"));
                        commande.setStatutCommande(rs.getString("statut_commande"));
                        commande.setMontantTotal(rs.getDouble("montant_total"));
                    
                        commandes.add(commande);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public List<Commande> findByClient(int idClient) {
        List<Commande> commandes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "SELECT id_commande, id_table, id_client, date_commande, " + heureColumn + " AS heure_value, " + descriptionColumn + " AS description_value, statut_commande, montant_total FROM COMMANDE WHERE id_client = ? ORDER BY date_commande DESC, " + heureColumn + " DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setInt(1, idClient);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commande commande = new Commande();
                        commande.setIdCommande(rs.getInt("id_commande"));
                        commande.setIdTable(rs.getInt("id_table"));
                        commande.setIdClient(rs.getInt("id_client"));
                        commande.setDateCommande(rs.getDate("date_commande"));
                        commande.setHeureCommande(rs.getTime("heure_value"));
                        commande.setDescriptionCommande(rs.getString("description_value"));
                        commande.setStatutCommande(rs.getString("statut_commande"));
                        commande.setMontantTotal(rs.getDouble("montant_total"));
                    
                        commandes.add(commande);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public List<Commande> findByDate(java.sql.Date date) {
        List<Commande> commandes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String heureColumn = getHeureColumn(conn);
            String descriptionColumn = getDescriptionColumn(conn);
            String sql = "SELECT id_commande, id_table, id_client, date_commande, " + heureColumn + " AS heure_value, " + descriptionColumn + " AS description_value, statut_commande, montant_total FROM COMMANDE WHERE date_commande = ? ORDER BY " + heureColumn + " DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setDate(1, date);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commande commande = new Commande();
                        commande.setIdCommande(rs.getInt("id_commande"));
                        commande.setIdTable(rs.getInt("id_table"));
                        commande.setIdClient(rs.getInt("id_client"));
                        commande.setDateCommande(rs.getDate("date_commande"));
                        commande.setHeureCommande(rs.getTime("heure_value"));
                        commande.setDescriptionCommande(rs.getString("description_value"));
                        commande.setStatutCommande(rs.getString("statut_commande"));
                        commande.setMontantTotal(rs.getDouble("montant_total"));
                    
                        commandes.add(commande);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
    
    public double getChiffreAffairesTotal() {
        String sql = "SELECT SUM(montant_total) as total FROM COMMANDE WHERE statut_commande = 'REGLEE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}