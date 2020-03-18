package vst.jimmy.bll;

/**
 * Created by JImmy on 01/01/2018.
 */

public class Master {
    private static final String IP = "MasterIp";
    private static final String PORT = "MasterPort";
    private static final String NAME = "MasterName";

    private String MasterIp;
    private String MasterName;
    private int MasterPort;

    public String getMasterName() {
        return MasterName;
    }

    public void setMasterName(String masterName) {
        MasterName = masterName;
    }

    public String getMasterIp() {
        return MasterIp;
    }

    public void setMasterIp(String masterIp) {
        MasterIp = masterIp;
    }

    public int getMasterPort() {
        return MasterPort;
    }

    public void setMasterPort(int masterPort) {
        MasterPort = masterPort;
    }


}
