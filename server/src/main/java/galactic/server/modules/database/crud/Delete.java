package galactic.server.modules.database.crud;


import galactic.server.modules.database.DbConnection;

import java.sql.SQLException;


public class Delete extends DbConnection {

    private static Delete instance;



    private Delete() {
        super(databasePort);
    }





    public static Delete GetInstance() {
        if (instance == null) {
            instance = new Delete();
        }
        return instance;
    }


    public static void UserFromGroup(String groupName, String username) {
        try {
            String createQuery = "DELETE FROM room_participants" +
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
}
