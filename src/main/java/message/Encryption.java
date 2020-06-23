package message;

import com.github.snksoft.crc.CRC;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

public class Encryption {
    //Key for encryption, for now is constant, symmetric
    private String TEMP_KEY;


    private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding


    public Encryption(String TEMP_KEY) {
        this.TEMP_KEY = TEMP_KEY;
    }

    public static String encrypt(String Data, String secret) throws Exception {
        Key key = generateKey(secret);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }
    public static String decrypt(String strToDecrypt, String secret) {
        try {
            Key key = generateKey(secret);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


    private static Key generateKey(String secret) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
        Key key = new SecretKeySpec(decoded, ALGO);
        return key;
    }
    public static String decodeKey(String str) {
        byte[] decoded = Base64.getDecoder().decode(str.getBytes());
        return new String(decoded);
    }
    public static String encodeKey(String str) {
        byte[] encoded = Base64.getEncoder().encode(str.getBytes());
        return new String(encoded);
    }


    public byte[] encrypt(Message message) throws Exception {

        byte [] messageEncrypted = Encryption.encrypt(message.getMessage(), encodeKey(TEMP_KEY)).getBytes();
        int wLen = messageEncrypted.length+8;

        byte encrypted[] = new byte[18+wLen];
        int i = 0;
        //write startMessage symbol
        encrypted[i++] = message.getStartMessage();
        //write unique code of programm
        encrypted[i++] = message.getUniqueCode();

        //write messageNumber
        for (byte b : ByteBuffer.allocate(8).putLong(message.getCurrMessageNum()).array()){
            encrypted[i++] = b;
        }
        //write length of message
        for (byte b : ByteBuffer.allocate(4).putInt(wLen).array()){
            encrypted[i++] = b;
        }
        //write crc of first part
        System.out.println(crc(Arrays.copyOfRange(encrypted, 0, 14)));
        for (byte b : ByteBuffer.allocate(2).putShort((short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(encrypted, 0, 14))).array()){
            encrypted[i++] = 0;
        }
        //write code of message
        for (byte b : ByteBuffer.allocate(4).putInt(message.getCode()).array()){
            encrypted[i++] = b;
        }
        //write user id
        for (byte b : ByteBuffer.allocate(4).putInt(message.getUserId()).array()){
            encrypted[i++] = b;
        }
        //write message
        for (byte b : messageEncrypted){
            encrypted[i++] = b;
        }
        //write message part crc
        for (byte b : ByteBuffer.allocate(2).putShort((short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(encrypted, 16, 16+wLen))).array()){
            encrypted[i++] = 1;
        }
        return  encrypted;
    }


    public Message decript(byte[] message) throws WrongCrcException, WrongStartOfMessage, GeneralSecurityException {
        //get startMessage symbol
        byte startMessage = ByteBuffer.wrap(Arrays.copyOfRange(message, 0, 1)).get();
        //check if this is start of message
        if (startMessage != 0x13) {
            throw new WrongStartOfMessage();
        }

        //get first part of message
        byte uniqueCode = ByteBuffer.wrap(Arrays.copyOfRange(message, 1, 2)).get();
        long currMessageNum = ByteBuffer.wrap(Arrays.copyOfRange(message, 2, 10)).getLong();
        int wLen = ByteBuffer.wrap(Arrays.copyOfRange(message, 10, 14)).getInt();

        //check first part crc
//        short crcActual = (short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(message, 0, 14));
//        short crcNeeded = ByteBuffer.wrap(Arrays.copyOfRange(message, 14, 16)).getShort();
//        if (crcActual!=crcNeeded){
//            throw new WrongCrcException();
//        }

        //get message part
        int code = ByteBuffer.wrap(Arrays.copyOfRange(message, 16, 20)).getInt();
        int userId = ByteBuffer.wrap(Arrays.copyOfRange(message, 20, 24)).getInt();
        byte[] messageEncrypted =  ByteBuffer.wrap(Arrays.copyOfRange(message, 24, 16+wLen)).array();

        //check message crc
//        crcActual = (short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(message, 16, 16+wLen));
//        crcNeeded = ByteBuffer.wrap(Arrays.copyOfRange(message, 16+wLen, 16+wLen+2)).getShort();
//        if (crcActual!=crcNeeded){
//            throw new WrongCrcException();
//        }

     return new Message(startMessage,uniqueCode, currMessageNum, code, userId, decrypt(new String(messageEncrypted), encodeKey(TEMP_KEY)));
    }



    public int crc(byte[] bytes){
        int[] table = {
                0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
                0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
                0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
                0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
                0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
                0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
                0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
                0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
                0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
                0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
                0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
                0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
                0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
                0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
                0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
                0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
                0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
                0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
                0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
                0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
                0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
                0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
                0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
                0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
                0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
                0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
                0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
                0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
                0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
                0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
                0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
                0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
        };

        int crc = 0x0000;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
        }
        System.out.println(crc);
        return crc;
    }

}
