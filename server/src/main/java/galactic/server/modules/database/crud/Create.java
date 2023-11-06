package galactic.server.modules.database.crud;


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


    public static void User(String username, String userPassword) {
        try{
            String createQuery = "INSERT INTO users (username, password) VALUES (?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, username);
            sqlStatement.setString(2, userPassword);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void PrivateChat(String clientName, String username) {
        Create.InsertRoom(null, false, false, null);

        int idRoom = Read.ChatId(clientName, username);
        Create.InsertUserInRoom(idRoom, clientName);
        Create.InsertUserInRoom(idRoom, username);
    }


    public static void GroupChat(String groupCreator, String groupName, boolean secure, String password) {
        Create.InsertRoom(groupName, true, secure, password);

        int idRoom = Read.GroupId(groupName);
        Create.InsertUserInRoom(idRoom, groupCreator);
    }


    public static void UserInGroup(String groupName, String username) {
        int idRoom = Read.GroupId(groupName);
        Create.InsertUserInRoom(idRoom, username);
    }


    public static void FileInChat(byte[] fileData, String fileName, String clientName, String username) {
        Create.InsertFile(fileData, fileName, Read.ChatId(clientName, username));
    }


    public static void FileInGroup(byte[] fileData, String fileName, String groupName) {
        Create.InsertFile(fileData, fileName, Read.GroupId(groupName));
    }




    private static void InsertRoom(String roomName, boolean roomType, boolean secure, String roomPassword) {
        try{
            String createQuery = "INSERT INTO room (name, type, secure, password) VALUES (?, ?, ?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setString(1, roomName);
            sqlStatement.setInt(2, (roomType) ? 1 : 0);
            sqlStatement.setInt(3, (secure) ? 1 : 0);
            sqlStatement.setString(4, roomPassword);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void InsertUserInRoom(int roomId, String username) {
        try{
            String userFieldValue = Read.User(username, "id");
            int userId = Integer.parseInt(userFieldValue);

            String createQuery = "INSERT INTO room_participants (idRoom, idUser) VALUES (?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setInt(1, roomId);
            sqlStatement.setInt(2, userId);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error in database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void InsertFile(byte[] file, String fileName, int idRoom) {
        try {
            Object dateParam = new Timestamp(System.currentTimeMillis());

            String createQuery = "INSERT INTO files (date, fileName, file, idRoom) VALUES (?, ?, ?, ?);";
            sqlStatement = connection.prepareStatement(createQuery);

            sqlStatement.setObject(1, dateParam);
            sqlStatement.setString(2, fileName);
            sqlStatement.setBytes(3, file);
            sqlStatement.setInt(4, idRoom);

            sqlStatement.execute();
        }
        catch (SQLException e) {
            System.out.println("Error when storing the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
