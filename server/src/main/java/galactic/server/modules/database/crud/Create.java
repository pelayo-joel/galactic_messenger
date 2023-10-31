package galactic.server.modules.database.crud;


import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import galactic.server.modules.database.DbConnection;


public class Create extends DbConnection {

    private static Create instance;



    private Create() {
        super(databasePort);
    }





//    public static Create GetInstance() {
//        if (instance == null) {
//            instance = new Create();
//        }
//        return instance;
//    }


    public static void InsertUser(String username, String userPassword) {
        try{
            String createQuery = "INSERT INTO users (username, password) VALUES (?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);
            sqlStatement.setString(2, userPassword);

            statementResult = sqlStatement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void InsertRoom(String roomName, boolean roomType, boolean secure, String roomPassword) {
        try{
            String createQuery = "INSERT INTO room (name, type, secure, password) VALUES (?, ?, ?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);
            sqlStatement.setInt(2, (roomType) ? 1 : 0);
            sqlStatement.setInt(3, (secure) ? 1 : 0);
            sqlStatement.setString(4, roomPassword);

            statementResult = sqlStatement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void InsertUserInRoom(String roomName, String username) {
        try{
            String createQuery = "INSERT INTO room_participants (idRoom, idUser) VALUES (?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            ResultSet userReader = Read.User(username);
            int userId = userReader.getInt(1), roomId = Read.RoomId(roomName);

            sqlStatement.setInt(1, roomId);
            sqlStatement.setInt(2, userId);

            sqlStatement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    public static void InsertMessage(String userMessage, String username, String roomName) {
//        try{
//            Object dateParam = new Timestamp(System.currentTimeMillis());
//            statementResult = Read.User(username);
//            int userId = statementResult.getInt(1), roomId = Read.RoomId(roomName);
//
//            String createQuery = "INSERT INTO messages (date, message, idRoom, idUser) VALUES (?, ?, ?, ?);";
//            sqlStatement = connection.prepareStatement(createQuery);
//
//
//            sqlStatement.setObject(1, dateParam);
//            sqlStatement.setString(2, userMessage);
//            sqlStatement.setInt(3, roomId);
//            sqlStatement.setInt(4, userId);
//
//            sqlStatement.executeQuery();
//        }
//        catch (SQLException e) {
//            System.out.println("Error in database: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }


    public static void InsertFile(byte[] fileUploadBytes, String groupName) {
        Object dateParam = new Timestamp(System.currentTimeMillis());
        int roomId = Read.RoomId(groupName);


        String createQuery = "INSERT INTO files (date, idRoom, file) VALUES (?, ?, ?, ?);";
        //sqlStatement = connection.prepareStatement(createQuery);


        //sqlStatement.setObject(1, dateParam);
        //sqlStatement.setString(2, roomId);
        //sqlStatement.setBlob(3, fileUploadBytes);

        //sqlStatement.executeQuery();
    }
}
