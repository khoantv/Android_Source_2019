package kmt.tieub.dbAccess;

import java.util.Date;

/**
 * Created by tieub on 04/08/2017.
 */

public class HistoryData
{

    private float HistoryId;
    private Date CurrentTime;
    private int DeviceId;
    private int TimeIrrgation;
    private float ValueHumidity;
    private float ValueTemperature;
    private float WaterLevel;
    private float Power;

    public float getHistoryId() {
        return HistoryId;
    }

    public void setHistoryId(float historyId) {
        HistoryId = historyId;
    }

    public Date getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(Date currentTime) {
        CurrentTime = currentTime;
    }

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public int getTimeIrrgation() {
        return TimeIrrgation;
    }

    public void setTimeIrrgation(int timeIrrgation) {
        TimeIrrgation = timeIrrgation;
    }

    public float getValueHumidity() {
        return ValueHumidity;
    }

    public void setValueHumidity(float valueHumidity) {
        ValueHumidity = valueHumidity;
    }

    public float getValueTemperature() {
        return ValueTemperature;
    }

    public void setValueTemperature(float valueTemperature) {
        ValueTemperature = valueTemperature;
    }

    public float getWaterLevel() {
        return WaterLevel;
    }

    public void setWaterLevel(float waterLevel) {
        WaterLevel = waterLevel;
    }

    public float getPower() {
        return Power;
    }

    public void setPower(float power) {
        Power = power;
    }

}
