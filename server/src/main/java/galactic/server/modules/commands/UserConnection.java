package galactic.server.modules.commands;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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

    public String getUsername() {
        return username;
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
            //method from database to store a new user with his password
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return "You've been registered as '" + this.username + "'.";
    }

    private String Login() {
        try {
            String encryptedPassword = Hashing(), storedPassword = "bruh";
            String salt = ":" + Salting();

            if (!(encryptedPassword + salt).equals((storedPassword + salt))) {
                return "Authentication failed";
            }
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return "Welcome back '" + this.username + "'";
    }
}
