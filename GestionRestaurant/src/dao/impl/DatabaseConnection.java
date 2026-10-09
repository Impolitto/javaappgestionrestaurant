package impl;

import util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        try {
            if (Config.DB_USER == null || Config.DB_USER.isBlank()) {
                throw new IllegalStateException("DB_USER est vide. Configurez DB_USER ou la propriete -Ddb.user.");
            }
            Class.forName(Config.DB_DRIVER);
            DriverManager.setLoginTimeout(5);
            Connection connection = DriverManager.getConnection(
                Config.DB_URL, 
                Config.DB_USER, 
                Config.DB_PASSWORD
            );
            System.out.println(" Connexion à MySQL établie !");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println(" Driver MySQL non trouvé !");
            System.err.println("Assurez-vous que le driver MySQL (mysql-connector-java) est dans le classpath");
            System.err.println("Détails: " + e.getMessage());
            throw new IllegalStateException("MySQL driver introuvable: " + Config.DB_DRIVER, e);
        } catch (SQLException e) {
            System.err.println(" Erreur de connexion MySQL !");
            System.err.println("Vérifiez:");
            System.err.println("- Que le serveur MySQL est démarré");
            System.err.println("- Que la base 'GestionRestaurant' existe");
            System.err.println("- Que les identifiants dans Config.java sont corrects");
            System.err.println("- URL: " + Config.DB_URL);
            System.err.println("- User: " + Config.DB_USER);
            System.err.println("- SQLState: " + e.getSQLState());
            System.err.println("- ErrorCode: " + e.getErrorCode());
            System.err.println("Détails: " + e.getMessage());
            throw new IllegalStateException("Connexion MySQL impossible: " + e.getMessage(), e);
        }
    }
}
