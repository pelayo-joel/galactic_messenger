package galactic.server.modules.commands;

import galactic.server.modules.commands.interfaces.Communication;
import galactic.server.modules.commands.interfaces.Encryption;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

public abstract class Commands implements Communication, Encryption {
    public abstract String CommandHandler();
    public String ServerResponse() { return ""; }
    public Set<String> GetReceivingParty() { return new HashSet<String>(); }
    public String Hashing() throws InvalidKeySpecException, NoSuchAlgorithmException { return ""; }
    public String Salting() throws NoSuchAlgorithmException { return ""; }
    @Override
    public String Decrypt(byte[] array) {
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
