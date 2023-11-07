package galactic.server.modules;


import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import galactic.server.modules.database.DbConnection;


public class ServerConnection {

    private static int serverPort, databasePort;

    private static ServerConnection server = null;

    private static DbConnection database;

    private static Set<String> userNames = new HashSet<>();

    private static Set<UserThread> userThreads = new HashSet<>();



    private ServerConnection(int port) {
        serverPort = port;
        execute();
    }





    private static void execute() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            DbConnection.GetInstance(databasePort);

            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket fileSocket = new DatagramSocket(serverPort);
            System.out.println("Server available at " + localHost.getHostAddress() + ":" + serverPort);

            while (true) {
                Socket messageSocket = serverSocket.accept();

                System.out.println("New user connected, ip: " + messageSocket.getInetAddress());

                UserThread newUser = new UserThread(messageSocket, fileSocket);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static ServerConnection GetInstance(int port, int dbPort) {
        if (server == null) {
            databasePort = dbPort;
            server = new ServerConnection(port);
        }
        return server;
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    public static void messageBroadcast(String message, Set<String> clientNames) {
        for (UserThread aUser : userThreads) {
            if (clientNames.contains(aUser.GetClientName())) {
                aUser.SendMessage(message);
            }
        }
    }


    /**
     * Stores username of the newly connected client.
     */
    public static void addUserName(String userName) {
        userNames.add(userName);
    }


    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    public static void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }


    public static Set<String> getUserNames() { return userNames; }


    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    public static boolean hasUsers() {
        return !userNames.isEmpty();
    }
}
