package message;

public class WrongCrcException extends Exception {
    public WrongCrcException() {
        super("Wrong Crc");
    }
}
