package galactic.server.modules.commands;


import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;

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
    public String Hashing() {
        String hashedPassword = "";

        try {
            char[] chars = this.password.toCharArray();
            //byte[] salt = Salting().getBytes();
            byte[] salt = new byte[16];

            PBEKeySpec pbfKey = new PBEKeySpec(chars, salt, 1000, 64*8);
            SecretKeyFactory algoEncryption = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = algoEncryption.generateSecret(pbfKey).getEncoded();
            //hashedPassword = Encryption.Decrypt(hash) + Encryption.Decrypt(salt);
            hashedPassword = Encryption.Decrypt(hash);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return hashedPassword;
    }

    @Override
    public String Salting() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Arrays.toString(salt);
    }




    private String Register() {
        String encryptedPassword = "";

        try {
            encryptedPassword = Hashing() + ":" + Encryption.Decrypt(Salting().getBytes());
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Encryption error");
            e.printStackTrace();
        }
        return encryptedPassword;
    }

    private String Login() {
        return "";
    }

    private void ServerPasswordEncryption() {

    }
}
