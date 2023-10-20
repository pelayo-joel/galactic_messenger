package galactic.server.modules.commands;


import java.util.List;

import galactic.server.modules.commands.interfaces.Command;


public class Chat implements Command {
    private String command, username, message;


    public Chat(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.username = clientInput.size() < 2 ? null : clientInput.get(1);
        this.message = clientInput.size() < 3 ? null : clientInput.get(2);
    }




    @Override
    public String CommandHandler() {
        String serverOutput = "";

        switch (this.command) {
            case "/private_chat" -> serverOutput = ChatRequest();
            case "/accept" -> {
                serverOutput = "accepted";
            }
            case "/decline" -> {
                serverOutput = "declined";
            }
            case "/msg" -> serverOutput = Message();
            case "/exit_private_chat" -> {
                serverOutput = "exit";
            }
            default -> { return "Invalid chat command"; }
        }
        return serverOutput;
    }

    private String ChatRequest() {
        return "";
    }

    private String Message() {
        return "";
    }
}
