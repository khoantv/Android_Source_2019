package dbAccess;

/**
 * Created by tieub on 03/08/2017.
 */

public class HistoryData {
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

    private float ValueHumidity;
    private float ValueTemperature;
    private float WaterLevel;
    private float Power;
}
