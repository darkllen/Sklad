/*
package message;

import com.github.snksoft.crc.CRC;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Encryption {
    //Key for encryption, for now is constant, symmetric
    public static final String TEMP_KEY = "asdfghjklzxcvbnm";
    */
/**
     *
     * @param key key with which bytes value will be encrypt
     * @param value value, needed to be encrypt
     * @return encrypted bytes of value
     * @throws GeneralSecurityException
     *//*

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
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,
                new IvParameterSpec(new byte[16]));
        //encrypt and return
        return cipher.doFinal(value.getBytes(Charset.forName("UTF-8")));
    }

    private static String decryitString(String key, byte[] encrypted)
            throws GeneralSecurityException {
        //get key bytes
        byte[] raw = key.getBytes(Charset.forName("UTF-8"));
        if (raw.length != 16) {
            throw new IllegalArgumentException("Invalid key size.");
        }
        //create key
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        //create and init cipher in decrypt mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec,
                new IvParameterSpec(new byte[16]));
        byte[] original = cipher.doFinal(encrypted);

        //decrypt and return
        return new String(original, Charset.forName("UTF-8"));
    }

    */
/**
     *
     * @return all message as bytes array
     *//*

    public byte[] encrypt(Message message) throws GeneralSecurityException {

        byte [] messageEncrypted = Encryption.encryptString(Encryption.TEMP_KEY, message.getMessage());
        int wLen = messageEncrypted.length+8;

        byte encrypted[] = new byte[26+wLen];
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
        for (byte b : ByteBuffer.allocate(2).putShort((short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(encrypted, 0, 14))).array()){
            encrypted[i++] = b;
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
            encrypted[i++] = b;
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
        short crcActual = (short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(message, 0, 14));
        short crcNeeded = ByteBuffer.wrap(Arrays.copyOfRange(message, 14, 16)).getShort();
        if (crcActual!=crcNeeded){
            throw new WrongCrcException();
        }

        //get message part
        int code = ByteBuffer.wrap(Arrays.copyOfRange(message, 16, 20)).getInt();
        int userId = ByteBuffer.wrap(Arrays.copyOfRange(message, 20, 24)).getInt();
        byte[] messageEncrypted =  ByteBuffer.wrap(Arrays.copyOfRange(message, 24, 16+wLen)).array();

        //check message crc
        crcActual = (short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOfRange(message, 16, 16+wLen));
        crcNeeded = ByteBuffer.wrap(Arrays.copyOfRange(message, 16+wLen, 16+wLen+2)).getShort();
        if (crcActual!=crcNeeded){
            throw new WrongCrcException();
        }

     return new Message(startMessage,uniqueCode, currMessageNum, code, userId, decryitString(Encryption.TEMP_KEY, messageEncrypted));
    }

}
*/
