package galactic.server.modules.commands.implementations;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.security.MessageDigest;
import java.util.Set;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.miscellaneous.Colors;
import galactic.server.modules.commands.miscellaneous.Encryption;


public class GroupChat extends Commands implements Encryption {

    private final String GRP_ARGS_ERROR = "Invalid usage: missing group\n" +
            "    Usage: <command> <group>",
            SECURE_GRP_ARGS_ERROR = "Invalid usage: missing group or password or both\n" +
            "    Usage: <command> <group> <password>";

    private String group, thirdArg;

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
        return this.group;
    }




    private String NewGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        return Colors.WHITE + "New group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String NewSecureGroup() {
        try {
            if (this.group == null || this.thirdArg == null) {
                return SECURE_GRP_ARGS_ERROR;
            }

            String encryptedPassword = Hashing() + Salting();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }

        return Colors.WHITE + "New secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String JoinGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        this.selfMessage = Colors.WHITE + "You joined '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined the group" + Colors.DEFAULT;
    }


    private String JoinSecureGroup() {
        try {
            if (this.group == null || this.thirdArg == null) {
                return SECURE_GRP_ARGS_ERROR;
            }

            String encryptedPassword = Hashing();
            String salt = ":" + Salting();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }

        this.selfMessage = Colors.WHITE + "You joined the secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined this secure group" + Colors.DEFAULT;
    }


    private String MessageGroup() {
        if (this.group == null || this.thirdArg == null) {
            return "Invalid usage: missing group or message or both\n" +
                    "    Usage: <command> <group> <message>";
        }

        this.selfMessage = Colors.YELLOW + "[" + this.group + "] " + Colors.CYAN_UNDERLINED + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
        return Colors.YELLOW + "[" + this.group + "] " + Colors.BLUE + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
    }


    private String ExitGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        return Colors.YELLOW + "[" + this.group + "] " + Colors.WHITE + "Someone left the group" + Colors.DEFAULT;
    }
}
