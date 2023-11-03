package galactic.server.modules.commands.implementations;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.commands.miscellaneous.Colors;
import galactic.server.modules.commands.miscellaneous.Encryption;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Read;
import galactic.server.modules.database.crud.Update;


public class UserAuthentication extends Commands implements Encryption {

    private String password;



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
        return Arrays.toString(salt);
    }




    private String Register() {
        try {
            String encryptedPassword = Hashing() + ":" + Salting();
            System.out.println(encryptedPassword.length());

            if (!Read.User(this.client, "id").isEmpty()) {
                return "Invalid username: '" + this.client + "' already exists";
            }
            Create.User(this.client, encryptedPassword);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return Colors.GREEN + "\nYou've been registered as '" + this.client + "'." + Colors.DEFAULT;
    }


    private String Login() {
        try {
            String encryptedPassword = Hashing(), storedRawPassword = Read.User(this.client, "password");
            String storedPassword = storedRawPassword.substring(0, storedRawPassword.lastIndexOf(":"));
            String newSalt = ":" + Salting();

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
}
