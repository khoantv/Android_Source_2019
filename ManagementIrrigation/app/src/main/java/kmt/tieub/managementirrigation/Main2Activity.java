package kmt.tieub.managementirrigation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import kmt.tieub.dbAccess.SetDeviceStatus;
import kmt.tieub.dbAccess.SharedPreferencesCls;

public class Main2Activity extends AppCompatActivity {

    private final String PHONE_KEY = "PHONE_NUMBER";
    private final String LOGTAG = "MAIN_ACTIVITY";
    private final int PERMISSION_REQUEST_CODE = 10;
    private static Main2Activity mainActivity;

    public static Main2Activity getMain2Activity() {
        return mainActivity;
    }
    public WebView mWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mainActivity = Main2Activity.this;

        if (!checkPermission())
        {
            requestPermission();
        }

        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if (phone == null || phone.equals("")) {
            startActivity(new Intent(Main2Activity.this, SetphoneActivity.class));
        }

        mWebView = (WebView) findViewById(R.id.wvPortal);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        ButtonClickJavascriptInterface myJavaScriptInterface = new ButtonClickJavascriptInterface(Main2Activity.this);
        mWebView.addJavascriptInterface(myJavaScriptInterface, "Android");
        mWebView.loadUrl("file:///android_asset/www/main.html");
    }

    //region -- Webview call Java --
    public class ButtonClickJavascriptInterface {
        Context mContext;

        ButtonClickJavascriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void ShowToast(String toast) {
            Toast.makeText(mContext, "Information: " + toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void ControlPump(boolean isOn) {
            System.out.println("isOn: "+ isOn);
            if(isOn==true)
            {
                sendSMSMessage("On");
            }
            else
            {
                sendSMSMessage("Off");
            }
        }

    }
    //endregion

    @Override
    public void onBackPressed() {
        this.finish();
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
    }

    //region Menu to set phone number
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.setPhone:
                startActivity(new Intent(Main2Activity.this, SetphoneActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(Main2Activity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region -- Check Permission --
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            System.out.println("READ_SMS PERMISSION_GRANTED");
            return true;
        } else {
            System.out.println("READ_SMS NOT PERMISSION_GRANTED");
            return false;
        }
    }
    //endregion

    //region --- Request Permission ---
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this,Manifest.permission.READ_SMS)){
            Toast.makeText(Main2Activity.this,"Read Sms Allowed.",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Main2Activity.this,"Read Sms Not Allowed.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(Main2Activity.this,new String[]{Manifest.permission.READ_SMS},PERMISSION_REQUEST_CODE);
        }
    }
    //endregion

    //region -- Send SMS --
    protected void sendSMSMessage(String message) {
        try {
            String phoneNo = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
            if (phoneNo == null || phoneNo.equals("")) {
                //startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
                Toast.makeText(getApplicationContext(), "Check your PhoneNumber", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Sent: "+message , Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "Sent: "+message , Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            System.out.println("Cannot send sms: "+ex.toString());
            //Toast.makeText(getApplicationContext(), "Can't send SMS." , Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region -- onRequestPermissionsResult --
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
                    Toast.makeText(Main2Activity.this, "Bạn không có quyền gửi tin nhắn" , Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main2Activity.this,"Permission Granted, Now you can access SMS.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Main2Activity.this,"Permission Denied, You cannot access SMS",Toast.LENGTH_LONG).show();
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    //endregion

    //region -- Update Control Value --
    public void controlPump(int time) {
        System.out.println("Time: "+ time);
        if (time > 0)
        {
            sendSMSMessage("On");
            //tb1.setChecked(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendSMSMessage("Off"); // Tắt bơm
                    SetDeviceStatus updateStatus = new SetDeviceStatus(getApplicationContext());
                    updateStatus.execute("false", "false", "false");
                }
            }, time*60*1000);
            Toast.makeText(getApplicationContext(), "Bơm sẽ tắt sau "+ time +" phút.", Toast.LENGTH_SHORT).show();
            System.out.println("Bơm sẽ tắt sau "+ time +" phút.");
        }
    }
    //endregion

    @Override
    protected void onRestart() {
        super.onRestart();
        mainActivity = Main2Activity.this;
    }

    public void updateHistory(final String jsonObject)
    {
        System.out.println("updateHistory: "+ jsonObject);
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:updateHistory('" + jsonObject + "')");
            }
        });
    }
}
