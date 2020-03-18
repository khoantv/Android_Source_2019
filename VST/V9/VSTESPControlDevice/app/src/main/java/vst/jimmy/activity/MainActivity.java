package vst.jimmy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vst.jimmy.bll.CommandCls;
import vst.jimmy.bll.Device;
import vst.jimmy.bll.Floor;
import vst.jimmy.bll.Master;
import vst.jimmy.broadcast.NetworkChangeReceiver;
import vst.jimmy.broadcast.NetworkChangeResult;
import vst.jimmy.db.NetworkUtil;
import vst.jimmy.db.SharepredPreferencesCls;
import vst.jimmy.socket.ClientSocket;
import vst.jimmy.socket.CurrentSession;

public class MainActivity extends AppCompatActivity implements ClientSocket.ServerListener, NetworkChangeResult, NavigationView.OnNavigationItemSelectedListener {

    //region  Variables
    private static String TAG = "MAIN_ACTIVITY";
    private String floor, ip, port;
    private ClientSocket clientSocket;
    private boolean connected = false;
    private WebView mWebView;
    private NavigationView navigationView;
    private SharepredPreferencesCls sharepredPreferencesCls;

    private static final String LIST_MASTER = "list_master";
    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "port";
    private static final String NAME = "name";

    private static final String PASS_SETUP = "pass_setup";
    private static final String PASS_LOGIN = "pass_login";
    private static final String IS_SAVE_PASS_SETUP = "YES";

    private static final String IS_REMEMBER = "YES";

    private static boolean IS_ENABLE_RECONNECT = true;

    private NetworkChangeReceiver mReceiver;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharepredPreferencesCls = new SharepredPreferencesCls(MainActivity.this);

        mReceiver = new NetworkChangeReceiver(MainActivity.this);

        //region -- Fix lỗi:
        /*
           android.os.FileUriExposedException: file:///storage/emulated/0/test.txt exposed beyond app through Intent.getData()
         */
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //endregion

        mWebView = findViewById(R.id.wbMain);

