package galactic.server.modules;


import java.io.*;
import java.net.*;
import java.util.List;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.implementations.*;
import galactic.server.modules.commands.miscellaneous.Colors;


/**
 * Client threading, handles a single client and its inputs
 */
public class UserThread extends Thread {

    private final Socket socket;

    private final DatagramSocket fileSocket;

    //Locks the user with '/login' or '/register' before using any other commands
    private boolean connected = false;

    private byte[] fileBuffer = new byte[200000000];

    private String clientName, serverMessage, clientCommand;

    private PrintWriter writer;


    /**
     * Construct a thread for the client, needs a TCP and UDP socket
     *
     * @param messageTransferSocket Specifies the client's TCP socket
     * @param fileTransferSocket Specifies the server's UDP socket
     */
    public UserThread(Socket messageTransferSocket, DatagramSocket fileTransferSocket) {
        this.socket = messageTransferSocket;
        this.fileSocket = fileTransferSocket;
    }


    /**
     * Main method of the thread, handles user input, ends when the user quits the application (/quit or /disconnect)
     */
    public void run() {
        try {
            System.out.println("User connection established");
            ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream(), true);

            //Main loop for each thread, waits the user inputs permanently until it's '/disconnect', '/quit', or a ctrl+c
            do {
                //Gets the user input as a List<String> and parses it with CommandHandler()
                List<String> userInputs = (List<String>) input.readObject();
                CommandHandler(userInputs);

            } while (!this.clientCommand.equals("/disconnect") && !this.clientCommand.equals("/quit"));


            ServerConnection.removeUser(this.clientName, this);
            socket.close();
            System.out.println(this.clientName + " disconnected");
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error on user thread or client has 'ctrl+c': " + ex.getMessage());
            //ex.printStackTrace(); //Used for debugging
        }
    }


    public String GetClientName() { return this.clientName; }


    /**
     * Sends a message to the client.
     */
    public void SendMessage(String message) {
        this.writer.println(message);
    }


    /**
     * Sends a file to the client.
     */
    public void SendFile(DatagramPacket file) throws IOException { this.fileSocket.send(file); }


    /**
     * Handles what has been sent by the user
     *
     * @param clientInput User input and their arguments
     * @throws IOException Needed to handle file transfer errors
     */
    private void CommandHandler(List<String> clientInput) throws IOException {
        this.clientCommand = clientInput.get(0);

        //First checks if the user is logged in
        if (!this.connected) {
            ClientLogin(clientInput);
            return;
        }

        Commands clientRequest = CommandParser(clientInput);


        //If tree for edge cases commands that are handled differently
        if (clientRequest != null) {
            this.serverMessage = clientRequest.CommandHandler();

            //If the command has invalid usage or arguments
            if (this.serverMessage.startsWith("Invalid")) {
                this.SendMessage(this.serverMessage);
                return;
            }

            //If the command is related to file transfer, it needs to receive/send the file from the UDP socket, it is handled differently
            if (this.clientCommand.equals("/download")) {
                DatagramPacket fileSend = ((FileTransmission) clientRequest).GetFile();
                this.SendFile(fileSend);
                this.SendMessage(this.serverMessage);
            }
            else if (this.clientCommand.equals("/upload")) {
                DatagramPacket fileReceive = new DatagramPacket(fileBuffer, fileBuffer.length);
                fileSocket.receive(fileReceive);
                ((FileTransmission) clientRequest).StoreFile(fileReceive.getData());

                this.SendMessage(clientRequest.ServerResponse());
                ServerConnection.messageBroadcast(this.serverMessage, clientRequest.GetReceivingParty());
            }
            //Default outcome, simply sends back something to the client and broadcasts a message to corresponding users
            else {
                System.out.println("Command executed?");
                this.SendMessage(clientRequest.ServerResponse());
                ServerConnection.messageBroadcast(this.serverMessage, clientRequest.GetReceivingParty());
            }
        }

        else if (this.clientCommand.equals("/register") || this.clientCommand.equals("/login")) {
            this.SendMessage(Colors.DEFAULT + "\nYou're already logged in, '/disconnect' or '/quit' to quit and log out");
        }

        else if (this.clientCommand.equals("/disconnect") || this.clientCommand.equals("/quit")) {
            this.connected = false;
            this.SendMessage(Colors.WHITE + "\nLogging you out, see you soon !" + Colors.DEFAULT);
        }

        else if (this.clientCommand.equals("/online_users")) {
            this.printUsers();
        }

        //'/help' is a client-side handled command, should return nothing from the server
        else if (this.clientCommand.equals("/help")) {
            return;
        }

        //Else, the command does not exist or hasn't been understood/taken into account
        else {
            this.SendMessage(Colors.DEFAULT + "\nInvalid command: " + this.clientCommand + "\n" +
                        "For 1-to-1 messages: '/private_chat', '/accept', '/decline', '/msg', '/exit_private_chat'\n" +
                        "For group command: '/create_group', '/join_group', '/msg_group', '/exit_group', '/create_secure_group', '/join_secure_group'\n" +
                        "For files command: '/upload', '/list_files', '/download'\n" +
                        "To print online users: '/online_users'\n" +
                        "To print all commands and their description/usage: '/help'\n" +
                        "To quit and log out from the program: '/disconnect', '/quit'");
        }
    }


    /**
     * Calls the abstract class 'Commands' and constructs the corresponding class
     * depending on the user inputs to handle them rightfully
     *
     * @param clientInput User input and their arguments
     * @return the corresponding class depending on the user inputs
     */
    private Commands CommandParser(List<String> clientInput) {
        Commands clientRequest = null;

        switch (this.clientCommand) {
            case "/private_chat", "/accept", "/decline", "/msg", "/exit_private_chat" ->
                    clientRequest = new Chat(clientInput, this.clientName);

            case "/create_group", "/join_group", "/msg_group", "/exit_group", "/create_secure_group", "/join_secure_group" ->
                    clientRequest = new GroupChat(clientInput, this.clientName);

            case "/upload", "/list_files", "/download" ->
                    clientRequest = new FileTransmission(clientInput, this.clientName);
        }
        return clientRequest;
    }


    /**
     * Handles how to respond to the user if he's not logged in
     *
     * @param clientInput User input and their arguments
     */
    private void ClientLogin(List<String> clientInput) {
        if (this.clientCommand.equals("/login") || this.clientCommand.equals("/register")) {
            Commands clientConnection = new UserAuthentication(clientInput);
            this.clientName = clientConnection.getClientName();
            this.serverMessage = clientConnection.CommandHandler();

            if (this.serverMessage.startsWith("Invalid") || this.serverMessage.equals("Authentication failed")) {
                this.SendMessage(this.serverMessage);
            }
            else {
                ServerConnection.addUserName(this.clientName);
                this.connected = true;
                this.SendMessage(this.serverMessage + Colors.BLUE + "\n" +
                        "  ________    _____  .____       _____  ____________________.____________         _____  ___________ _________ ____________________ _______    _____________________________ \n" +
                        " /  _____/   /  _  \\ |    |     /  _  \\ \\_   ___ \\__    ___/|   \\_   ___ \\       /     \\ \\_   _____//   _____//   _____/\\_   _____/ \\      \\  /  _____/\\_   _____/\\______   \\\n" +
                        "/   \\  ___  /  /_\\  \\|    |    /  /_\\  \\/    \\  \\/ |    |   |   /    \\  \\/      /  \\ /  \\ |    __)_ \\_____  \\ \\_____  \\  |    __)_  /   |   \\/   \\  ___ |    __)_  |       _/\n" +
                        "\\    \\_\\  \\/    |    \\    |___/    |    \\     \\____|    |   |   \\     \\____    /    Y    \\|        \\/        \\/        \\ |        \\/    |    \\    \\_\\  \\|        \\ |    |   \\\n" +
                        " \\______  /\\____|__  /_______ \\____|__  /\\______  /|____|   |___|\\______  /____\\____|__  /_______  /_______  /_______  //_______  /\\____|__  /\\______  /_______  / |____|_  /\n" +
                        "        \\/         \\/        \\/       \\/        \\/                      \\/_____/       \\/        \\/        \\/        \\/         \\/         \\/        \\/        \\/         \\/ \n\n" +
                        Colors.WHITE +
                        "For 1-to-1 messages: '/private_chat', '/accept', '/decline', '/msg', '/exit_private_chat'\n" +
                        "For group command: '/create_group', '/join_group', '/msg_group', '/exit_group', '/create_secure_group', '/join_secure_group'\n" +
                        "For files command: '/upload', '/list_files', '/download'\n" +
                        "To print online users: '/online_users'\n" +
                        "To print all commands and their description/usage: '/help'\n" +
                        "To quit and log out from the program: '/disconnect', '/quit'\n" + Colors.DEFAULT);

            }
        }

        else {
            this.SendMessage(Colors.DEFAULT + "\nInvalid command: " + this.clientCommand + "\n" +
                    "Please sign in with '/register' if you're new here or '/login', '/quit' to exit the program");
        }
    }





    /**
     * Sends a list of online users
     */
    private void printUsers() {
        if (ServerConnection.hasUsers()) {
            this.writer.println(Colors.WHITE + "\nConnected users: " + ServerConnection.getUserNames() + Colors.DEFAULT);
        } else {
            this.writer.println(Colors.WHITE + "\nNo other users connected" + Colors.DEFAULT);
        }
    }
}
