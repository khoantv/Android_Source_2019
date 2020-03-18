package kmt.jimmy.espcontrol;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kmt.jimmy.bll.Device;
import kmt.jimmy.db.SharepredPreferencesCls;
import kmt.jimmy.socket.ClientSocket;

public class MainActivity extends AppCompatActivity implements ClientSocket.ServerListener{

    private static Date date;

    //region -- Define variables --
    private static String TAG = "MAIN_ACTIVITY";
    private WebView mWebView;
    private String ip, port;
    private ClientSocket clientSocket;
    private boolean connected = false;

    public static final String IP_ADDRESS = "ip_address";
    public static final String PORT = "port";

    private SharepredPreferencesCls sharepredPreferencesCls;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkKey())
        {
            Toast.makeText(MainActivity.this, "Time use out of date!", Toast.LENGTH_LONG).show();
            finish();
        }

        sharepredPreferencesCls = new SharepredPreferencesCls(MainActivity.this);

        InitView();

        if(connected){
            connectStatusChange(true);
        }

        //region  -- Webview Setting --
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        ButtonClickJavascriptInterface myJavaScriptInterface = new ButtonClickJavascriptInterface(MainActivity.this);
        mWebView.addJavascriptInterface(myJavaScriptInterface, "Android");
        mWebView.loadUrl("file:///android_asset/www/home.html");

        //region -- Not use --
        mWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){

            }
        });
        //endregion
        //endregion
    }

    //region -- Check Date Expired ---
    private boolean checkKey(){
        boolean isExpired = true;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //For declaring values in new date objects. use same date format when creating dates

            Date dateExpired = sdf.parse("2017-12-29"); //date is February 23, 1995
            long timeExpired = dateExpired.getTime();

            long currentMillis = Calendar.getInstance().getTimeInMillis();  // long currentMillis = System.currentTimeMillis();
            System.out.println("timeExpired: "+ timeExpired);
            System.out.println("now: "+ currentMillis);
            if(timeExpired < currentMillis)
            {
                System.out.println(" timeExpired < now");
                isExpired = true;
            }
            else
            {
                System.out.println("timeExpired > now");
                isExpired = false;
            }
        }
        catch (Exception ex)
        {

        }
        return isExpired;
    }
    //endregion

    //region -- Webview call --
    public class ButtonClickJavascriptInterface {
        Context mContext;

        ButtonClickJavascriptInterface(Context c) {
            mContext = c;
        }

        /**
         * Hiển thị một thông báo, được gửi từ WebView
         * @param toast
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, "Message: " + toast, Toast.LENGTH_SHORT).show();
        }

        /**
         * Hàm kết nối với ESP
         * @param ip: Địa chỉ Ip của ESP
         * @param port: Port của ESP
         */
        @JavascriptInterface
        public void connect(String ip, String port)
        {
            String saveIp = sharepredPreferencesCls.getString(IP_ADDRESS);
            int savePort = sharepredPreferencesCls.getInt(PORT);
            if (saveIp == null || savePort == -1 || (!saveIp.equals(ip) &&  savePort!= Integer.parseInt(port)))
            {
                sharepredPreferencesCls.putString(IP_ADDRESS, ip);
                sharepredPreferencesCls.putInt(PORT, Integer.parseInt(port));
                //Toast.makeText(MainActivity.this, "save "+ ip + "; "+ port, Toast.LENGTH_SHORT).show();
            }

            if(!connected){
                clientSocket = new ClientSocket(ip, Integer.parseInt(port));
                clientSocket.setServerListener(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbESP);
                        webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECTING...\");");
                    }
                });
                clientSocket.connect();
            }else{
                clientSocket.disconnect();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbESP);
                        webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECT\");");
                    }
                });
            }
        }

        /**
         * Nếu đã lưu địa chỉ ip và port thì hiển thị
         * Đọc thông tin lưu trong Shareprefence, gửi sang webview
         */
        @JavascriptInterface
        public void showMyIp() {
            final String ip = sharepredPreferencesCls.getString(IP_ADDRESS);
            final int port = sharepredPreferencesCls.getInt(PORT);
            //Toast.makeText(MainActivity.this, "get to show: "+ ip + "; "+ port, Toast.LENGTH_SHORT).show();
            if (ip != null && port != -1)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbESP);
                        String s = String.format("showIpPort(\"%s\",%d)",ip, port) ;
                        webView.loadUrl("javascript:"+s+";");
                    }
                });
            }
            //view.loadUrl("javascript:document.getElementById('ipaddress').value="+ip+";document.getElementById('port').value="+port+";");
        }

        /**
         * Gửi lệnh điều khiển xuống ESP
         * @param message: Lệnh điều khiển
         */
        @JavascriptInterface
        public void sendData(String message) {
            try {
                if(connected)
                {
                    clientSocket.connect();
                    clientSocket.sendMessenge(message);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "You are not connect!", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void getDevicesList() {
            try {
                List<Device> list = Device.getListDevices(MainActivity.this);
                final String jsonText;
                if (list != null && list.size() > 0)
                {
                    jsonText = new Gson().toJson(list);
                    System.out.println("getDevicesList: "+ jsonText);
                }
                else
                {
                    jsonText = "[]";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbESP);
                        String s = String.format("getDeviceList(%s)",jsonText) ;
                        webView.loadUrl("javascript:"+s+";");
                    }
                });
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Tạo mới thiết bị lưu vào database.
         * @param deviceName: Tên thiết bị
         */
        @JavascriptInterface
        public void createDevice(String deviceJson) {
            try {
                Gson gson = new Gson();
                Device device = gson.fromJson(deviceJson, new TypeToken<Device>() {
                }.getType());
                boolean result = Device.insert(MainActivity.this, device);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void updateDevice(String oldid, String deviceJson) {
            try {
                Gson gson = new Gson();
                Device device = gson.fromJson(deviceJson, new TypeToken<Device>() {
                }.getType());
                boolean result = Device.update(MainActivity.this, oldid, device);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteteDevice(String id) {
            try {
                boolean result = Device.delete(MainActivity.this, id);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteteAllDevice() {
            try {
                boolean result = Device.deleteAll(MainActivity.this);
                if (result)
                {
                    Toast.makeText(MainActivity.this, "Deleted All Devices!", Toast.LENGTH_SHORT).show();
                    //getDevicesList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    //endregion

    //region -- Can go Back --
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    //endregion

    //region -- Init View --
    private void InitView() {
        mWebView = (WebView) findViewById(R.id.wbESP);
    }
    //endregion

    //region -- Socket IO --
    @Override
    public void connectStatusChange(boolean status) {
        this.connected = status;
        if(connected)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbESP);
                    webView.loadUrl("javascript:$(\"#btnConnect\").html(\"DISCONNECT\");$(\"#ipaddress\").prop(\"readonly\",true);$(\"#port\").prop(\"readonly\",true);control();");
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbESP);
                    webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECT\");$(\"#ipaddress\").prop(\"readonly\",false);$(\"#port\").prop(\"readonly\",false);");
                }
            });
        }
    }

    @Override
    public void newMessengeFromServer(String messenge) {
        Log.d("RECEIVER MESSAGE", messenge);
    }

    public void updateDevice(String jsonInString)
    {
        //region  -- Insert devices using json data received --
        try
        {
            // 2. JSON to Java object, read it from a Json String.
            Gson gson = new Gson();
            List<Device> lsDevice = gson.fromJson(jsonInString, new TypeToken<List<Device>>(){}.getType());
            if (lsDevice!=null&&lsDevice.size()>0)
            {
                for (int i = 0; i< lsDevice.size(); i++)
                {
                    Device.insert(MainActivity.this, lsDevice.get(i));
                }
                Toast.makeText(MainActivity.this, "Updated List Devices!", Toast.LENGTH_SHORT).show();
                getDevicesList();
            }

        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.toString());
        }
        //endregion
    }

    public void getDevicesList(){
        try {
            List<Device> list = Device.getListDevices(MainActivity.this);
            final String jsonText;
            if (list != null && list.size() > 0)
            {
                jsonText = new Gson().toJson(list);
                System.out.println("getDevicesList: "+ jsonText);
            }
            else
            {
                jsonText = "[]";
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbESP);
                    String s = String.format("getDeviceList(%s)",jsonText) ;
                    webView.loadUrl("javascript:"+s+";");
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.connected){
            clientSocket.disconnect();
            Log.d(TAG, "Socket Disconnect");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!connected){
            if (clientSocket!= null)
            {
                clientSocket.connect();
                Log.d(TAG, "Socket Disconnect");
            }
        }
    }
    //endregion
}
