package galactic.server.modules.commands.interfaces;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface Encryption {
    public String Hashing() throws InvalidKeySpecException, NoSuchAlgorithmException;
    public String Salting() throws NoSuchAlgorithmException;
    public String Decrypt(byte[] array);
}
