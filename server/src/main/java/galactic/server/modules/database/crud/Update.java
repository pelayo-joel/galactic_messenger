package galactic.server.modules.database.crud;


import galactic.server.modules.database.DbConnection;

import java.sql.SQLException;


public class Update extends DbConnection {

    private static Update instance;



    private Update() {
        super(databasePort);
    }





    public static Update GetInstance() {
        if (instance == null) {
            instance = new Update();
        }
        return instance;
    }


    public static void UserPassword(String username, String newPassword) {
        try{
            String createQuery = "UPDATE users SET password = ? WHERE username = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, newPassword);
            sqlStatement.setString(2, username);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void GroupPassword(String groupName, String newPassword) {
        try{
            String createQuery = "UPDATE room SET password = ? WHERE name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, newPassword);
            sqlStatement.setString(2, groupName);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}