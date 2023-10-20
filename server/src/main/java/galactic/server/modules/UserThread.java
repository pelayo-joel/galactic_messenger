package galactic.server.modules;


import java.io.*;
import java.net.*;
import java.util.List;

import galactic.server.modules.commands.*;


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
            System.out.println("User connection established");

            do {

                ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());
                this.writer = new PrintWriter(socket.getOutputStream(), true);
//            System.out.println("Return signal");
//            this.sendMessage("Connection established");
//            System.out.println("Return signal");

                List<String> userInputs = (List<String>) input.readObject();

                CommandHandler(userInputs);

            } while (!this.clientCommand.equals("/disconnect"));

            server.removeUser(userName, this);
            socket.close();
            System.out.println("disconnected");

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("Erreur survenue sur un thread utilisateur: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void CommandHandler(List<String> clientInput) {
        this.clientCommand = clientInput.get(0);

        switch (this.clientCommand) {
            case "/register", "/login" -> {
                UserConnection clientConnection = new UserConnection(clientInput);
                this.serverMessage = clientConnection.CommandHandler();
                this.sendMessage("Hashed password: " + this.serverMessage);
            }
            //server.broadcast(this.serverMessage, this);
            case "/private_chat", "/accept", "/decline", "/msg", "/exit_private_chat" -> {
                Chat chat = new Chat(clientInput);
                this.serverMessage = chat.CommandHandler();
                this.sendMessage("Tried to " + this.clientCommand);
            }
            //server.broadcast(this.serverMessage, this);
            case "/create_group", "/join_group", "/msg_group", "/exit_group", "/create_secure_group", "/join_secure_group" -> {
                GroupChat group = new GroupChat(clientInput);
                this.serverMessage = group.CommandHandler();
                this.sendMessage("Tried to " + this.clientCommand);
            }
            //server.broadcast(this.serverMessage, this);
            case "/upload", "/list_files", "/download" -> {
                FileTransmission fileTunnel = new FileTransmission(clientInput);
                this.serverMessage = fileTunnel.CommandHandler();
                this.sendMessage("Tried to " + this.clientCommand);
            }
            //server.broadcast(this.serverMessage, this);
            case "/online_users" -> {
                printUsers();
                this.sendMessage("Tried to " + this.clientCommand);
            }
            //server.broadcast(this.serverMessage, this);
            default -> {
                this.sendMessage("Error when reading command: " + this.clientCommand);
            }
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    private void printUsers() {
        if (server.hasUsers()) {
            this.writer.println("Connected users: " + server.getUserNames());
        } else {
            this.writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        this.writer.println(message);
    }
}
