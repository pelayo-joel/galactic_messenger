package galactic.server.modules.commands.implementations;


import java.util.HashSet;
import java.util.List;

import galactic.server.modules.commands.Commands;


public class Chat extends Commands {
    private String username, message;


    public Chat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.username = clientInput.size() < 2 ? null : clientInput.get(1);
        this.message = clientInput.size() < 3 ? null : clientInput.get(2);
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
            return "Invalid usage: missing username\n" +
                    "Usage: " + this.command + "<username>";
        }

        this.receiver.add(username);
        this.selfMessage = "Chat request sent to: " + this.username;
        return "/dprivate " + this.client;
    }

    private String Accept() {
        if (this.username == null) {
            return "Invalid usage: missing username\n" +
                    "Usage: " + this.command + "<username>";
        }

        //Needs database method to create new room between both users

        this.receiver.add(username);
        this.selfMessage = "You've accepted to chat with " + username;
        return this.client + " has accepted your request to chat with you";
    }

    private String Decline() {
        if (this.username == null) {
            return "Invalid usage: missing username\n" +
                    "Usage: " + this.command + "<username>";
        }

        this.receiver.add(username);
        this.selfMessage = "You've declined a chat with " + username;
        return this.client + " has declined your request to chat with you";
    }

    private String Message() {
        if (this.username == null || this.message == null) {
            return "Invalid usage: missing username or message or both\n" +
                    "Usage: " + this.command + "<username> <message>";
        }
        //Needs database method to store message in message table

        this.receiver.add(username);
        this.selfMessage = this.client + ": " + this.message;
        return this.selfMessage;
    }
}
