package mobi.ttg.arduinowificontrol;

/**
 * Created by duong on 2/15/2016.
 */
public class DeviceItem {
    private String name, des;
    private boolean on = false;
    private String command;
    private long id;
    public DeviceItem(){

    }
    public DeviceItem(String name, String des, String command){
        this.name = name;
        this.des = des;
        this.command = command;
    }
    public DeviceItem(String name, String des, String command, boolean on){
        this(name,des, command);
        this.on = on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
