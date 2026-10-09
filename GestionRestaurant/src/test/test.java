package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author alaak
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/?user=root";
        String user = "root";
        String password = "root";
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
            connection.close();
        } catch (SQLException e) { e.printStackTrace();
            System.out.println("Échec de la connexion !");
        }  
    }
    
}
