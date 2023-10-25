package galactic.server.modules;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import galactic.server.modules.database.DbConnection;


public class ServerConnection {
    private int port;
    private DbConnection database;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public ServerConnection(int port) {
        this.port = port;
        this.execute();
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            //database = new DbConnection(port);

            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Server available at " + localHost.getHostAddress() + ":" + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
                //System.out.println("checking");
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, Set<String> clientNames) {
        for (UserThread aUser : userThreads) {
            if (clientNames.contains(aUser.GetClientName())) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}