        //region -- Web setting --
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        ButtonClickJavascriptInterface myJavaScriptInterface = new ButtonClickJavascriptInterface(MainActivity.this);
        mWebView.addJavascriptInterface(myJavaScriptInterface, "Android");
        mWebView.loadUrl("file:///android_asset/www/control.html");
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });
        //endregion



        //region --- Kiểm tra kết nối với ESP
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (!CurrentSession.isConnected) {
//                    CurrentSession.reConnect(MainActivity.this);
//                }
//            }
//        }, 1000, 20000);
        //endregion

        //region -- Kiểm tra kết nối mạng
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                boolean isConnected = NetworkUtil.getConnectivityStatus(MainActivity.this);
//                if (isConnected == false)
//                {
//                    CurrentSession.getStatusConnection() = false;
//                    if (CurrentSession.clientSocket != null)
//                    {
//                        CurrentSession.clientSocket.disconnect();
//                        CurrentSession.clientSocket = null;
//                    }
//                    MainActivity.this.finish();
//                }
//                else
//                {
//                    CurrentSession.reConnect(MainActivity.this);
//                }
//            }
//        }, 1000, 10000);
        //endregion

        //region -- Navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion

        showMenuItem();
    }

    private void showMenuItem() {
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_option).setVisible(true);
    }

    //region Internet connection change
    /**
     * Nếu có sự thay đổi về mạng, mất kết nối mạng thì thoát ngay ra màn hình đăng nhập
     * @param isConnected
     */
    @Override
    public void checkInternetConnection(boolean isConnected) {
        Log.d(TAG, "isConnected: "+ isConnected);
        if (isConnected == false)
        {
            CurrentSession.closeConnection();
            MainActivity.this.finish();
        }
        else
        {
            CurrentSession.reConnect(MainActivity.this);
        }
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
         *
         * @param toast
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, "Message: " + toast, Toast.LENGTH_SHORT).show();
        }

        //region -- device interface --
        @JavascriptInterface
        public void getDevicesList() {
            try {
                List<Device> list = Device.getListDevices(MainActivity.this);
                final String jsonText;
                if (list != null && list.size() > 0) {
                    jsonText = new Gson().toJson(list);
                    System.out.println("getDevicesList: " + jsonText);
                } else {
                    jsonText = "[]";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("showDeviceList(%s)", jsonText);
                        webView.loadUrl("javascript:" + s + ";");
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void getDevicesControl() {
            try {
                List<Device> list = Device.getListDevices(MainActivity.this);
                final String jsonText;
                if (list != null && list.size() > 0) {
                    jsonText = new Gson().toJson(list);
                    System.out.println("getDevicesList: " + jsonText);
                } else {
                    jsonText = "[]";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("initControl(%s)", jsonText);
                        webView.loadUrl("javascript:" + s + ";");
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "getDevicesControl Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Tạo mới thiết bị lưu vào database.
         *
         * @param deviceJson: Đối tượng Device dạng Json
         */
        @JavascriptInterface
        public void createDevice(String deviceJson) {
            try {
                String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                String query = CommandCls.createDevice(passSetup,deviceJson);
                sendData(query);
            }
            catch (Exception x)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_insert_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void updateDevice(String oldid, String deviceJson) {
            try {
                String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                String query = CommandCls.updateDevice(passSetup,deviceJson);
                sendData(query);
            }
            catch (Exception x)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteDevice(String id, String type) {
            try {
                String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                String query = CommandCls.deleteDevice(passSetup,id, type);
                sendData(query);
            }
            catch (Exception x)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_delete_error), Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region -- floor interface --
        @JavascriptInterface
        public void getFloorList() {
            try {
                List<Floor> list = Floor.getListFloors(MainActivity.this);
                final String jsonText;
                if (list != null && list.size() > 0) {
                    jsonText = new Gson().toJson(list);
                    System.out.println("getFloorsList: " + jsonText);
                } else {
                    jsonText = "[]";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        String s = String.format("showFloorList(%s)", jsonText);
                        webView.loadUrl("javascript:" + s + ";");
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, " getFloorList Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Tạo mới thiết bị lưu vào database.
         *
         * @param floorJson: Đối tượng Floor dạng Json
         */
        @JavascriptInterface
        public void createFloor(String floorJson) {
            try {
                Gson gson = new Gson();
                Floor floor = gson.fromJson(floorJson, new TypeToken<Floor>() {
                }.getType());
                boolean result = Floor.insert(MainActivity.this, floor);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_insert_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void updateFloor(String oldid, String floorJson) {
            try {
                Gson gson = new Gson();
                Floor floor = gson.fromJson(floorJson, new TypeToken<Floor>() {
                }.getType());
                boolean result = Floor.update(MainActivity.this, oldid, floor);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_update_error), Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void deleteFloor(String id) {
            try {
                boolean result = Floor.delete(MainActivity.this, id);
                if (result) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_delete_success), Toast.LENGTH_SHORT).show();
                    getFloorList();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_delete_error), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.floor_delete_error), Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region -- Control --
        @JavascriptInterface
        public void downloadDevice() {
            String passLogin = sharepredPreferencesCls.getString(PASS_LOGIN);
            String query = CommandCls.download(passLogin);
            sendData(query);
        }

        @JavascriptInterface
        public void getPassSetup() {
            String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
            final String is_save_pass = sharepredPreferencesCls.getString(IS_SAVE_PASS_SETUP);

            if (passSetup == null || is_save_pass ==null || is_save_pass.equals("NO"))
            {
                passSetup = "";
            }

            final String finalPassSetup = passSetup;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    String s = String.format("showPassSetup(\"%s\",\"%s\")", finalPassSetup,is_save_pass);
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        }

        @JavascriptInterface
        public void save_unsave(String cmd) {
            try {
                System.out.println("save_unsave: "+cmd);
                sharepredPreferencesCls.putString(IS_SAVE_PASS_SETUP, cmd);
            } catch (Exception ex) {
            }
        }

        @JavascriptInterface
        public void checkPassSetup(String _passSetup) {
            try {
                String query = CommandCls.setup(_passSetup);
                sendData(query);
            } catch (Exception ex) {

            }
        }

        @JavascriptInterface
        public void sendControl(String id, String address, String type) {
            String passLogin = sharepredPreferencesCls.getString(PASS_LOGIN);
            String query = CommandCls.controldevice(passLogin, address,id, type);
            sendData(query);
        }

        @JavascriptInterface
        public  void logout()
        {
            clientSocket.disconnect();
            MainActivity.this.finish();
        }
        //endregion

        //region -- Setting --
        @JavascriptInterface
        public void changePass(int type, String oldPass, String newPass, String newPass2) {
            try {
                if(!newPass.equals(newPass2))
                {
                    showToast("Mật khẩu mới không trùng nhau!");
                }
                else if(newPass.trim().length() == 0)
                {
                    showToast("Mật khẩu không được để trống!");
                }
                else if(newPass.trim().length() > 10)
                {
                    showToast("Mật khẩu chỉ bao gồm 6 kí tự!");
                }

                if (type == 1)  // Change PassLogin
                {
                    //String passLogin = sharepredPreferencesCls.getString(PASS_LOGIN);
                    String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                    String query = CommandCls.changePassLogin(passSetup,newPass);
                    sendData(query);
                } else if (type == 2)  // Change PassSetup
                {
                    String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
                    String query = CommandCls.changePassSetup(passSetup, newPass);
                    sendData(query);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        webView.loadUrl("javascript:$(\"#modalChangePass\").modal('show');");
                    }
                });

            } catch (Exception ex) {

            }
        }
        //endregion

        //region -- Send Data --

        /**
         * Gửi lệnh điều khiển xuống ESP
         *
         * @param message: Lệnh điều khiển
         */
        @JavascriptInterface
        public void sendData(String message) {
            try {
                //if (clientSocket.isClosed()) clientSocket.connect();
                clientSocket.sendMessenge(message);
//                Toast.makeText(MainActivity.this, message , Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "sendData. "+ex.toString());
            }
        }
        //endregion

        //endregion
    }
    //endregion

    //region -- Socket IO --
    @Override
    public void connectStatusChange(boolean status) {
        String TAG = "connectSC MAIN";
        clientSocket.GetSocketStatus(TAG);
        if (status == false && IS_ENABLE_RECONNECT == true) {
            clientSocket.reConnect(MainActivity.this);
        }
    }

    @Override
    public void MessengeFromServer(String messenge) {
        try {
            Log.d("RECEIVER:", "RECEIVER MESSAGE: " + messenge);
            //Toast.makeText(MainActivity.this, messenge, Toast.LENGTH_SHORT).show();
            //messenge = messenge.replace(" ", "");
            messenge = messenge.replace("{","").replace("}","");
            String[] rs = messenge.split("=");
            String cmd = rs[0].trim();

            String response = "";
            if (rs.length > 1)
                response = rs[1].trim();

            switch (cmd)
            {
                case "DOL":
                    downloadResponse(response); break;
                case "CTL":                             // Nếu là phản hồi của điều khiển thiết bị
                    break;
                case "SET":
                    setupResponse(response); break;
                case "UPI":
                    insertResponse(response); break;
                case "UPU":
                    updateResponse(response);  break;
                case "UPD":
                    deleteResponse(response); break;
                case "SPS":
                    changeSetupResponse(response);
                case "SPL":
                    changeloginResponse(response);
                    break;
            }
        }
        catch (Exception ex)
        {
            Log.e("RECEIVER","RECEIVER MESSAGE: " + ex.toString());
        }
    }

    //region RESPONSE METHOD
        //region downloadResponse
    private void downloadResponse(String response)
        {
            if (response.length() == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        webView.loadUrl("javascript:noDevice();");
                    }
                });
                return;
            }
            if (response.charAt(response.length()-1) == ';')
            {
                response = response.substring(0, response.length()-1);
                Log.d(TAG, "downloadResponse: "+response);
            }
            //response = response.substring(0, response.length()-1);

            downloadDeviceProcess(response);
        }
    //endregion
        //region setupResponse
    private void setupResponse(String response)
        {
            if (!response.contains("FALSE")) {
                sharepredPreferencesCls.putString(PASS_SETUP, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = findViewById(R.id.wbMain);
                        webView.loadUrl("file:///android_asset/www/setting.html");
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.check_passsetup_error), Toast.LENGTH_SHORT).show();
            }
        }
    //endregion
        //region insertResponse
    private void insertResponse(String response)
        {
            //region  insert device
            if (!response.equals("FALSE")) {
                try {
                    Device device = new Device();
                    device.setDeviceId(response.split(",")[0].trim());
                    device.setDeviceName(response.split(",")[1].trim());
                    device.setAddress(response.split(",")[2].trim());
                    device.setDeviceType(response.split(",")[3].trim());
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

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_nochange), Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
    //endregion
        //region updateResponse
    private void updateResponse(String response)
        {
            //region Update device
            if (!response.equals("FALSE")) {
                try {
                    Device device = new Device();
                    device.setDeviceId(response.split(",")[0].trim());
                    device.setDeviceName(response.split(",")[1].trim());
                    device.setAddress(response.split(",")[2].trim());
                    device.setDeviceType(response.split(",")[3].trim());
                    boolean result = Device.update(MainActivity.this, device.getDeviceId(),device);
                    if (result) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_success), Toast.LENGTH_SHORT).show();
                        getDevicesList();
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.device_update_error), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.device_nochange), Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
    //endregion
        //region deleteResponse
    private void deleteResponse(String response)
        {
            //region Delete device
            if (!response.equals("FALSE")) {
                try {
                    String id = response.split(",")[0].trim();
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
            //endregion
        }
    //endregion
        //region changeloginResponse
    private void changeloginResponse(String response)
        {
            if (response .equals("FALSE")) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.change_plogin_error), Toast.LENGTH_SHORT).show();
            } else {
                sharepredPreferencesCls.putString(PASS_LOGIN, response);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.change_plogin_success), Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        webView.loadUrl("file:///android_asset/www/setting.html");
                    }
                });
            }
        }
    //endregion
        //region changeSetupResponse
        private void changeSetupResponse(String response)
        {
            if (response .equals("FALSE")) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.change_psetup_error), Toast.LENGTH_SHORT).show();
            } else {
                sharepredPreferencesCls.putString(PASS_SETUP, response);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.change_psetup_success), Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbMain);
                        webView.loadUrl("file:///android_asset/www/setting.html");
                    }
                });
            }
        }
    //endregion
    //endregion

    //region -- Get DeviceList --
    private void getDevicesList()
    {
        try {
            List<Device> list = Device.getListDevices(MainActivity.this);
            final String jsonText;
            if (list != null && list.size() > 0) {
                jsonText = new Gson().toJson(list);
                System.out.println("getDevicesList: " + jsonText);
            } else {
                jsonText = "[]";
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    String s = String.format("showDeviceList(%s)", jsonText);
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region -- downloadDeviceProcess --
    private void downloadDeviceProcess(String strDevice) {
        List<Device> lsDevice;
        String[] lsDeviceStr = strDevice.split(";");
        lsDevice = new ArrayList<Device>();
        for (int i = 0; i < lsDeviceStr.length; i++) {
            Device device = new Device();
            String[] x = lsDeviceStr[i].split(",");
            device.setDeviceId(x[0].trim());
            device.setDeviceName(x[1].trim());
            device.setAddress(x[2].trim());
            device.setDeviceType(x[3].trim());
            lsDevice.add(device);
        }
        if (lsDevice != null && lsDevice.size() > 0) {
            updateDevice(lsDevice);
        }
    }
    //endregion

    //region -- updateDevice --
    public void updateDevice(List<Device> lsDevice) {
        //region  -- Insert devices using json data received --
        try {
            // Xóa hết tất cả các thiết bị
            //region -- delete all device --
            try {
                boolean result = Device.deleteAll(MainActivity.this);
                if (result) {
//                  Toast.makeText(MainActivity.this, "Deleted All Devices!", Toast.LENGTH_SHORT).show();
                    System.out.print("Deleted All Devices!");
                    // Insert lại hết các thiết bị vào CSDL
                    for (int i = 0; i < lsDevice.size(); i++) {
                        Device.insert(MainActivity.this, lsDevice.get(i));
                    }
                    //Toast.makeText(MainActivity.this, "Updated List Devices!", Toast.LENGTH_SHORT).show();
                    System.out.print("Updated List Devices!");
                    showDevicesList(lsDevice);
                } else {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "updateDevice. Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
            //endregion
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "updateDevice. Error!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "updateDevice. " +ex.toString());
        }
        //endregion
    }
    //endregion

    //region -- showDevicesList --
    public void showDevicesList(List<Device> list) {
        try {
            final String jsonText;
            if (list != null && list.size() > 0) {
                jsonText = new Gson().toJson(list);
                System.out.println("showDevicesList: " + jsonText);
            } else {
                jsonText = "[]";
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    String s = String.format("initControl(%s)", jsonText);
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Ex: " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //endregion

    //region -- Start Stop --
    @Override
    protected void onStart() {
        super.onStart();

        String TAG = "START MAIN";
        IS_ENABLE_RECONNECT = true;
        try {
            if (clientSocket == null)
            {
                String _ip = sharepredPreferencesCls.getString(IP_ADDRESS);
                String _port = sharepredPreferencesCls.getString(PORT);
                clientSocket = new ClientSocket(_ip, Integer.parseInt(_port));
                clientSocket.setServerListener(MainActivity.this);
                clientSocket.connect();
            }
            clientSocket.GetSocketStatus(TAG);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onStart. "+ex.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String TAG = "RESTART MAIN";
        clientSocket.GetSocketStatus(TAG);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String TAG = "RESTART MAIN";
        try {
            if (clientSocket != null)
                clientSocket.GetSocketStatus(TAG);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onRestart"+ ex.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_ENABLE_RECONNECT = false;
        String TAG = "DESTROY MAIN";
        try {
            if (clientSocket != null && clientSocket.isClosed() == false)
            {
                clientSocket.GetSocketStatus(TAG);
                clientSocket.disconnect();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onDestroy. "+ ex.toString());
        }
    }

    //endregion

    //region -- Can go Back --
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.warning);
        builder.setTitle(getResources().getString(R.string.ms_title));
        builder.setMessage(getResources().getString(R.string.ms_content));
        // Add the buttons
        builder.setPositiveButton(getResources().getString(R.string.ms_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                    System.exit(0);
                }
                else
                {
                    finish();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.ms_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    //endregion

    //region Navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    String s = String.format("openSetting()");
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        } else if(id == R.id.nav_logout) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = (WebView) findViewById(R.id.wbMain);
                    String s = String.format("openLogoutModal()");
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        }
        else if (id == R.id.nav_share) {
            //Toast.makeText(MainActivity.this, "Tính năng đang được phát triển!", Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(MainActivity.this, Html.fromHtml("<font background-color='#FFFF00' font-size='30px' color='#FF0000' ><b>Tính năng đang được phát triển!</b></font>"), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (id == R.id.nav_send) {
            //Toast.makeText(MainActivity.this, "Tính năng đang được phát triển!", Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(MainActivity.this, Html.fromHtml("<font background-color='#FFFF00' font-size='30px' color='#FF0000' ><b>Tính năng đang được phát triển!</b></font>"), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (id == R.id.website) {
            goToHomePage();
        } else if (id == R.id.infor) {
            showInforCompany();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToHomePage() {
        String url = "http://vste.vn/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void showInforCompany() {
        WebView webView = new WebView(MainActivity.this);
        webView.loadUrl("file:///android_asset/www/about.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(webView).show();
    }


    //endregion

}

