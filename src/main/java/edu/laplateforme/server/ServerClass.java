package edu.laplateforme.server;

import java.sql.*;

public class ServerClass {
    private Connection connection = null;
    private PreparedStatement sqlStatement = null;
    private ResultSet statementResult = null;



    public ServerClass() {
        String url = "jdbc:mysql://localhost:3000/galactic_messenger";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, "root", "sqlroot");
            System.out.println("Connected: " + this.connection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
