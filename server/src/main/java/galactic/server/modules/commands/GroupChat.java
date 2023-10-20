package galactic.server.modules.commands;


import java.util.List;
import java.security.MessageDigest;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Encryption;


public class GroupChat implements Command, Encryption {
    private String command, group, message;


    public GroupChat(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.group = clientInput.size() < 2 ? null : clientInput.get(1);
        this.message = clientInput.size() < 3 ? null : clientInput.get(2);
    }




    @Override
    public String CommandHandler() {
        switch (this.command) {
            case "/create_group" -> { return NewGroup(); }
            case "/create_secure_group" -> { return NewSecureGroup(); }
            case "/join_group" -> { return JoinGroup(); }
            case "/join_secure_group" -> { return JoinSecureGroup(); }
            case "/msg_group" -> { return MessageGroup(); }
            case "/exit_group" -> { return ExitGroup(); }
            default -> { return "Invalid group chat command"; }
        }
    }

    @Override
    public String Hashing() {
        return "";
    }

    @Override
    public String Salting() {
        return "";
    }



    private String NewGroup() {
        return "";
    }

    private String NewSecureGroup() {
        return "";
    }

    private String JoinGroup() {
        return "";
    }

    private String JoinSecureGroup() {
        return "";
    }

    private  String MessageGroup() {
        return "";
    }

    private String ExitGroup() {
        return "";
    }
}
