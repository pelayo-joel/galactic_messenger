package galactic.server.modules;


import java.io.*;
import java.net.*;
import java.util.List;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.implementations.*;


/**=-
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
            ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream(), true);

            do {
                List<String> userInputs = (List<String>) input.readObject();
                CommandHandler(userInputs);

            } while (!this.clientCommand.equals("/disconnect") && !this.clientCommand.equals("/quit"));

            server.removeUser(this.clientName, this);
            socket.close();
            System.out.println(this.clientName + " disconnected");
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error on user thread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String GetClientName() {
        return this.clientName;
    }



    private void CommandHandler(List<String> clientInput) {
        this.clientCommand = clientInput.get(0);

        switch (this.clientCommand) {

            case "/register", "/login" -> {
                if (!this.connected) {
                    UserAuthentication clientConnection = new UserAuthentication(clientInput);
                    this.clientName = clientConnection.getUsername();
                    this.serverMessage = clientConnection.CommandHandler();
                    this.sendMessage(this.serverMessage);

                    if (!this.serverMessage.equals("Authentication failed")) {
                        server.addUserName(clientName);
                        this.connected = true;
                    }
                }
                else { this.sendMessage("You're already logged in..."); }
            }

            case "/private_chat", "/accept", "/decline", "/msg", "/exit_private_chat" -> {
                if (this.connected) {
                    Commands chat = new Chat(clientInput, this.clientName);
                    this.serverMessage = chat.CommandHandler();
                    this.sendMessage(chat.ServerResponse());
                    server.broadcast(this.serverMessage, chat.GetReceivingParty());
                }
                else { this.sendMessage("Log in before chatting with other users"); }
            }

            case "/create_group", "/join_group", "/msg_group", "/exit_group", "/create_secure_group", "/join_secure_group" -> {
                if (this.connected) {
                    Commands group = new GroupChat(clientInput, this.clientName);
                    this.serverMessage = group.CommandHandler();
                    this.sendMessage(group.ServerResponse());
                    server.broadcast(this.serverMessage, group.GetReceivingParty());
                }
                else { this.sendMessage("Log in before chatting with a group"); }
            }

            case "/upload", "/list_files", "/download" -> {
                if (this.connected) {
                    Commands fileTunnel = new FileTransmission(clientInput, this.clientName);
                    this.serverMessage = fileTunnel.CommandHandler();
                    this.sendMessage(fileTunnel.ServerResponse());
                    server.broadcast(this.serverMessage, fileTunnel.GetReceivingParty());
                }
                else { this.sendMessage("Log in before interacting with some files"); }
            }

            case "/online_users" -> {
                if (this.connected) {
                    this.printUsers();
                    this.sendMessage("Tried to " + this.clientCommand);
                }
                else { this.sendMessage("Log in before listing online users"); }
            }

            case "/disconnect", "/quit" -> {
                if (this.connected) {
                    this.sendMessage("Logging out, see you soon !");
                    this.connected = false;
                }
                else { this.sendMessage("Quitting"); }
            }

            default -> this.sendMessage("Error when reading command or might be invalid: " + this.clientCommand);
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
