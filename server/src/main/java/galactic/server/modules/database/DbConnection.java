package galactic.server.modules.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Singleton class, handles the connection to the database
 * Child classes holds multiple methods that interacts with the database
 */
public class DbConnection {

    protected static int databasePort;

    protected static DbConnection database = null;

    protected static Connection connection = null;

    protected static PreparedStatement sqlStatement = null;

    protected static ResultSet statementResult = null;



    protected DbConnection(int port) {
        databasePort = port;
        String url = "jdbc:mysql://127.0.0.1:" + databasePort + "/galactic_messenger";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, "root", "mysqlroot");
            System.out.println("Successfully connected to the database");
        } catch (Exception e) {
            System.out.println(e);
        }
    }





    public static DbConnection GetInstance(int port) {
        if (database == null) {
            database = new DbConnection(port);
        }
        return database;
    }
}
