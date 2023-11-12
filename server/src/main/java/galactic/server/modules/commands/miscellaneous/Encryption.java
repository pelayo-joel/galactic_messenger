package galactic.server.modules.commands.miscellaneous;


import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


/**
 * Encryption methods to implement needed for cryptography, may implement different algorithm
 */
public interface Encryption {

    String Hashing() throws InvalidKeySpecException, NoSuchAlgorithmException;

    String Salting() throws NoSuchAlgorithmException;

    static String Decrypt(byte[] array) {
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
