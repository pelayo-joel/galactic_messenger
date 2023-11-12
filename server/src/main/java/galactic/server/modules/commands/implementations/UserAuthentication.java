package galactic.server.modules.commands.implementations;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.miscellaneous.Colors;
import galactic.server.modules.commands.miscellaneous.Encryption;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Read;
import galactic.server.modules.database.crud.Update;


/**
 * Inherits 'Commands', handles user authentication commands and creates a corresponding response from the server
 */
public class UserAuthentication extends Commands implements Encryption {

    private String password;


    /**
     * Gets each string from the list to parse them
     *
     * @param clientInput user Input
     */
    public UserAuthentication(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.client = clientInput.get(1);
        this.password = clientInput.get(2);
    }





    @Override
    public String CommandHandler() {
        if (this.client == null || this.password == null) {
            return "\nInvalid usage: missing username or password or both\n" +
                    "    Usage: <command> <username> <password>";
        }

        switch (this.command) {
            case "/register" -> { return Register(); }
            case "/login" -> { return Login(); }
            default -> { return null; }
        }
    }


    @Override
    public String ServerResponse() {
        return this.selfMessage;
    }


    @Override
    public Set<String> GetReceivingParty() {
        return this.receiver;
    }



    @Override
    public String Hashing() throws InvalidKeySpecException, NoSuchAlgorithmException {
        char[] chars = this.password.toCharArray();
        byte[] salt = new byte[16];

        PBEKeySpec pbfKey = new PBEKeySpec(chars, salt, 1000, 64*8);
        SecretKeyFactory algoEncryption = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = algoEncryption.generateSecret(pbfKey).getEncoded();
        return Encryption.Decrypt(hash);
    }


    @Override
    public String Salting() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[32];
        sr.nextBytes(salt);

        StringBuilder hexaString = new StringBuilder();
        for (byte b : salt) { hexaString.append(String.format("%02X", b)); }
        return hexaString.toString();
    }




    private String Register() {
        try {
            String encryptedPassword = Hashing() + ":" + Salting();

            //Checks if the user already exists or not
            if (!Read.User(this.client, "id").isEmpty()) { return "Invalid username: '" + this.client + "' already exists"; }
            Create.User(this.client, encryptedPassword);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            //e.printStackTrace(); //Used for debugging
        }
        return Colors.GREEN + "\nYou've been registered as '" + this.client + "'." + Colors.DEFAULT;
    }


    /**
     * Compares the hashed password with the stored one and adds a new salt to both,
     * if the login is successful the stored password gets updated with the new salt and logs the user
     *
     * @return a corresponding String that indicates if the login is successful or not
     */
    private String Login() {
        try {
            if (Read.User(this.client, "username").isEmpty()) { return "Invalid username: '" + this.client + "' does not exists"; }

            String encryptedPassword = Hashing(), storedRawPassword = Read.User(this.client, "password");
            String storedPassword = storedRawPassword.substring(0, storedRawPassword.lastIndexOf(":"));
            String newSalt = ":" + Salting();
            System.out.println(encryptedPassword + newSalt);

            if (!(encryptedPassword + newSalt).equals((storedPassword + newSalt))) {
                return "Authentication failed";
            }
            else {
                Update.UserPassword(this.client, storedPassword + newSalt);
            }
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return Colors.GREEN + "\nWelcome back '" + this.client + "'" + Colors.DEFAULT;
    }


    @Override
    protected String InvalidArgumentsErrors() {
        return "";
    }
}
