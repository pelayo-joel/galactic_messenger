package galactic.server.modules.commands;


import java.util.List;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Encryption;


public class UserConnection implements Command, Encryption {
    private String command, username, password;


    public UserConnection(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.username = clientInput.get(1);
        this.password = clientInput.get(2);
    }




    @Override
    public String CommandHandler() {
        switch (this.command) {
            case "/register" -> { return Register(); }
            case "/login" -> { return Login(); }
            default -> { return "Invalid connection command"; }
        }
    }



    @Override
    public void Hashing() {

    }

    @Override
    public void Salting() {

    }

    private String Register() {
        return "";
    }

    private String Login() {
        return "";
    }

    private void ServerPasswordEncryption() {

    }
}
