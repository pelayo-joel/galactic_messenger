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
import galactic.server.modules.database.crud.Update;


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
        this.group = clientInput.size() == 2 ? clientInput.get(1) : null;
        this.thirdArg = clientInput.size() == 3 ? clientInput.get(2) : null;
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
    public Set<String> GetReceivingParty() {
        return this.receiver;
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

        if (Read.AllGroupNames().contains(this.group)) {
            this.selfMessage = "Group name already taken...";
            return "";
        }
        Create.GroupChat(this.client, this.group, false, null);
        System.out.println("New group named '" + this.group + "' in database");

        this.receiver.addAll(Read.AllUsers());
        this.receiver.remove(this.client);
        this.selfMessage = Colors.WHITE + "\nYou've successfully created the group '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.WHITE + "\nNew group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String NewSecureGroup() {
        try {
            if (this.group == null || this.thirdArg == null) {
                return SECURE_GRP_ARGS_ERROR;
            }

            if (Read.AllGroupNames().contains(this.group)) {
                this.selfMessage = "Group name already taken...";
                return "";
            }
            String encryptedPassword = Hashing() + Salting();
            Create.GroupChat(this.client, this.group, true, encryptedPassword);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }

        this.receiver.addAll(Read.AllUsers());
        this.receiver.remove(this.client);
        this.selfMessage = Colors.WHITE + "\nYou've successfully created the secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.WHITE + "\nNew secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "' created" + Colors.DEFAULT;
    }


    private String JoinGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        if (!Read.AllGroupNames().contains(this.group)) {
            this.selfMessage =  "'" + this.group + "' does not exist...";
            return "";
        }

        if(Read.GroupUsers(this.group).contains(this.client)) {
            this.selfMessage =  "You're already in '" + this.group + "'";
            return "";
        }
        Create.UserInGroup(this.group, this.client);
        this.receiver = Read.GroupUsers(this.group);
        this.receiver.remove(this.client);

        this.selfMessage = Colors.WHITE + "\nYou joined '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined the group" + Colors.DEFAULT;
    }


    private String JoinSecureGroup() {
        try {
            if (this.group == null || this.thirdArg == null) {
                return SECURE_GRP_ARGS_ERROR;
            }

            if (!Read.AllGroupNames().contains(this.group)) {
                this.selfMessage =  "'" + this.group + "' does not exist...";
                return "";
            }
            String encryptedPassword = Hashing(), storedPassword = Read.SecureGroupPassword(this.group);
            String salt = ":" + Salting();

            if (!(encryptedPassword + salt).equals(storedPassword + salt)) {
                this.selfMessage = "Group authentication failed";
                return "";
            }
            Update.GroupPassword(this.group, storedPassword + salt);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }

        if(Read.GroupUsers(this.group).contains(this.client)) {
            this.selfMessage =  "You're already in '" + this.group + "'";
            return "";
        }
        Create.UserInGroup(this.group, this.client);
        this.receiver.addAll(Read.GroupUsers(this.group));

        this.selfMessage = Colors.WHITE + "\nYou joined the secure group '" + Colors.YELLOW + this.group + Colors.WHITE + "'" + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] '" + Colors.BLUE + this.client + Colors.WHITE + "' joined this secure group" + Colors.DEFAULT;
    }


    private String MessageGroup() {
        if (this.group == null || this.thirdArg == null) {
            return "\nInvalid usage: missing group or message or both\n" +
                    "    Usage: <command> <group> <message>";
        }

        if (ArgumentsValidation()) return "";
        this.receiver.addAll(Read.GroupUsers(this.group));
        this.receiver.remove(this.client);

        this.selfMessage = Colors.YELLOW + "\n[" + this.group + "] " + Colors.CYAN_UNDERLINED + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
        return Colors.YELLOW + "\n[" + this.group + "] " + Colors.BLUE + this.client + Colors.WHITE + ": " + this.thirdArg + Colors.DEFAULT;
    }


    private String ExitGroup() {
        if (this.group == null) {
            return GRP_ARGS_ERROR;
        }

        if (ArgumentsValidation()) return "";
        Delete.UserFromGroup(this.group, this.client);
        this.receiver.addAll(Read.GroupUsers(this.group));

        return Colors.YELLOW + "\n[" + this.group + "] " + Colors.WHITE + "Someone left the group" + Colors.DEFAULT;
    }


    private boolean ArgumentsValidation() {
        if (!Read.AllGroupNames().contains(this.group)) {
            this.selfMessage =  "'" + this.group + "' does not exist...";
            return true;
        }

        if(!Read.GroupUsers(this.group).contains(this.client)) {
            this.selfMessage =  "You're not in '" + this.group + "'";
            return true;
        }
        return false;
    }


    @Override
    protected String InvalidArgumentsErrors() {
        return "";
    }
}
