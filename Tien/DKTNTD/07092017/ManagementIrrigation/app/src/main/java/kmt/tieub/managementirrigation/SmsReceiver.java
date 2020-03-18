package kmt.tieub.managementirrigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Calendar;

import kmt.tieub.dbAccess.HistoryData;
import kmt.tieub.dbAccess.InsertHistoryData;
import kmt.tieub.dbAccess.SetToolStatus;
import kmt.tieub.dbAccess.SharedPreferencesCls;

/**
 * Created by tieub on 04/08/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    //region ---- Variables.
    private Context mContext;
    private Bundle mBundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private final String PHONE_KEY = "PHONE_NUMBER";
    private Main2Activity main2Activity;
    //endregion

    //region ---- onReceiver.
    public void onReceive(Context context, Intent intent) {
        try{
            mContext = context;
            mBundle = intent.getExtras();

            if (mBundle != null){
                //Toast.makeText(context, "You have new Message!", Toast.LENGTH_LONG).show();
                Log.i("SMSReceiver", "You have new Message!" );
                System.out.println("You have new Message!");
                getSMSDetails();
            }
            else
                Log.e("Info","Bundle is Empty!");
        }
        catch(Exception sgh){
            Log.e("ERROR", "Error in Init : "+sgh.toString());
        }
    }//fn onReceive
    //endregion

    //region ---- get SMS detail.
    private void getSMSDetails(){
        SmsMessage[] msgs = null;
        SmsMessage sms;
        SmsMessage sms1 = null;
        try{
            Object[] pdus = (Object[]) mBundle.get("pdus");
            if(pdus != null){
                msgs = new SmsMessage[pdus.length];
                Log.e("Info", "pdus length : " + pdus.length);
                // add SMS to List

                for(int k=0; k < msgs.length; k++) {
                    sms = SmsMessage.createFromPdu((byte[]) pdus[k]);
                    // //...............................
                    String body = sms.getMessageBody().trim();
                    System.out.println("body: "+body);
                    String phoneSent =sms.getDisplayOriginatingAddress().trim();

                    if(phoneSent.contains("+84")) phoneSent = phoneSent.replace("+84", "0");

                    String phone = SharedPreferencesCls.getPrefVal(mContext , PHONE_KEY).trim();

                    if (phone.equals(phoneSent))
                    {
                        if (body.contains("D"))  // Nếu là tin nhắn cập nhật giá trị NĐ, ĐÂ, ...
                        {
                            String[] a = body.split(";");
                            String deviceId = a[1].trim();
                            String humidityStr = a[2].trim();
                            String temperatureStr = a[3].trim();
                            float humidity, temperature;

                            HistoryData history = new HistoryData();
                            history.setDeviceId(Integer.parseInt(deviceId));

                            if (humidityStr.length() > 2)
                            {
                                humidityStr = humidityStr.substring(0,2) ;
                            }
                            humidity = Float.parseFloat(humidityStr);

                            temperatureStr = temperatureStr.replace(",", ".");
                            if (temperatureStr.length() > 2)
                            {
                                temperatureStr = temperatureStr.substring(0,2) ;
                            }
                            temperature = Float.parseFloat(temperatureStr);

                            history.setValueHumidity(humidity);
                            history.setValueTemperature(temperature);
                            int timeIrrgation = getTime(humidity, temperature);
                            history.setTimeIrrgation(timeIrrgation);
                            int P = 750;
                            int C = 5;

                            Calendar calendar = Calendar.getInstance();

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);

                            if (hour == 6 || hour == 12 || hour == 18 || hour == 7)
                            {
                                history.setWaterLevel(P * timeIrrgation * C/1000);
                                history.setPower(P * timeIrrgation/1000);
                            }
                            else
                            {
                                history.setWaterLevel(0);
                                history.setPower(0);
                            }

                            Gson gson = new Gson();
                            String jSon = gson.toJson(history);
                            System.out.println("json Update Device Status: "+ jSon);

                            //region -- Send SMS --
                            //Main2Activity main2Activity = new Main2Activity();
                            Main2Activity.getMain2Activity().controlPump(timeIrrgation);

                            InsertHistoryData insertHistory = new InsertHistoryData(mContext);
                            insertHistory.execute(jSon);
                            //main2Activity.updateHistory(jSon);
                            //endregion
                        }
                        else if (body.contains("T"))   // Nếu là tin nhắn cập nhật trạng thái thiết bị
                        {
                            String[] a = body.split(";");
                            String deviceId = a[1].trim();
                            String tb1 = a[2].trim();
//                            String tb2 = a[3].trim();
//                            String tb3 = a[4].trim();

                            System.out.println("json Update Tool Status: "+ convertToBool(tb1) +";"+  false +";"+  false);
                            // Xử lý cập nhật trạng thái lên server.
                            SetToolStatus updateStatus = new SetToolStatus(mContext);
                            updateStatus.execute(convertToBool(tb1)+"", "false", "false");
                        }
                    }
                }
            }
        }
        catch(Exception sfgh){
            Log.e("ERROR", "Error in getSMSDetails : "+ sfgh.toString());
        }
    }//fn getSMSDetails
    //endregion

    public boolean convertToBool(String value)
    {
        if (value.equals("1")) return true;
        else return false;
    }

    public int getTime(float valueHumidity, float valueTemperature)
    {
        int t = 0;
        if (valueHumidity < 40)
        {
            //region -- TH 1--
            if (valueTemperature < 20)
            {
                t = 30;
            }
            else if (valueTemperature >= 20 && valueTemperature <= 30)
            {
                t = 40;
            }
            else if (valueTemperature > 30 && valueTemperature <= 40)
            {
                t = 50;
            }
            else if (valueTemperature > 40)
            {
                t = 60;
            }
            //endregion
        }
        else if (valueHumidity >= 40 && valueHumidity<=60)
        {

            //region  -- TH 2 --
            if (valueTemperature < 20)
            {
                t = 20;
            }
            else if (valueTemperature >= 20 && valueTemperature <= 30)
            {
                t = 20;
            }
            else if (valueTemperature > 30 && valueTemperature <= 40)
            {
                t = 20;
            }
            else if (valueTemperature > 40)
            {
                t = 40;
            }
            //endregion

        }
        else if (valueHumidity > 60 && valueHumidity <= 80)
        {
            //region -- TH 3 --
            if (valueTemperature < 20)
            {
                t = 10;
            }
            else if (valueTemperature > 20 && valueTemperature < 30)
            {
                t = 10;
            }
            else if (valueTemperature >= 30 && valueTemperature <= 40)
            {
                t = 10;
            }
            else if (valueTemperature > 40)
            {
                t = 20;
            }
            //endregion
        }
        else if (valueHumidity > 80)
        {
            if (valueTemperature > 40)
            {
                t = 10;
            }
            else
            {
                t = 0;
            }
        }
        return t;
    }
}
