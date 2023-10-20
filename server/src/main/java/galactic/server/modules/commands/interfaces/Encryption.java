package galactic.server.modules.commands.interfaces;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public interface Encryption {
    public String Hashing();
    public String Salting() throws NoSuchAlgorithmException;

    public static String Decrypt(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();

        if (paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }
        else {
            return hex;
        }
    }
}
