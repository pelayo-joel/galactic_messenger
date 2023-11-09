package galactic.server.modules;


import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import galactic.server.modules.database.DbConnection;


/**
 * Singleton class that handles every client,
 * has methods to facilitate client connection/communication
 */
public class ServerConnection {

    private static int serverPort, databasePort;

    private static ServerConnection server = null;

    //Keeps in memory each client username
    private static Set<String> userNames = new HashSet<>();

    //Keeps in memory each connected client
    private static Set<UserThread> userThreads = new HashSet<>();



    /**
     * Constructs the server connection and starts port listening for clients
     *
     * @param port Specifies the server's port
     */
    private ServerConnection(int port) {
        serverPort = port;
        execute();
    }


    /**
     * Server's method that waits any client connection and threads it
     */
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
            //ex.printStackTrace(); //Used for debugging
        }
    }


    /**
     * Method which starts the server connection
     *
     * @param port Server port
     * @param dbPort Database port
     * @return the server connection
     */
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
    public static void addUserName(String userName) { userNames.add(userName); }


    /**
     * When a client is disconnected, removes the associated username and UserThread
     */
    public static void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }


    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    public static boolean hasUsers() { return !userNames.isEmpty(); }


    public static Set<String> getUserNames() { return userNames; }

}
