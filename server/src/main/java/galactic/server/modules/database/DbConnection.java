package galactic.server.modules.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DbConnection {
    private Connection connection = null;
    private PreparedStatement sqlStatement = null;
    private ResultSet statementResult = null;



    public DbConnection(int port) {
        String url = "jdbc:mysql://localhost:" + port + "/galactic_messenger";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, "root", "mysqlroot");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
