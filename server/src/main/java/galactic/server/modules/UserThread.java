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

        if (this.connected) {
            Commands clientRequest = null;

            switch (this.clientCommand) {
                case "/private_chat", "/accept", "/decline", "/msg", "/exit_private_chat" ->
                        clientRequest = new Chat(clientInput, this.clientName);

                case "/create_group", "/join_group", "/msg_group", "/exit_group", "/create_secure_group", "/join_secure_group" ->
                        clientRequest = new GroupChat(clientInput, this.clientName);

                case "/upload", "/list_files", "/download" ->
                        clientRequest = new FileTransmission(clientInput, this.clientName);

                case "/online_users" ->
                        this.printUsers();
            }

            if (clientRequest != null) {
                this.serverMessage = clientRequest.CommandHandler();
                if (!this.serverMessage.startsWith("Invalid")) {
                    this.sendMessage(clientRequest.ServerResponse());
                    server.broadcast(this.serverMessage, clientRequest.GetReceivingParty());
                }
                else { this.sendMessage(this.serverMessage); }
            }
            else if (this.clientCommand.equals("/register") || this.clientCommand.equals("/login")) {
                this.sendMessage("You're already logged in, '/disconnect' or '/quit' to quit and log out");
            }
            else if (this.clientCommand.equals("/disconnect") || this.clientCommand.equals("/quit")) {
                this.connected = false;
                this.sendMessage("Logging you out, see you soon !");
            }
            else {
                this.sendMessage("Invalid command: " + this.clientCommand + "\n" +
                            "For 1-to-1 messages: '/private_chat', '/accept', '/decline', '/msg', '/exit_private_chat'\n" +
                            "For group command: '/create_group', '/join_group', '/msg_group', '/exit_group', '/create_secure_group', '/join_secure_group'\n" +
                            "For files command: '/upload', '/list_files', '/download'\n" +
                            "To print online users: '/online_users'\n" +
                            "To quit and log out from the program: '/disconnect', '/quit'");
            }
        }

        else if (this.clientCommand.equals("/login") || this.clientCommand.equals("/register")) {
            Commands clientConnection = new UserAuthentication(clientInput);
            this.clientName = clientConnection.getClientName();
            this.serverMessage = clientConnection.CommandHandler();

            if (!this.serverMessage.startsWith("Invalid") || !this.serverMessage.equals("Authentication failed")) {
                server.addUserName(this.clientName);
                this.connected = true;
            }
            this.sendMessage(this.serverMessage);
        }

        else {
            this.sendMessage("Invalid command: " + this.clientCommand + "\n" +
                    "Please sign in with '/register' if you're new here or '/login', '/quit' to exit the program");
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
