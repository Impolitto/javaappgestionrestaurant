package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TableRestaurant;

public class TableDAOImpl implements DAO<TableRestaurant> {
    
    @Override
    public boolean create(TableRestaurant table) {
        String sql = "INSERT INTO TABLE_RESTAURANT (numero_table, capacite, emplacement, statut) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, table.getNumeroTable());
            pstmt.setInt(2, table.getCapacite());
            pstmt.setString(3, table.getEmplacement());
            pstmt.setString(4, table.getStatut());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        table.setIdTable(rs.getInt(1));
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
    public TableRestaurant read(int id) {
        String sql = "SELECT * FROM TABLE_RESTAURANT WHERE id_table = ?";
        TableRestaurant table = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    table = new TableRestaurant();
                    table.setIdTable(rs.getInt("id_table"));
                    table.setNumeroTable(rs.getString("numero_table"));
                    table.setCapacite(rs.getInt("capacite"));
                    table.setEmplacement(rs.getString("emplacement"));
                    table.setStatut(rs.getString("statut"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }
    
    @Override
    public boolean update(TableRestaurant table) {
        String sql = "UPDATE TABLE_RESTAURANT SET numero_table = ?, capacite = ?, emplacement = ?, statut = ? WHERE id_table = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, table.getNumeroTable());
            pstmt.setInt(2, table.getCapacite());
            pstmt.setString(3, table.getEmplacement());
            pstmt.setString(4, table.getStatut());
            pstmt.setInt(5, table.getIdTable());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM TABLE_RESTAURANT WHERE id_table = ?";
        
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
    public List<TableRestaurant> findAll() {
        List<TableRestaurant> tables = new ArrayList<>();
        String sql = "SELECT * FROM TABLE_RESTAURANT ORDER BY numero_table";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TableRestaurant table = new TableRestaurant();
                table.setIdTable(rs.getInt("id_table"));
                table.setNumeroTable(rs.getString("numero_table"));
                table.setCapacite(rs.getInt("capacite"));
                table.setEmplacement(rs.getString("emplacement"));
                table.setStatut(rs.getString("statut"));
                
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
}
