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


    public static String User(String username, String usersField) {
        String fieldValue = "";
        try{
            String createQuery = "SELECT * FROM users WHERE username = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);

            statementResult = sqlStatement.executeQuery();
            while(statementResult.next()) {
                fieldValue = statementResult.getString(usersField);
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }

        return fieldValue;
    }


    public static String SecureGroupPassword(String groupName) {
        String groupPassword = "";
        try{
            String createQuery = "SELECT password FROM room WHERE name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, groupName);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                groupPassword = statementResult.getString("password");
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }

        return groupPassword;
    }


    public static List<String> AllUsernames() {
        List<String> userList = new ArrayList<>();

        try{
            String createQuery = "SELECT username FROM user;";
            sqlStatement = connection.prepareStatement(createQuery);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                userList.add(statementResult.getString("username"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }


    public static byte[] FileData(String fileName) {
        byte[] fileBytes = null;

        try{
            String createQuery = "SELECT file FROM files WHERE id = (SELECT id FROM files WHERE fileName = ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, fileName);

            statementResult = sqlStatement.executeQuery();
            while(statementResult.next()) {
                fileBytes = statementResult.getBytes("file");
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }

        return fileBytes;
    }


    public static int GroupId(String roomName) {
        int roomId = 0;
        try{
            String createQuery = "SELECT id FROM room WHERE name = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                roomId = statementResult.getInt(1);
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return roomId;
    }


    public static int ChatId(String clientName, String username) {
        int roomId = 0;
        try{
            String createQuery = "SELECT id FROM room " +
                    "INNER JOIN room_participants ON room_participants.idRoom = room.id " +
                    "INNER JOIN user ON user.id = room_particpants.idUser " +
                    "WHERE room.type = 0 AND user.username = ? OR user.username = ?;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, clientName);
            sqlStatement.setString(2, username);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                roomId = statementResult.getInt(1);
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return roomId;
    }


    public static List<String> AllGroupNames() {
        List<String> groupList = new ArrayList<>();

        try{
            String createQuery = "SELECT name FROM room;";
            sqlStatement = connection.prepareStatement(createQuery);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                groupList.add(statementResult.getString("name"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return groupList;
    }


    public static List<String> GroupFiles(String roomName) {
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


    public static List<String> ChatFiles(String username) {
        List<String> fileList = new ArrayList<>();

        try{
            String createQuery = "SELECT fileName FROM files " +
                    "INNER JOIN room ON room.id = files.idRoom " +
                    "INNER JOIN room_participants ON room_participants.idRoom = room.id " +
                    "INNER JOIN user ON user.id = room_participants.idUser " +
                    "WHERE user.username = ? AND room.type = 0;";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);

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
                    "INNER JOIN room_participants ON users.id = room_participants.idUser " +
                    "INNER JOIN room ON room_participants.idRoom = room.id " +
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





    public static Set<String> AllUsers() {
        Set<String> allUsers = new HashSet<>();

        try{
            String createQuery = "SELECT username FROM users;";
            sqlStatement = connection.prepareStatement(createQuery);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                allUsers.add(statementResult.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return allUsers;
    }


    public static Set<String> AllGroup() {
        Set<String> allGroup = new HashSet<>();

        try{
            String createQuery = "SELECT name FROM room;";
            sqlStatement = connection.prepareStatement(createQuery);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                allGroup.add(statementResult.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return allGroup;
    }


    public static Set<String> AllPrivateChat() {
        Set<String> allChat = new HashSet<>();

        try{
            String createQuery = "SELECT id FROM room WHERE type = 0;";
            sqlStatement = connection.prepareStatement(createQuery);

            statementResult = sqlStatement.executeQuery();

            while(statementResult.next()) {
                allChat.add(statementResult.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
        return allChat;
    }
}
