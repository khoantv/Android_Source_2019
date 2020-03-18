package vst.jimmy.bll;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JImmy on 23/12/2017.
 */

public class CommandCls {

    public static String login(String passlogin)
    {
        return String.format("{LOG=%s,}",passlogin);
    }

    public static String download(String passlogin)
    {
        return String.format("{DOL=%s,}",passlogin);
    }

    public static String controldevice(String passlogin, String address, String cmd, String type)
    {
        return String.format("{CTL=%s,ADD=%s,CMD=%s,TYP=%s}",passlogin,address,cmd, type);
    }

    public static String setup(String passSetup)
    {
        return String.format("{SET=%s,}",passSetup);
    }

    public static String createDevice(String passSetup, String deviceJson)
    {
        Gson gson = new Gson();
        Device device = gson.fromJson(deviceJson, new TypeToken<Device>() {
        }.getType());
        return String.format("{UPI=%s,IDV=%s,NAM=%s,ADD=%s,TYP=%s,}",passSetup,device.getDeviceId(), device.getDeviceName(), device.getAddress(), device.getDeviceType());
    }

    public static String updateDevice(String passSetup, String deviceJson)
    {
        Gson gson = new Gson();
        Device device = gson.fromJson(deviceJson, new TypeToken<Device>() {
        }.getType());
        return String.format("{UPU=%s,IDV=%s,NAM=%s,ADD=%s,TYP=%s}",passSetup,device.getDeviceId(), device.getDeviceName(), device.getAddress(),device.getDeviceType());
    }

    public static String deleteDevice(String passSetup, String id, String type)
    {
        return String.format("{UPD=%s,IDV=%s,NAM=KETTHUC-DANHSACH,ADD=00000000,TYP=%s,}",passSetup, id,type);
    }

    public static String changePassLogin(String passSetup, String pass)
    {
        return String.format("{SPL=%s,PLG=%s,}",passSetup, pass);
    }

    public static String changePassSetup(String passSetup, String pass)
    {
        return String.format("{SPS=%s,PSU=%s,}",passSetup, pass);
    }
}
