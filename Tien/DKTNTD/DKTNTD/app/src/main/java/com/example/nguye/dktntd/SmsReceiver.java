package com.example.nguye.dktntd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {

    //region ---- Variables.
    private Context mContext;
    private Bundle mBundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private final String PHONE_KEY = "PHONE_NUMBER";
    //endregion

    //region ---- onReceiver.
    public void onReceive(Context context, Intent intent) {
        try{
            mContext = context;
            mBundle = intent.getExtras();

            if (mBundle != null){
                //Toast.makeText(context, "You have new Message!", Toast.LENGTH_LONG).show();
                Log.i("SMSReceiver", "You have new Message!" );
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
                    String phoneSent =sms.getDisplayOriginatingAddress().trim();

                    if(phoneSent.contains("+84")) phoneSent = phoneSent.replace("+84", "0");

                    String phone = SharedPreferencesCls.getPrefVal(mContext , PHONE_KEY).trim();

                    if (phone.equals(phoneSent))
                    {
                        String[] a = body.split(";");
                        String deviceId = a[0].trim();
                        String humidity = a[1].trim();
                        String temperature = a[2].trim();
                        String level = a[3].trim();
                        String power = a[4].trim();


                        if (MainActivity.getMainActivity() != null)
                        {
                            MainActivity.getMainActivity().setValueChange(humidity, temperature, level, power);
                        }
                        else
                        {
                            Log.d("SMSReceiver", "MainActivity is null");
                        }
                    }
                }
            }
        }
        catch(Exception sfgh){
            Log.e("ERROR", "Error in getSMSDetails : "+sfgh.toString());
        }
    }//fn getSMSDetails
    //endregion
}
