package message;

import java.util.Random;


public class MessageGenerator {

    /**
     *
     * @return Random generated Message
     */
    public static Message generateMessage(){
        int code = new Random().nextInt(Commands.values().length)+1;
       return new Message(code,1,Commands.values()[code-1].name());
    }
}
