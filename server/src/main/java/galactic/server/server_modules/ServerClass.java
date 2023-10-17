package galactic.server.server_modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServerClass {
    private Connection connection = null;
    private PreparedStatement sqlStatement = null;
    private ResultSet statementResult = null;



    public ServerClass(int port) {
        String url = "jdbc:mysql://localhost:3000/galactic_messenger";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, "root", "mysqlroot");
            System.out.println("Connected: " + this.connection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
