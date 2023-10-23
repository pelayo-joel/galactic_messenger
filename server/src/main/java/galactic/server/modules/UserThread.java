package galactic.server.modules;


import java.io.*;
import java.net.*;
import java.util.List;

import galactic.server.modules.commands.*;


/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 */
public class UserThread extends Thread {
    private boolean connected = false;
    private String clientName, serverMessage, clientCommand;
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

                List<String> userInputs = (List<String>) input.readObject();

                CommandHandler(userInputs);

            } while (!this.clientCommand.equals("/disconnect"));

            this.sendMessage("Logging out, see you soon !");
            server.removeUser(this.clientName, this);
            socket.close();
            System.out.println(this.clientName + " disconnected");
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
                if (!this.connected) {
                    UserConnection clientConnection = new UserConnection(clientInput);
                    this.clientName = clientConnection.getUsername();
                    this.serverMessage = clientConnection.CommandHandler();
                    this.sendMessage(this.serverMessage);
                    this.connected = true;
                }
                else { this.sendMessage("You're already logged in..."); }
            }
            //server.broadcast(this.serverMessage, this);
            case "/private_chat", "/accept", "/decline", "/msg", "/exit_private_chat" -> {
                if (this.connected) {
                    Chat chat = new Chat(clientInput);
                    this.serverMessage = chat.CommandHandler();
                    this.sendMessage("Tried to " + this.clientCommand);
                }
                else { this.sendMessage("Log in before chatting with other users"); }
            }
            //server.broadcast(this.serverMessage, this);
            case "/create_group", "/join_group", "/msg_group", "/exit_group", "/create_secure_group", "/join_secure_group" -> {
                if (this.connected) {
                    GroupChat group = new GroupChat(clientInput);
                    this.serverMessage = group.CommandHandler();
                    this.sendMessage("Tried to " + this.clientCommand);
                }
                else { this.sendMessage("Log in before chatting with a group"); }
            }
            //server.broadcast(this.serverMessage, this);
            case "/upload", "/list_files", "/download" -> {
                if (this.connected) {
                    FileTransmission fileTunnel = new FileTransmission(clientInput);
                    this.serverMessage = fileTunnel.CommandHandler();
                    this.sendMessage("Tried to " + this.clientCommand);
                }
                else { this.sendMessage("Log in before interacting with some files"); }
            }
            //server.broadcast(this.serverMessage, this);
            case "/online_users" -> {
                if (this.connected) {
                    printUsers();
                    this.sendMessage("Tried to " + this.clientCommand);
                }
                else { this.sendMessage("Log in before listing online users"); }
            }
            //server.broadcast(this.serverMessage, this);
            default -> {
                this.sendMessage("Error when reading command or might be invalid: " + this.clientCommand);
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
