package topics;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message (byte[] data){
        this.data = data.clone();//Immutable
        this.asText = new String(data);//text to string
        this.date = new Date();// current date
        double tempDouble;
        try {
            tempDouble = Double.parseDouble(asText);//string to number
        }catch (NumberFormatException e){
            tempDouble=Double.NaN;// fail= default
        }
        this.asDouble = tempDouble;
    }

    public Message(String text) {
        this(text.getBytes()); //
    }


    public Message(double value) {
        this(String.valueOf(value));
    }

}