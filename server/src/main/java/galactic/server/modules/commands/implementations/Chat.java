package galactic.server.modules.commands.implementations;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.miscellaneous.Colors;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Delete;
import galactic.server.modules.database.crud.Read;


/**
 * Inherits 'Commands', handles private chat commands and creates a corresponding response from the server
 */
public class Chat extends Commands {

    private final String
            CHAT_RQST_ARGS_ERROR = "\nInvalid usage: missing username\n" +
            "    Usage: <command> <username>",
            CHAT_ARGS_ERROR = "\nInvalid usage: missing username or message or both\n" +
                    "    Usage: <command> <username> <message>";

    private String username, message;



    /**
     * Parse the received List,
     * number of String might change depending on the used command,
     * gets the properties to null if the command needs less arguments
     *
     * @param clientInput the user inputs
     * @param clientName the client username
     */
    public Chat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.username = clientInput.size() >= 2 ? clientInput.get(1) : null;
        this.message = clientInput.size() == 3 ? clientInput.get(2) : null;
    }





    @Override
    public String CommandHandler() {
        this.receiver = new HashSet<>();
        switch (this.command) {
            case "/private_chat" -> { return ChatRequest(); }
            case "/accept" -> { return Accept(); }
            case "/decline" -> { return Decline(); }
            case "/msg" -> { return Message(); }
            case "/exit_private_chat" -> { return ExitChat(); }
            default -> { return null; }
        }
    }


    @Override
    public String ServerResponse() {
        return this.selfMessage;
    }


    @Override
    public Set<String> GetReceivingParty() {
        return this.receiver;
    }



    private String ChatRequest() {
        if (this.username == null) {
            return CHAT_RQST_ARGS_ERROR;
        }

        if (!Read.AllUsers().contains(this.username)) {
            this.selfMessage = "'" + this.username + "' does not exist...";
            return "";
        }

        this.receiver.add(this.username);
        this.selfMessage = "\nChat request sent to: " + this.username + Colors.DEFAULT;
        return Colors.WHITE + "\n/dprivate " + this.client + Colors.DEFAULT;
    }


    private String Accept() {
        if (this.username == null) {
            return CHAT_RQST_ARGS_ERROR;
        }

        Create.PrivateChat(this.client, this.username);

        this.receiver.add(this.username);
        this.selfMessage = "\nYou've accepted to chat with " + this.username + Colors.DEFAULT;
        return Colors.GREEN + "\n" + this.client + " has accepted your request to chat with you" + Colors.DEFAULT;
    }


    private String Decline() {
        if (this.username == null) {
            return CHAT_RQST_ARGS_ERROR;
        }

        this.receiver.add(this.username);
        this.selfMessage = "\nYou've declined a chat with " + this.username + Colors.DEFAULT;
        return Colors.RED + "\n" + this.client + " has declined your request to chat with you" + Colors.DEFAULT;
    }


    private String Message() {
        System.out.println("username: " + this.username + ", message: " + this.message);
        if (this.username == null || this.message == null) {
            this.selfMessage = CHAT_ARGS_ERROR;
            return "";
        }

        //Checks if the client already sent an accepted request to the specified user
        if (Read.ChatId(this.client, this.username) == 0) {
            this.selfMessage = "Send a '/private_chat' and wait for the username to accept before sending a message";
            return "";
        }

        this.receiver.add(this.username);
        this.selfMessage = Colors.PURPLE + "\n<private> " + Colors.CYAN_UNDERLINED + this.client + Colors.WHITE + ": " + this.message + Colors.DEFAULT;
        return Colors.PURPLE + "\n<private> " + Colors.BLUE + this.client + Colors.WHITE + ": " + this.message + Colors.DEFAULT;
    }


    private String ExitChat() {
        if (this.username == null) {
            return CHAT_RQST_ARGS_ERROR;
        }

        if (Read.ChatId(this.client, this.username) == 0) {
            this.selfMessage = "You're not chatting with '" + Colors.BLUE + this.username + Colors.DEFAULT + "'";
            return "";
        }

        Delete.PrivateChat(this.client, this.username);
        this.selfMessage = "\nYou've quitted the chat with " + this.username + Colors.DEFAULT;
        return Colors.RED + "\n" + this.client + " stopped chatting with you" + Colors.DEFAULT;
    }


    @Override
    protected String InvalidArgumentsErrors() {
        return "";
    }
}
