import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton que realiza la conexión a la base de datos.
 * 
 * @author Orlando Ledesma Rincón
 *
 */
public class ConexionMySQL {

    private static final ConexionMySQL conexionInstance = new ConexionMySQL();

    private Connection connection;

    /**
     * Constructor privado que realiza recupera la información para realizar la conexion desde un archivo properties y
     * realiza la conexión a la base de datos.
     */
    private ConexionMySQL() {

        Properties prop = new Properties();
        try (FileInputStream is = new FileInputStream("db" + Main.getType() + ".properties")) {
            prop.load(is);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: No se encontro el archivo \"db" + Main.getType() + ".properties\"");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String db_name = prop.getProperty("db_name");
        String db_host = prop.getProperty("db_host");
        String db_username = prop.getProperty("db_username");
        String db_password = prop.getProperty("db_password");

        String url = "jdbc:mysql://" + db_host + "/" + db_name;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, db_username, db_password);
            System.out.println("Conexion a la base de datos exitosa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public Connection getConnection() {
        return connection;
    }


    public static ConexionMySQL getInstance() {
        return conexionInstance;
    }
}
