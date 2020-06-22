import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

public class Main {

    public static void main(String[] args) throws GeneralSecurityException {
        System.out.println(new String(encryptString("asdasdasdasdasda", "asdasd")));
    }



    private static byte[] encryptString(String key, String value)
            throws GeneralSecurityException {
        //get key bytes
        byte[] raw = key.getBytes(Charset.forName("UTF-8"));
        if (raw.length != 16) {
            throw new IllegalArgumentException("Invalid key size.");
        }
        //create key
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //create and init cipher in encrypt mode
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        //encrypt and return
        return cipher.doFinal(value.getBytes(Charset.forName("UTF-8")));
    }
}


