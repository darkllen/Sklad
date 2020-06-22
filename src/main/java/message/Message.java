package message;

import java.security.GeneralSecurityException;
import java.util.Objects;

public class Message {
    private static long messageNum = -1;

    private byte startMessage = 0x13;
    private byte uniqueCode = 0x1;
    private long currMessageNum;



    private int code;
    private int userId;
    private String message;

    /**
     *
     * @param code message code
     * @param userId user id
     * @param message message
     * @throws GeneralSecurityException
     */
    public Message(int code, int userId, String message) {
        this.code = code;
        this.userId = userId;
        this.message = message;

        messageNum++;
        currMessageNum = messageNum;
    }




    /**constructor for server response
     *
     * @param code message code
     * @param userId user id
     * @param message message
     * @param startMessage symbol of start message
     * @param uniqueCode unique code of application
     * @param currMessageNum number of this message
     */
    public Message(byte startMessage, byte uniqueCode, long currMessageNum, int code, int userId, String message) {
        this.startMessage = startMessage;
        this.uniqueCode = uniqueCode;
        this.currMessageNum = currMessageNum;
        this.code = code;
        this.userId = userId;
        this.message = message;
    }

    public static long getMessageNum() {
        return messageNum;
    }

    public byte getStartMessage() {
        return startMessage;
    }

    public byte getUniqueCode() {
        return uniqueCode;
    }

    public long getCurrMessageNum() {
        return currMessageNum;
    }

    public int getCode() {
        return code;
    }

    public int getUserId() {
        return userId;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return startMessage == message1.startMessage &&
                uniqueCode == message1.uniqueCode &&
                currMessageNum == message1.currMessageNum &&
                code == message1.code &&
                userId == message1.userId &&
                message.equals(message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startMessage, uniqueCode, currMessageNum, code, userId, message);
    }

    @Override
    public String toString() {
        return "unique code: " + uniqueCode + "\n" +
                "user id: " + userId + "\n" +
                "message num: " + currMessageNum +"\n" +
                "message code: " +code + "\n" +
                "message: " + message;
    }
}
