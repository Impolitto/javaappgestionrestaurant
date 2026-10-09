
import util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(Config.DB_DRIVER);
                connection = DriverManager.getConnection(
                    Config.DB_URL, 
                    Config.DB_USER, 
                    Config.DB_PASSWORD
                );
                System.out.println("✅ Connexion à MySQL établie !");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Driver MySQL non trouvé !");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion MySQL !");
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔌 Connexion fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}