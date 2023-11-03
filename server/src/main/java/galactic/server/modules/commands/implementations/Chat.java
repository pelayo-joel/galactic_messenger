package galactic.server.modules.commands.implementations;


import java.util.HashSet;
import java.util.List;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.miscellaneous.Colors;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Read;


public class Chat extends Commands {

    private final String
            CHAT_RQST_ARGS_ERROR = "\nInvalid usage: missing username\n" +
            "    Usage: <command> <username>",
            CHAT_ARGS_ERROR = "\nInvalid usage: missing username or message or both\n" +
                    "    Usage: <command> <username> <message>";

    private String username, message;



    public Chat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.username = clientInput.get(1).isEmpty() ? null : clientInput.get(1);
        this.message = clientInput.get(2).isEmpty() ? null : clientInput.get(2);
    }





    @Override
    public String CommandHandler() {
        this.receiver = new HashSet<>();
        switch (this.command) {
            case "/private_chat" -> { return ChatRequest(); }
            case "/accept" -> { return Accept(); }
            case "/decline" -> { return Decline(); }
            case "/msg" -> { return Message(); }
            case "/exit_private_chat" -> { return "exit"; }
            default -> { return null; }
        }
    }




    private String ChatRequest() {
        if (this.username == null) {
            return CHAT_RQST_ARGS_ERROR;
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
        if (this.username == null || this.message == null) {
            return CHAT_ARGS_ERROR;
        }

        this.receiver.add(this.username);
        this.selfMessage = Colors.PURPLE + "\n<private> " + Colors.CYAN_UNDERLINED + this.client + Colors.WHITE + ": " + this.message + Colors.DEFAULT;
        return Colors.PURPLE + "\n<private> " + Colors.BLUE + this.client + Colors.WHITE + ": " + this.message + Colors.DEFAULT;
    }
}
