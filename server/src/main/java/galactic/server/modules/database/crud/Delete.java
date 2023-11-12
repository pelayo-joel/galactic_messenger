package galactic.server.modules.database.crud;


import galactic.server.modules.database.DbConnection;

import java.sql.SQLException;


/**
 * Inherits 'DbConnection', Handles the DELETE operations for the database
 */
public class Delete extends DbConnection {



    private Delete() {
        super(databasePort);
    }





    public static void UserFromGroup(String groupName, String username) {
        try {
            String createQuery = "DELETE FROM room_participants " +
                    "INNER JOIN users ON room_participants.idUser = users.id " +
                    "INNER JOIN room ON room_participants.idRoom = room.id " +
                    "WHERE users.username = ? AND room.name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);
            sqlStatement.setString(2, groupName);

            sqlStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void PrivateChat(String clientName, String username) {
        int roomId = Read.ChatId(clientName, username);
        try {
            String createQuery = "DELETE FROM room_participants WHERE idRoom = ?;";
            sqlStatement = connection.prepareStatement(createQuery);
            sqlStatement.setInt(1, roomId);
            sqlStatement.executeQuery();

            createQuery = "DELETE FROM room WHERE idRoom = ?;";
            sqlStatement = connection.prepareStatement(createQuery);
            sqlStatement.setInt(1, roomId);
            sqlStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
