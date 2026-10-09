package impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Client;

public class ClientDAOImpl implements DAO<Client> {
    
    @Override
    public boolean create(Client client) {
        String sql = "INSERT INTO CLIENT (nom, prenom, email, telephone, nb_visites) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getTelephone());
            pstmt.setInt(5, client.getNbVisites());
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        client.setIdClient(rs.getInt(1));
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
    public Client read(int id) {
        String sql = "SELECT * FROM CLIENT WHERE id_client = ?";
        Client client = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    client = new Client();
                    client.setIdClient(rs.getInt("id_client"));
                    client.setNom(rs.getString("nom"));
                    client.setPrenom(rs.getString("prenom"));
                    client.setEmail(rs.getString("email"));
                    client.setTelephone(rs.getString("telephone"));
                    client.setNbVisites(rs.getInt("nb_visites"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }
    
    @Override
    public boolean update(Client client) {
        String sql = "UPDATE CLIENT SET nom = ?, prenom = ?, email = ?, telephone = ?, nb_visites = ? WHERE id_client = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getTelephone());
            pstmt.setInt(5, client.getNbVisites());
            pstmt.setInt(6, client.getIdClient());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM CLIENT WHERE id_client = ?";
        
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
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENT ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setTelephone(rs.getString("telephone"));
                client.setNbVisites(rs.getInt("nb_visites"));
                
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
