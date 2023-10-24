package galactic.server.modules.commands;


import java.util.List;

import galactic.server.modules.commands.interfaces.Command;


public class Chat implements Command {
    private final String client;
    private String command, username, message;


    public Chat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.username = clientInput.size() < 2 ? null : clientInput.get(1);
        this.message = clientInput.size() < 3 ? null : clientInput.get(2);
    }



    @Override
    public String CommandHandler() {

        switch (this.command) {
            case "/private_chat" -> { return ChatRequest(); }
            case "/accept" -> { return "accepted"; }
            case "/decline" -> { return "declined"; }
            case "/msg" -> { return Message(); }
            case "/exit_private_chat" -> { return "exit"; }
            default -> { return "Invalid chat command"; }
        }
    }



    private String ChatRequest() {
        return "";
    }

    private String Message() {
        return this.client + ": " + this.message;
    }
}
