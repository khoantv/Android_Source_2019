package kmt.jimmy.wifimanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Intent.ACTION_TIME_CHANGED;

public class MainActivity extends AppCompatActivity {

    private final String STATUS = "STATUS";
    private final String TIME_1 = "TIME_1";
    private final String TIME_2 = "TIME_2";
    private final String TIME_3 = "TIME_3";
    private final String TAG = "TAG";
    private CheckBox chkStatus, chkTime1, chkTime2, chkTime3;

    public static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(ACTION_TIME_CHANGED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(m_timeChangedReceiver, s_intentFilter);

        //region -- Init control --
        chkStatus = (CheckBox) findViewById(R.id.chkStatus);
        chkTime1 = (CheckBox) findViewById(R.id.chkTime1);
        chkTime2 = (CheckBox) findViewById(R.id.chkTime2);
        chkTime3 = (CheckBox) findViewById(R.id.chkTime3);
        //endregion

        try
        {
            //region ---- Init SharedPreferencesCls ----
            String status = SharedPreferencesCls.getPrefVal(getApplicationContext(), STATUS);
            if (status == null || status.equals("")) {
                SharedPreferencesCls.setPrefVal(getApplicationContext(), STATUS, "0");
                SharedPreferencesCls.setPrefVal(getApplicationContext(), TIME_1, "0");
                SharedPreferencesCls.setPrefVal(getApplicationContext(), TIME_2, "0");
                SharedPreferencesCls.setPrefVal(getApplicationContext(), TIME_3, "0");
            }
            else if(status.equals("1"))
            {
                chkStatus.setChecked(true);
                chkStatus.setText(getString(R.string.status_enable));
            }
            else
            {
                chkStatus.setChecked(false);
                chkStatus.setText(getString(R.string.status_disable));
            }
            //endregion
        }
        catch (Exception ex)
        {
            Log.e("TAG", ex.toString());
            Toast.makeText(MainActivity.this, "Not Save!",Toast.LENGTH_SHORT).show();
        }

        //region -- show statuc check --
        String time1 =  SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_1);
        String time2 =  SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_2);
        String time3 =  SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_3);
        if (time1.equals("1"))
        {
            chkTime1.setChecked(true);
        }
        else chkTime1.setChecked(false);

        if (time2.equals("1"))
        {
            chkTime2.setChecked(true);
        }
        else chkTime2.setChecked(false);

        if (time3.equals("1"))
        {
            chkTime3.setChecked(true);
        }
        else chkTime3.setChecked(false);
        //endregion

        //region -- set check change --
        chkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck)
                    chkStatus.setText(getString(R.string.status_enable));
                else
                    chkStatus.setText(getString(R.string.status_disable));
            }
        });
        //endregion
    }

    //region -- Save status --
    public void btnSave(View v)
    {
        try
        {
            if (chkStatus.isChecked())
            {
                SharedPreferencesCls.setPrefVal(MainActivity.this, STATUS, "1");
            }
            else
            {
                SharedPreferencesCls.setPrefVal(MainActivity.this, STATUS, "0");
            }

            if (chkTime1.isChecked())
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_1, "1");
            else
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_1, "0");

            if (chkTime2.isChecked())
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_2, "1");
            else
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_2, "0");

            if (chkTime3.isChecked())
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_3, "1");
            else
                SharedPreferencesCls.setPrefVal(MainActivity.this, TIME_3, "0");

            Log.d(TAG, "isEnable: "+ SharedPreferencesCls.getPrefVal(MainActivity.this, STATUS));
            Log.d(TAG, "chkTime1: "+ SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_1));
            Log.d(TAG, "chkTime2: "+ SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_2));
            Log.d(TAG, "chkTime3: "+ SharedPreferencesCls.getPrefVal(MainActivity.this, TIME_3));

            Toast.makeText(MainActivity.this, "Saved!",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("TAG", ex.toString());
            Toast.makeText(MainActivity.this, "Not Save!",Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_TICK))
            {
                Log.d("TimeChange", "Time Changed");
                @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()){
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
            }
            else
            {
                Log.d("TimeChange", "Not run");
            }
        }
    };

    //region -- Change Wifi State --
    private void WifiStatus(boolean isEnable)
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(isEnable);
    }
    //endregion

    //region ---- Back => Exit.
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Cảnh Báo!");
        alert.setIcon(R.drawable.warning);
        alert.setMessage("Bạn có muốn đăng xuất không?");
        alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }
    //endregion
}
