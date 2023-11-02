package galactic.server.modules.database.crud;


import galactic.server.modules.database.DbConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Read extends DbConnection {

    private static Read instance;



    private Read() {
        super(databasePort);
    }





//    public static Read GetInstance() {
//        if (instance == null) {
//            instance = new Read();
//        }
//        return instance;
//    }


    public static ResultSet User(String username) {
        try{
            String createQuery = "SELECT * FROM users WHERE username = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);

            statementResult = sqlStatement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }

        return statementResult;
    }


    public static byte[] FileData(String fileName, String roomName) {
        byte[] fileBytes = null;

        try{
            String createQuery = "SELECT file FROM files WHERE id = (SELECT id FROM files WHERE fileName = ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, fileName);

            statementResult = sqlStatement.executeQuery();
            fileBytes = statementResult.getBytes("file");
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }

        return fileBytes;
    }


    public static int RoomId(String roomName) {
        int roomId = 0;
        try{
            String createQuery = "SELECT id FROM room WHERE name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);

            statementResult = sqlStatement.executeQuery();

            roomId = statementResult.getInt(1);
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return roomId;
    }


    public static List<String> RoomFiles(String roomName) {
        List<String> fileList = new ArrayList<>();

        try{
            String createQuery = "SELECT fileName FROM files " +
                    "INNER JOIN room ON room.id = files.idRoom " +
                    "WHERE room.name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                fileList.add(statementResult.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return fileList;
    }


    public static Set<String> GroupUsers(String roomName) {
        Set<String> groupUsers = new HashSet<>();

        try{
            String createQuery = "SELECT username FROM users " +
                    "INNER JOIN room_participants ON users.id = room_participants.idUser" +
                    "INNER JOIN room ON room_participants.idRoom = room.id" +
                    "WHERE room.name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                groupUsers.add(statementResult.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return groupUsers;
    }
}
