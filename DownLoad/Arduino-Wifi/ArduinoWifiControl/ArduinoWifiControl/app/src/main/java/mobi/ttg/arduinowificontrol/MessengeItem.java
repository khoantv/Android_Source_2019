package mobi.ttg.arduinowificontrol;

/**
 * Created by duong on 2/14/2016.
 */
public class MessengeItem {
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;
    private int type;
    private String body;
    public MessengeItem(int type, String body){
        this.type = type;
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public String getBody() {
        return body;
    }
}
