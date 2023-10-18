package galactic.server.modules;

import java.io.*;
import java.net.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserThread extends Thread {
    private String userName, serverMessage, clientCommand;
    private Socket socket;
    private final ServerConnection server;
    private PrintWriter writer;

    public UserThread(Socket socket, ServerConnection server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(this.socket.getOutputStream());

            String[] userInput = (String[]) input.readObject();
            writer = new PrintWriter(output, true);

            do {
                CommandHandler(userInput);
            } while (!clientCommand.equals("/disconnect"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);


        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erreur survenue sur un thread utilisateur: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void CommandHandler(String[] userInput) {
        this.clientCommand = userInput[0];

        switch (this.clientCommand) {
            case "/register":
                this.serverMessage = "[" + userName + "]: has been registered";
                server.broadcast(this.serverMessage, this);
                break;
            case "/login":
                server.broadcast(this.serverMessage, this);
                break;
            case "/private_chat":
                server.broadcast(this.serverMessage, this);
                break;
            case "/accept":
                server.broadcast(this.serverMessage, this);
                break;
            case "/exit_private_chat":
                server.broadcast(this.serverMessage, this);
                break;
            case "/create_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/join_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/msg_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/exit_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/upload":
                server.broadcast(this.serverMessage, this);
                break;
            case "/list_files":
                server.broadcast(this.serverMessage, this);
                break;
            case "/download":
                server.broadcast(this.serverMessage, this);
                break;
            case "/create_secure_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/join_secure_group":
                server.broadcast(this.serverMessage, this);
                break;
            case "/online_users":
                printUsers();
                server.broadcast(this.serverMessage, this);
                break;
            default:
                break;
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    private void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
