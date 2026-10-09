package util;

public class Config {
    public static final String DB_URL = readSetting(
        "db.url",
        "DB_URL",
        "jdbc:mysql://localhost:3306/GestionRestaurant?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    public static final String DB_USER = readSetting("db.user", "DB_USER", "root");
    public static final String DB_PASSWORD = readSetting("db.password", "DB_PASSWORD", "root");
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private Config() {}

    private static String readSetting(String systemPropertyKey, String envKey, String defaultValue) {
        String fromSystemProperty = System.getProperty(systemPropertyKey);
        if (fromSystemProperty != null && !fromSystemProperty.isBlank()) {
            return fromSystemProperty;
        }

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        return defaultValue;
    }
}