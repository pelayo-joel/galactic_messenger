package galactic.server.modules.commands;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.security.MessageDigest;
import java.util.Set;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Communication;
import galactic.server.modules.commands.interfaces.Encryption;


public class GroupChat implements Command, Communication, Encryption {
    private final String client;
    private String command, group, thirdArg, selfMessage;
    private Set<String> receiver;


    public GroupChat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.group = clientInput.size() < 2 ? null : clientInput.get(1);
        this.thirdArg = clientInput.size() < 3 ? null : clientInput.get(2);
    }




    @Override
    public String CommandHandler() {
        this.receiver = new HashSet<>();
        switch (this.command) {
            case "/create_group" -> { return NewGroup(); }
            case "/create_secure_group" -> { return NewSecureGroup(); }
            case "/join_group" -> { return JoinGroup(); }
            case "/join_secure_group" -> { return JoinSecureGroup(); }
            case "/msg_group" -> { return MessageGroup(); }
            case "/exit_group" -> { return ExitGroup(); }
            default -> { return null; }
        }
    }


    @Override
    public String ServerResponse() { return this.selfMessage; }
    @Override
    public Set<String> GetReceivingParty() { return this.receiver; }


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


    public String getGroup() {
        return group;
    }



    private String NewGroup() {

        return "New group '" + this.group + "' created";
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
        this.selfMessage = "You joined '" + this.group + "'";
        return this.client + " joined '" + this.group + "'";
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
        this.selfMessage = "You joined the secure group '" + this.group + "'";
        return this.client + " joined secure group '" + this.group + "'";
    }

    private  String MessageGroup() {
        this.selfMessage = this.client + ": " + this.thirdArg;
        return this.selfMessage;
    }

    private String ExitGroup() {
        return "";
    }
}
