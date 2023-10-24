package galactic.server.modules.commands;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Communication;


public class Chat implements Command, Communication {
    private final String client;
    private String command, username, message, selfMessage;
    private Set<String> receiver;


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


    @Override
    public String ServerResponse() { return this.selfMessage; }
    @Override
    public Set<String> GetReceivingParty() { return this.receiver; }



    private String ChatRequest() {
        this.receiver.add(username);
        this.selfMessage = "Chat request sent to: " + this.username;
        return "/dprivate " + this.client;
    }

    private String Accept() {
        //Needs database method to create new room between both users

        this.receiver.add(username);
        this.selfMessage = "You've accepted to chat with " + username;
        return this.client + " has accepted your request to chat with you";
    }

    private String Decline() {
        this.receiver.add(username);
        this.selfMessage = "You've declined a chat with " + username;
        return this.client + " has declined your request to chat with you";
    }

    private String Message() {
        //Needs database method to store message in message table

        this.receiver.add(username);
        this.selfMessage = this.client + ": " + this.message;
        return this.selfMessage;
    }
}
