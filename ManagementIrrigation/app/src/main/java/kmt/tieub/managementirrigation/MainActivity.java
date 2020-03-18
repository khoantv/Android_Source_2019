package kmt.tieub.managementirrigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kmt.tieub.dbAccess.GetHistory;
import kmt.tieub.dbAccess.GetDeviceStatus;
import kmt.tieub.dbAccess.HistoryData;
import kmt.tieub.dbAccess.SetDeviceStatus;
import kmt.tieub.dbAccess.SharedPreferencesCls;

public class MainActivity extends AppCompatActivity {


    private Switch tb1, tb2, tb3, tb4;
    private SeekBar seekMoisture;
    private TextView txtValueHumidityOne, txtValueTemperatureOne, txtWaterLevelOne, txtPowerOne,
            txtValueHumidityTwo, txtValueTemperatureTwo, txtWaterLevelTwo, txtPowerTwo,
            txtValueHumidityThree, txtValueTemperatureThree, txtWaterLevelThree, txtPowerThree;

    private final String PHONE_KEY = "PHONE_NUMBER";
    private final String LOGTAG = "MAIN_ACTIVITY";
    private final int PERMISSION_REQUEST_CODE = 10;
    private final int PERMISSION_REQUEST_CODE_RECEIVE = 11;

    private static MainActivity mainActivity;
    private int isCreate = 0;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = MainActivity.this;
        isCreate = 0;
        Init();
        if (!checkPermission())
        {
            requestPermission();
        }
        if (!checkPermissionReceiveSMS())
        {
            requestPermissionReceiveSMS();
        }

        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if (phone == null || phone.equals("")) {
            startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
        }

//        Gson gson = new Gson();
//        String jSon = gson.toJson(ls);
//        System.out.println("Json_SMS: "+ jSon);
        //Http Post Method
        String deviceId = "1";
        String regionId = "1";
        new GetHistory(MainActivity.this).execute("1");
        new GetHistory(MainActivity.this).execute("2");
        new GetHistory(MainActivity.this).execute("3");
        new GetDeviceStatus(MainActivity.this).execute();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new GetDeviceStatus(MainActivity.this).execute();
            }
        }, 1000, 5000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Date d = new Date();
                long hour = d.getHours();//(d.getTime() / 1000 / 60 / 60) % 24;
                long minute = (d.getTime() / 1000 / 60) % 60;
                long second = (d.getTime() / 1000) % 60;
                //System.out.println(hour+ ":" + minute + ":" + second);
                if ((hour == 6 || hour == 12 || hour == 18) && minute == 0 && (second > 0&& second < 5) )
                    sendSMSMessage("GET_DATA_1");
                else if ((hour == 6 || hour == 12 || hour == 18) && minute == 30 && (second > 0&& second < 5) )
                    sendSMSMessage("GET_DATA_2");
                else if ((hour == 7 || hour == 13 || hour == 19) && minute == 0 && (second > 0&& second < 5) )
                    sendSMSMessage("GET_DATA_3");
            }
        }, 1000, 5000);
    }

    //region -- Init Control and Radio Event --
    private void Init() {
        txtValueHumidityOne = (TextView) findViewById(R.id.txtHumidityOne);
        txtValueTemperatureOne = (TextView) findViewById(R.id.txtTemperatureOne);
        txtWaterLevelOne = (TextView) findViewById(R.id.txtLevelOne);
        txtPowerOne = (TextView) findViewById(R.id.txtPowerOne);

        txtValueHumidityTwo = (TextView) findViewById(R.id.txtHumidityTwo);
        txtValueTemperatureTwo = (TextView) findViewById(R.id.txtTemperatureTwo);
        txtWaterLevelTwo = (TextView) findViewById(R.id.txtLevelTwo);
        txtPowerTwo = (TextView) findViewById(R.id.txtPowerTwo);

        txtValueHumidityThree = (TextView) findViewById(R.id.txtHumidityThree);
        txtValueTemperatureThree = (TextView) findViewById(R.id.txtTemperatureThree);
        txtWaterLevelThree = (TextView) findViewById(R.id.txtLevelThree);
        txtPowerThree = (TextView) findViewById(R.id.txtPowerThree);

        tb1 = (Switch) findViewById(R.id.switchTB1);
        tb2 = (Switch) findViewById(R.id.switchTB2);
        tb3 = (Switch) findViewById(R.id.switchTB3);
        //tb4 = (Switch) findViewById(R.id.switchTB4);

        //region -- Radio Event --
        tb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tb1.isChecked())
                {
                    sendSMSMessage("VAN11");
                    Toast.makeText(MainActivity.this, "TB1 ON", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendSMSMessage("VAN10");
                    Toast.makeText(MainActivity.this, "TB1 OFF", Toast.LENGTH_SHORT).show();
                }
                boolean statusTB1 = tb1.isChecked();
                boolean statusTB2 = tb2.isChecked();
                boolean statusTB3 = tb3.isChecked();

                // Xử lý cập nhật trạng thái lên server.
                SetDeviceStatus updateStatus = new SetDeviceStatus(MainActivity.this);
                updateStatus.execute(statusTB1+"", statusTB2+"", statusTB3+"");
            }
        });
        tb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tb2.isChecked())
                {
                    sendSMSMessage("VAN21");
                    Toast.makeText(MainActivity.this, "TB2 ON", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendSMSMessage("VAN20");
                    Toast.makeText(MainActivity.this, "TB2 OFF", Toast.LENGTH_SHORT).show();
                }
                boolean statusTB1 = tb1.isChecked();
                boolean statusTB2 = tb2.isChecked();
                boolean statusTB3 = tb3.isChecked();

                // Xử lý cập nhật trạng thái lên server.
                SetDeviceStatus updateStatus = new SetDeviceStatus(MainActivity.this);
                updateStatus.execute(statusTB1+"", statusTB2+"", statusTB3+"");
            }
        });
        tb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tb3.isChecked())
                {
                    sendSMSMessage("VAN31");
                    Toast.makeText(MainActivity.this, "TB3 ON", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendSMSMessage("VAN30");
                    Toast.makeText(MainActivity.this, "TB3 OFF", Toast.LENGTH_SHORT).show();
                }
                boolean statusTB1 = tb1.isChecked();
                boolean statusTB2 = tb2.isChecked();
                boolean statusTB3 = tb3.isChecked();

                // Xử lý cập nhật trạng thái lên server.
                SetDeviceStatus updateStatus = new SetDeviceStatus(MainActivity.this);
                updateStatus.execute(statusTB1+"", statusTB2+"", statusTB3+"");
            }
        });
        //endregion
    }
    //endregion

    boolean isPumpOn = false;
    //region -- Update Control Value --
    public void setValueChange(HistoryData historyData) {
        int time = historyData.getTimeIrrgation();
        String ValueHumidity = historyData.getValueHumidity()+"";
        String ValueTemperature = historyData.getValueTemperature()+"";
        String WaterLevel = historyData.getWaterLevel()+"";
        String Power = historyData.getPower()+"";

        int deviceId = historyData.getDeviceId();
        if (deviceId == 1)
        {
            txtValueHumidityOne.setText(ValueHumidity);
            if (ValueTemperature.length() > 5)
                ValueTemperature = ValueTemperature.substring(0,5);
            txtValueTemperatureOne.setText(ValueTemperature);
            txtWaterLevelOne.setText(WaterLevel);
            txtPowerOne.setText(Power);
            isPumpOn = tb1.isChecked();
            if (time != 0)
            {
                if (isPumpOn==false) // Nếu bơm đang tắt thì mới bơm
                {
                    sendSMSMessage("VAN11");
                    isPumpOn = true;
                    tb1.setChecked(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendSMSMessage("VAN10"); // Tắt bơm
                            isPumpOn = false;
                            tb1.setChecked(false);
                        }
                    }, time*60*1000);
                    Toast.makeText(MainActivity.this, "Bơm sẽ tắt sau "+ time +" phút.", Toast.LENGTH_SHORT).show();
                    System.out.println("Bơm sẽ tắt sau "+ time +" phút.");
                }
            }
        }
        else if (deviceId == 2)
        {
            txtValueHumidityTwo.setText(ValueHumidity);
            if (ValueTemperature.length() > 5)
                ValueTemperature = ValueTemperature.substring(0,5);
            txtValueTemperatureTwo.setText(ValueTemperature);
            txtWaterLevelTwo.setText(WaterLevel);
            txtPowerTwo.setText(Power);
            isPumpOn = tb2.isChecked();
            if (time != 0)
            {
                if (isPumpOn==false) // Nếu bơm đang tắt thì mới bơm
                {
                    sendSMSMessage("VAN21");
                    isPumpOn = true;
                    tb2.setChecked(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendSMSMessage("VAN20"); // Tắt bơm
                            isPumpOn = false;
                            tb2.setChecked(false);
                        }
                    }, time*60*1000);
                    Toast.makeText(MainActivity.this, "Bơm sẽ tắt sau "+ time +" phút.", Toast.LENGTH_SHORT).show();
                    System.out.println("Bơm sẽ tắt sau "+ time +" phút.");
                }
            }
        } else if (deviceId == 3)
        {
            txtValueHumidityThree.setText(ValueHumidity);
            if (ValueTemperature.length() > 5)
                ValueTemperature = ValueTemperature.substring(0,5);
            txtValueTemperatureThree.setText(ValueTemperature);
            txtWaterLevelThree.setText(WaterLevel);
            txtPowerThree.setText(Power);
            isPumpOn = tb3.isChecked();
            if (time != 0)
            {
                if (isPumpOn==false) // Nếu bơm đang tắt thì mới bơm
                {
                    sendSMSMessage("VAN31");
                    isPumpOn = true;
                    tb3.setChecked(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendSMSMessage("VAN30"); // Tắt bơm
                            isPumpOn = false;
                            tb3.setChecked(false);
                        }
                    }, time*60*1000);
                    Toast.makeText(MainActivity.this, "Bơm sẽ tắt sau "+ time +" phút.", Toast.LENGTH_SHORT).show();
                    System.out.println("Bơm sẽ tắt sau "+ time +" phút.");
                }
            }
        }
    }

    public void setDeviceStatus(boolean isSend, boolean statusTb1, boolean statusTb2, boolean statusTb3)
    {
        if (tb1.isChecked() != statusTb1 )
        {
            tb1.setChecked(statusTb1);
            if (isSend && isCreate > 0)
            {
                if (statusTb1 )
                    sendSMSMessage("VAN11");
                else sendSMSMessage("VAN10");
            }
        }
        if (tb2.isChecked() != statusTb2)
        {
            tb2.setChecked(statusTb2);
            if (isSend && isCreate > 0)
            {
                if (statusTb2)
                    sendSMSMessage("VAN21");
                else sendSMSMessage("VAN20");
            }
        }
        if (tb3.isChecked() != statusTb3)
        {
            tb3.setChecked(statusTb3);
            if (isSend && isCreate > 0)
            {
                if (statusTb3)
                    sendSMSMessage("VAN31");
                else sendSMSMessage("VAN30");
            }
        }
        isCreate++;
    }

    //region -- Update Control Value --
    public void controlPump(final int deviceId, int time) {
        System.out.println("Time: "+ time);
        if (time > 0)
        {
            if (deviceId == 1)
                sendSMSMessage("VAN11");
            if (deviceId == 2)
                sendSMSMessage("VAN21");
            if (deviceId == 3)
                sendSMSMessage("VAN31");
            //tb1.setChecked(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Tắt bơm
                    if (deviceId == 1)
                        sendSMSMessage("VAN10");
                    if (deviceId == 2)
                        sendSMSMessage("VAN20");
                    if (deviceId == 3)
                        sendSMSMessage("VAN30");
//                    SetDeviceStatus updateStatus = new SetDeviceStatus(getApplicationContext());
//                    updateStatus.execute("false", "false", "false");
                }
            }, time*60*1000);
            Toast.makeText(getApplicationContext(), "Bơm sẽ tắt sau "+ time +" phút.", Toast.LENGTH_SHORT).show();
            System.out.println("Bơm sẽ tắt sau "+ time +" phút.");
        }
    }
    //endregion
    //endregion

    //region -- Send SMS --
    protected void sendSMSMessage(String message) {
        try {
            String phoneNo = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
            if (phoneNo == null || phoneNo.equals("")) {
                //startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
                Toast.makeText(MainActivity.this, "Check your PhoneNumber", Toast.LENGTH_SHORT).show();
            } else {
                int PERMISSION_REQUEST_CODE = 1;

                if (android.os.Build.VERSION.SDK_INT >= 23) {

                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_DENIED) {

                        Log.d("permission", "permission denied to SEND_SMS - requesting it");
                        String[] permissions = {Manifest.permission.SEND_SMS};
                        this.message = message;
                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    }
                    else
                    {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, message, null, null);
                        //Toast.makeText(MainActivity.this, "Sent: "+message , Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    //Toast.makeText(MainActivity.this, "Sent: "+message , Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            System.out.println("Cannot send mss: "+ex.toString());
            //Toast.makeText(getApplicationContext(), "Can't send SMS." , Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    String message;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String phoneNo = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, "Bạn không có quyền đọc tin nhắn" , Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Permission Granted, Now you can access SMS.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Permission Denied, You cannot access SMS",Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_CODE_RECEIVE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Permission Granted, Now you can access SMS.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Permission Denied, You cannot receive SMS",Toast.LENGTH_LONG).show();
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setPhone(View v)
    {
        startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
    }

    //region -- Check Permission --
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            System.out.println("READ_SMS PERMISSION_GRANTED");
            return true;
        } else {
            System.out.println("READ_SMS NOT PERMISSION_GRANTED");
            return false;
        }
    }
    private boolean checkPermissionReceiveSMS(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            System.out.println("RECEIVE_SMS PERMISSION_GRANTED");
            return true;
        } else {
            System.out.println("RECEIVE_SMS NOT PERMISSION_GRANTED");
            return false;
        }
    }
    //endregion

    //region --- Request Permission ---
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS)){
            Toast.makeText(MainActivity.this,"Read Sms Allowed.",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,"Read Sms Not Allowed.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS},PERMISSION_REQUEST_CODE);
        }
    }
    private void requestPermissionReceiveSMS(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.RECEIVE_SMS)){
            Toast.makeText(MainActivity.this,"Receive Sms Allowed.",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,"Receive Sms Not Allowed.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},PERMISSION_REQUEST_CODE_RECEIVE);
        }
    }

    //endregion
}

//http://ais.tnut.edu.vn/Android/checkAccount?userName=Admin&passWord=12345