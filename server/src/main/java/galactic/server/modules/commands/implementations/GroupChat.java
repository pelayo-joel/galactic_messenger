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
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Delete;
import galactic.server.modules.database.crud.Read;


public class GroupChat extends Commands implements Encryption {

    private final String GRP_ARGS_ERROR = "\nInvalid usage: missing group\n" +
            "    Usage: <command> <group>",
            SECURE_GRP_ARGS_ERROR = "\nInvalid usage: missing group or password or both\n" +
            "    Usage: <command> <group> <password>";

    private String group, thirdArg;

    private Set<String> receiver;



    public GroupChat(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.group = clientInput.get(1).isEmpty() ? null : clientInput.get(1);
        this.thirdArg = clientInput.get(2).isEmpty() ? null : clientInput.get(2);
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

        Create.InsertRoom(this.group, true, false, null);
        Create.InsertUserInRoom(this.client, this.group);

        return Colors.WHITE + "\nNew group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String NewSecureGroup() {
        try {
            if (this.group == null || this.thirdArg == null) {
                return SECURE_GRP_ARGS_ERROR;
            }

            Create.InsertRoom(this.group, true, true, this.thirdArg);
            Create.InsertUserInRoom(this.client, this.group);

            String encryptedPassword = Hashing() + Salting();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }

        return Colors.WHITE + "\nNew secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String JoinGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        Create.InsertUserInRoom(this.client, this.group);

        this.selfMessage = Colors.WHITE + "\nYou joined '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined the group" + Colors.DEFAULT;
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

        Create.InsertUserInRoom(this.client, this.group);

        this.selfMessage = Colors.WHITE + "\nYou joined the secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined this secure group" + Colors.DEFAULT;
    }


    private String MessageGroup() {
        if (this.group == null || this.thirdArg == null) {
            return "\nInvalid usage: missing group or message or both\n" +
                    "    Usage: <command> <group> <message>";
        }

        //Create.InsertMessage(this.thirdArg, this.client, this.group);
        this.receiver = Read.GroupUsers(this.group);

        this.selfMessage = Colors.YELLOW + "\n[" + this.group + "] " + Colors.CYAN_UNDERLINED + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] " + Colors.BLUE + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
    }


    private String ExitGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        Delete.UserFromGroup(this.group, this.client);

        return Colors.YELLOW + "\n[" + this.group + "] " + Colors.WHITE + "Someone left the group" + Colors.DEFAULT;
    }
}
