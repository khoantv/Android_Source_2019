package kmt.jimmy.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by tieub on 30/05/2017.
 */

public class myBroadcastReceiver extends BroadcastReceiver {

    private final String STATUS = "STATUS";
    private final String TIME_1 = "TIME_1";
    private final String TIME_2 = "TIME_2";
    private final String TIME_3 = "TIME_3";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (networkInfo != null) {
            Log.d("TAG", "Type : " + networkInfo.getType()
                    + "State : " + networkInfo.getState());

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                //get the different network states
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED ) {  // || networkInfo.getState() ==  NetworkInfo.State.CONNECTED

                    String status =  SharedPreferencesCls.getPrefVal(context, STATUS);
                    if(status.equals("1"))
                    {
//                        Intent i = new Intent(context, MainActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(i);
                          String time1 =  SharedPreferencesCls.getPrefVal(context, TIME_1);
                          String time2 =  SharedPreferencesCls.getPrefVal(context, TIME_2);
                          String time3 =  SharedPreferencesCls.getPrefVal(context, TIME_3);

                        Calendar rightNow = Calendar.getInstance();
                        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
                        int minutes = rightNow.get(Calendar.MINUTE);

                        Log.d(TAG, "onReceive: "+ hours+ "-"+minutes);
//                        Toast.makeText(context, "onReceive: "+ hours+ "-"+minutes ,Toast.LENGTH_SHORT).show();

                        if (time1.equals("1") && hours >= 11 && hours <= 12)
                        {
                            WifiStatus(true);
                            return;
                        }
                        if (time2.equals("1") && hours >= 17 && hours <= 20)
                        {
                            WifiStatus(true);
                            return;
                        }
                        if (time3.equals("1") && hours >= 22)
                        {
                            WifiStatus(true);
                            return;
                        }
                        WifiStatus(false);
                        Toast.makeText(context, "Not Allow to connect to Wifi network!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Toast.makeText(context, "Enable!",Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void WifiStatus(boolean isEnable)
    {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(isEnable);

    }
}