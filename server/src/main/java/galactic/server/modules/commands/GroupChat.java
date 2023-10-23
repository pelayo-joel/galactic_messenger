package galactic.server.modules.commands;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.security.MessageDigest;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Encryption;


public class GroupChat implements Command, Encryption {
    private String command, group, thirdArg;


    public GroupChat(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.group = clientInput.size() < 2 ? null : clientInput.get(1);
        this.thirdArg = clientInput.size() < 3 ? null : clientInput.get(2);
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

    public String getGroup() {
        return group;
    }


    @Override
    public String Hashing() throws InvalidKeySpecException, NoSuchAlgorithmException {
        MessageDigest algo = MessageDigest.getInstance("SHA-512");
        byte[] encryptionBytes = algo.digest(this.thirdArg.getBytes());

        StringBuilder passwordGeneration = new StringBuilder();
        for (byte b : encryptionBytes) {
            passwordGeneration.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return passwordGeneration.toString();
    }

    @Override
    public String Salting() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Arrays.toString(salt);
    }



    private String NewGroup() {
        return "";
    }

    private String NewSecureGroup() {
        try {
            String encryptedPassword = Hashing() + Salting();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return "New secure group '" + this.group + "' created";
    }

    private String JoinGroup() {
        return "";
    }

    private String JoinSecureGroup() {
        try {
            String encryptedPassword = Hashing();
            String salt = ":" + Salting();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return "Joined '" + this.group + "'";
    }

    private  String MessageGroup() {
        return "";
    }

    private String ExitGroup() {
        return "";
    }
}
