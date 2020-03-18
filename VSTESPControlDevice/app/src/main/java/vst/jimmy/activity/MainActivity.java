package vst.jimmy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.List;

import vst.jimmy.bll.Device;
import vst.jimmy.bll.Floor;
import vst.jimmy.db.SharepredPreferencesCls;
import vst.jimmy.socket.ClientSocket;

public class MainActivity extends AppCompatActivity implements ClientSocket.ServerListener{

    //region  Variables
    private static String TAG = "MAIN_ACTIVITY";
    private String floor, ip, port;
    private ClientSocket clientSocket;
    private boolean connected = false;
    private WebView mWebView;
    private SharepredPreferencesCls sharepredPreferencesCls;

    private static final String FLOOR = "floor";
    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "port";

    private static final String PASS_SETUP = "admin";
    private static final String PASS_LOGIN = "admin";

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        mWebView = findViewById(R.id.wbMain);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        ButtonClickJavascriptInterface myJavaScriptInterface = new ButtonClickJavascriptInterface(MainActivity.this);
        mWebView.addJavascriptInterface(myJavaScriptInterface, "Android");
        mWebView.loadUrl("file:///android_asset/www/start.html");

        sharepredPreferencesCls = new SharepredPreferencesCls(MainActivity.this);

        String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
        if (passSetup == null)
            sharepredPreferencesCls.putString(PASS_SETUP, "admin");
    }

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

        //region -- Start Interface --
        @JavascriptInterface
        public void ConnectESP(String _floor, String _ip, String _port)
        {
            //region  Tính sau
            //            String floor =  sharepredPreferencesCls.getString(FLOOR);
//            String saveIp = sharepredPreferencesCls.getString(IP_ADDRESS);
//            int savePort = sharepredPreferencesCls.getInt(PORT);
//
//            if(!connected){
//                clientSocket = new ClientSocket(ip, Integer.parseInt(port));
//                clientSocket.setServerListener(MainActivity.this);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final WebView webView = (WebView) findViewById(R.id.wbMain);
//                        webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECTING...\");");
//                    }
//                });
//                clientSocket.connect();
//            }else{
//                clientSocket.disconnect();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final WebView webView = (WebView) findViewById(R.id.wbMain);
//                        webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECT\");");
//                    }
//                });
//            }
            //endregion

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    webView.loadUrl("file:///android_asset/www/control.html");
                }
            });
        }

        /**
         * Nếu đã lưu địa chỉ ip và port thì hiển thị
         * Đọc thông tin lưu trong Shareprefence, gửi sang webview
         */
        @JavascriptInterface
        public void showMyIp() {
            final String floor = sharepredPreferencesCls.getString(FLOOR);
            final String ip = sharepredPreferencesCls.getString(IP_ADDRESS);
            final int port = sharepredPreferencesCls.getInt(PORT);
            //Toast.makeText(MainActivity.this, "get to show: "+ ip + "; "+ port, Toast.LENGTH_SHORT).show();
            if (ip != null && floor != null && port != -1)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("showIpPort(\"%s\",\"%s\",%d)",floor, ip,port) ;
                        webView.loadUrl("javascript:"+s+";");
                    }
                });
            }
            //view.loadUrl("javascript:document.getElementById('ipaddress').value="+ip+";document.getElementById('port').value="+port+";");
        }
        //endregion

        //region -- device interface --
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
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("showDeviceList(%s)",jsonText) ;
                        webView.loadUrl("javascript:"+s+";");
                    }
                });
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void getDevicesControl() {
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
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("initControl(%s)",jsonText) ;
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
         * @param deviceJson: Đối tượng Device dạng Json
         */
        @JavascriptInterface
        public void createDevice(String deviceJson) {
            try {
                Gson gson = new Gson();
                Device device = gson.fromJson(deviceJson , new TypeToken<Device>(){}.getType());
                boolean result = Device.insert(MainActivity.this, device);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_error) , Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void updateDevice(String oldid, String deviceJson) {
            try {
                Gson gson = new Gson();
                Device device = gson.fromJson(deviceJson , new TypeToken<Device>(){}.getType());
                boolean result = Device.update(MainActivity.this, oldid, device);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteDevice(String id) {
            try {
                boolean result = Device.delete(MainActivity.this, id);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_success), Toast.LENGTH_SHORT).show();
                    getDevicesList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this,  getResources().getString(R.string.device_delete_error) , Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region -- floor interface --
        @JavascriptInterface
        public void getFloorList() {
            try {
                List<Floor> list = Floor.getListFloors(MainActivity.this);
                final String jsonText;
                if (list != null && list.size() > 0)
                {
                    jsonText = new Gson().toJson(list);
                    System.out.println("getFloorsList: "+ jsonText);
                }
                else
                {
                    jsonText = "[]";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("showFloorList(%s)",jsonText) ;
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
         * @param floorJson: Đối tượng Floor dạng Json
         */
        @JavascriptInterface
        public void createFloor(String floorJson) {
            try {
                Gson gson = new Gson();
                Floor floor = gson.fromJson(floorJson , new TypeToken<Floor>(){}.getType());
                boolean result = Floor.insert(MainActivity.this, floor);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_error) , Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void updateFloor(String oldid, String floorJson) {
            try {
                Gson gson = new Gson();
                Floor floor = gson.fromJson(floorJson , new TypeToken<Floor>(){}.getType());
                boolean result = Floor.update(MainActivity.this, oldid, floor);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteFloor(String id) {
            try {
                boolean result = Floor.delete(MainActivity.this, id);
                if (result)
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_delete_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_delete_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this,  getResources().getString(R.string.floor_delete_error) , Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region -- Control --
        @JavascriptInterface
        public void checkPassSetup(String _passSetup) {
            try {
                String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                if (passSetup.equals(_passSetup))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final WebView webView = (WebView) findViewById(R.id.wbMain);
                            webView.loadUrl("file:///android_asset/www/setting.html");
                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.check_passsetup_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {

            }
        }
        //endregion

        //region -- Setting --
        @JavascriptInterface
        public void changePass(int type, String oldPass, String newPass){
            try {
                if (type == 1)  // Change PassLogin
                {
                    String passLogin = sharepredPreferencesCls.getString(PASS_LOGIN);
                    if (!oldPass.equals(passLogin))
                    {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.check_passlogin_error), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sharepredPreferencesCls.putString(PASS_LOGIN, newPass);
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.check_pass_success), Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final WebView webView = (WebView) findViewById(R.id.wbMain);
                                webView.loadUrl("file:///android_asset/www/setting.html");
                            }
                        });
                    }
                }
                else if(type == 2)  // Change PassSetup
                {
                    String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                    if (!oldPass.equals(passSetup))
                    {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.check_passsetup_error), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sharepredPreferencesCls.putString(PASS_SETUP, newPass);
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.check_pass_success), Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final WebView webView = (WebView) findViewById(R.id.wbMain);
                                webView.loadUrl("file:///android_asset/www/setting.html");
                            }
                        });
                    }
                }
            }
            catch (Exception ex)
            {

            }
        }
        //endregion

        //endregion
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
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    webView.loadUrl("javascript:$(\"#btnConnect\").html(\"DISCONNECT\");$(\"#ipaddress\").prop(\"readonly\",true);$(\"#port\").prop(\"readonly\",true);control();");
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    webView.loadUrl("javascript:$(\"#btnConnect\").html(\"CONNECT\");$(\"#ipaddress\").prop(\"readonly\",false);$(\"#port\").prop(\"readonly\",false);");
                }
            });
        }
    }

    @Override
    public void newMessengeFromServer(String messenge) {
        System.out.println("RECEIVER MESSAGE: "+ messenge);

//        if (messenge.contains("{\"deviceId\"") && messenge.contains("\"deviceName\""))
//        {
//            //region -- delete all device --
//            try {
//                boolean result = Device.deleteAll(MainActivity.this);
//                if (result)
//                {
//                    Toast.makeText(MainActivity.this, "Deleted All Devices!", Toast.LENGTH_SHORT).show();
//                    updateDevice(messenge);
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (Exception ex)
//            {
//                Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
//            }
//            //endregion
//        }
//        else
//        {
//
//        }
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
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
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
}

// COnnect thành công mới lưu
// sharepredPreferencesCls.putString(FLOOR, floor);
//sharepredPreferencesCls.putString(IP_ADDRESS, ip);
//sharepredPreferencesCls.putInt(PORT, Integer.parseInt(port));
