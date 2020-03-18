package vst.jimmy.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vst.jimmy.bll.CommandCls;
import vst.jimmy.bll.Device;
import vst.jimmy.bll.Master;
import vst.jimmy.broadcast.NetworkChangeReceiver;
import vst.jimmy.broadcast.NetworkChangeResult;
import vst.jimmy.db.NetworkUtil;
import vst.jimmy.db.SharepredPreferencesCls;
import vst.jimmy.socket.ClientSocket;
import vst.jimmy.socket.CurrentSession;

public class StartActivity extends AppCompatActivity implements ClientSocket.ServerListener, NetworkChangeResult
                                                        , NavigationView.OnNavigationItemSelectedListener{

    //region  Variables
    private static String TAG = "START_ACTIVITY";
    private String floor, ip, port, pass;
    private ClientSocket clientSocket;
    private boolean connected = false, isGo = false;
    private SharepredPreferencesCls sharepredPreferencesCls;

    private static final String LIST_MASTER = "list_master";
    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "port";
    private static final String NAME = "name";

    private static final String PASS_SETUP = "pass_setup";
    private static final String PASS_LOGIN = "pass_login";
    private static final String IS_SAVE_PASS_LOGIN = "IS_SAVE_PASS_LOGIN";
    private static final String IS_SAVE_PASS_SETUP = "IS_SAVE_PASS_SETUP";

    private static final String IS_REMEMBER = "YES";

    private NetworkChangeReceiver mReceiver;
    //endregion

    private Button btnSelect, btnConnect;
    private CheckBox chkShow, chkSave;
    private EditText edtMasterName, edtIp, edtPort, edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initPermission();

        btnConnect = findViewById(R.id.btnConnect);
        edtIp = findViewById(R.id.edtIp);
        edtPort = findViewById(R.id.edtPort);
        edtPass = findViewById(R.id.edtPassWord);
        chkShow = findViewById(R.id.chkShow);
        chkSave = findViewById(R.id.chkSave);

        edtMasterName = findViewById(R.id.edtMasterName);

        mReceiver = new NetworkChangeReceiver(StartActivity.this);

        //region SharepredPreferencesCls
        sharepredPreferencesCls = new SharepredPreferencesCls(StartActivity.this);
        String name = sharepredPreferencesCls.getString(NAME);
        String passSetup = sharepredPreferencesCls.getString(PASS_SETUP);
        String passLogin = sharepredPreferencesCls.getString(PASS_LOGIN);
        String is_save_pass_login = sharepredPreferencesCls.getString(IS_SAVE_PASS_LOGIN);
        String is_save_pass_setup = sharepredPreferencesCls.getString(IS_SAVE_PASS_SETUP);

        if (name == null)
            sharepredPreferencesCls.putString(NAME, "");

        if (passSetup == null)
            sharepredPreferencesCls.putString(PASS_SETUP, "78654321");
        if (passLogin == null)
            sharepredPreferencesCls.putString(PASS_LOGIN, "12345678");
        if (is_save_pass_setup == null)
            sharepredPreferencesCls.putString(IS_SAVE_PASS_SETUP, "YES");
        if (is_save_pass_login == null)
            sharepredPreferencesCls.putString(IS_SAVE_PASS_LOGIN, "YES");

        String _ip = sharepredPreferencesCls.getString(IP_ADDRESS);
        String _port = sharepredPreferencesCls.getString(PORT);
        String _name = sharepredPreferencesCls.getString(NAME);

        if (_name != null && !_name.isEmpty())
            edtMasterName.setText(_name);

        if (_ip != null && !_ip.isEmpty())
            edtIp.setText(_ip);

        if (_port != null && !_port.isEmpty())
            edtPort.setText(_port);

        if(is_save_pass_login != null && is_save_pass_login.equals("YES"))
        {
            String _pass = sharepredPreferencesCls.getString(PASS_LOGIN);
            edtPass.setText(_pass);
            chkSave.setChecked(true);
        }
        else
        {
            chkSave.setChecked(false);
        }
        //endregion

        //region -- Kiểm tra kết nối mạng
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean isConnected = NetworkUtil.getConnectivityStatus(StartActivity.this);
                if (isConnected == false)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnConnect.setEnabled(false);
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnConnect.setEnabled(true);
                        }
                    });
                }
            }
        }, 1000, 10000);
        //endregion

        //region -- Navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion

        //region checkbox
        chkShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked)
                {
                    edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else
                {
                    edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        chkSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    sharepredPreferencesCls.putString(IS_SAVE_PASS_LOGIN, "YES");
                }
                else
                {
                    sharepredPreferencesCls.putString(IS_SAVE_PASS_LOGIN, "NO");
                }
            }
        });
        //endregion
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.INTERNET)) {
                    Toast.makeText(StartActivity.this, "Không được cấp quyền truy cập Internet.", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(StartActivity.this, "Không được cấp quyền truy cập Internet. Không hỏi lại.", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(StartActivity.this, "Đã được cấp quyền truy cập Internet.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StartActivity.this, "Quyền truy cập Internet bị từ chối.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void connectToESP(View view)
    {
        String _masterName = edtMasterName.getText().toString();
        String _ip = edtIp.getText().toString();
        String _port = edtPort.getText().toString();
        String _pass = edtPass.getText().toString();

        if (_masterName.isEmpty())
        {
            Toast.makeText(StartActivity.this, getResources().getString(R.string.master_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (_ip.isEmpty())
        {
            Toast.makeText(StartActivity.this, getResources().getString(R.string.ip_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (_port.isEmpty())
        {
            Toast.makeText(StartActivity.this, getResources().getString(R.string.port_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (_pass.isEmpty())
        {
            Toast.makeText(StartActivity.this, getResources().getString(R.string.pass_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (clientSocket == null) {   // chưa kết nối
            clientSocket = new ClientSocket(_ip, Integer.parseInt(_port));
            clientSocket.setServerListener(StartActivity.this);
            clientSocket.connect();
            btnConnect.setText(getResources().getString(R.string.connecting));
        }
        else    // đã kết nối, gửi lại kiểm tra mật khẩu.
        {
            if (clientSocket.isClosed()) clientSocket.connect();
            String query = CommandCls.login(_pass);
            sendData(query);
            btnConnect.setText(getResources().getString(R.string.connecting));
        }
    }

    public void selectMaster(View view)
    {
        try {
            String lsMaster = sharepredPreferencesCls.getString(LIST_MASTER);
            if (lsMaster == null || lsMaster.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(StartActivity.this).create();
                alertDialog.setTitle("Thông Báo");
                alertDialog.setMessage("Chưa có lịch sử.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else
            {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Master>>(){}.getType();
                final List<Master> ls = gson.fromJson(lsMaster, type);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Chọn phòng đã lưu: ");

                // add a list
                String[] masterName = new String[ls.size()];
                for (int  i = 0; i < masterName.length; i++)
                {
                    masterName[i] = ls.get(i).getMasterName();
                }
                builder.setItems(masterName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        edtMasterName.setText(ls.get(position).getMasterName());
                        edtIp.setText(ls.get(position).getMasterIp());
                        edtPort.setText(ls.get(position).getMasterPort()+"");
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
       catch (Exception ex)
       {
           Log.e(TAG, "selectMaster: "+ ex.toString());
       }
    }

    public void saveIpPort(String name, String ip, String port)
    {
        sharepredPreferencesCls.putString(NAME, name);
        sharepredPreferencesCls.putString(IP_ADDRESS, ip);
        sharepredPreferencesCls.putInt(PORT, Integer.parseInt(port));
        Log.d(TAG, "Saved Ip and Port."+ port + "; "+ ip);
        String lsMaster = sharepredPreferencesCls.getString(LIST_MASTER);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Master>>(){}.getType();
        List<Master> ls = gson.fromJson(lsMaster, type);
        Master master = new Master();
        master.setMasterName(name);
        master.setMasterIp(ip);
        master.setMasterPort(Integer.parseInt(port));

        if (ls != null && ls.size() >= 0) {
            for(int i = 0; i< ls.size(); i ++) {
                Master item = ls.get(i);
                if (item.getMasterIp().equals(master.getMasterIp()) && item.getMasterPort() == master.getMasterPort())
                    return;
            }
        }
        else if (ls == null)
        {
            ls = new ArrayList<>();
        }
        ls.add(master);
        lsMaster = gson.toJson(ls);
        sharepredPreferencesCls.putString(LIST_MASTER, lsMaster);
    }

    public void clearListIp()
    {
        String lsMaster = sharepredPreferencesCls.getString(LIST_MASTER);
        if(lsMaster == null || lsMaster.length() == 0)
        {
            String mess = "Danh sách trống. Không có gì để xóa!";
            Toast.makeText(StartActivity.this, mess, Toast.LENGTH_SHORT ).show();
            return;
        }
        sharepredPreferencesCls.putString(LIST_MASTER, "");
        String mess = "Xóa thành công!";
        Toast.makeText(StartActivity.this, mess, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void checkInternetConnection(boolean isConnected) {
        Log.d(TAG, "isConnected: "+ isConnected);
        if (isConnected == false)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnConnect.setEnabled(false);
                }
            });
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnConnect.setEnabled(true);
                }
            });
        }
    }

    //region -- Socket IO --
    @Override
    public void connectStatusChange(boolean status) {
        String TAG = "connectSC START";
        clientSocket.GetSocketStatus(TAG);

        Log.d(TAG, "status: "+ status);
        Log.d(TAG, "socket status: "+ clientSocket.isClosed());

        if (status) {
            saveIpPort(edtMasterName.getText().toString(),edtIp.getText().toString(), edtPort.getText().toString() );

            // Kết nối thành công thì gửi lệnh kiểm tra mật khẩu
            sharepredPreferencesCls.putString(NAME, edtMasterName.getText().toString());
            sharepredPreferencesCls.putString(IP_ADDRESS, edtIp.getText().toString());
            sharepredPreferencesCls.putString(PORT, edtPort.getText().toString());

            String _pass = edtPass.getText().toString();
            String query = CommandCls.login(_pass);
            sendData(query);
            btnConnect.setText(getResources().getString(R.string.check_pass));
        } else {
            //Toast.makeText(StartActivity.this, getResources().getString(R.string.connect_error), Toast.LENGTH_SHORT).show();
            btnConnect.setText(getResources().getString(R.string.connect));
        }
    }

    public void sendData(String message) {
        try {
            clientSocket.sendMessenge(message);
//            Toast.makeText(StartActivity.this, message , Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            //Toast.makeText(StartActivity.this, getResources().getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.toString());
        }
    }

    @Override
    public void MessengeFromServer(String messenge) {
        try {
            Log.d("RECEIVER:", "RECEIVER MESSAGE: " + messenge);
            messenge = messenge.replace("{","").replace("}","");
            String[] rs = messenge.split("=");
            String cmd = rs[0].trim();

            String response = "";
            if (rs.length > 1)
                response = rs[1].trim();

            if(cmd.equals("LOG"))
            {
                loginResponse(response);
            }
        }
        catch (Exception ex)
        {
            Log.e("RECEIVER","RECEIVER MESSAGE: " + ex.toString());
        }
    }

    //region loginResponse
    private void loginResponse(String response)
    {
        if (!response.contains("FALSE"))   // đăng nhập thành công
        {
            final String passLogin = response.split(",")[0];
            sharepredPreferencesCls.putString(PASS_LOGIN, passLogin);
            // Chuyển sang giao diện điều khiển
            isGo = true;
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        } else  // đăng nhập thất bại
        {
            Toast.makeText(StartActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_SHORT).show();
            btnConnect.setText(getResources().getString(R.string.connect));
        }
    }
    //endregion

    //endregion

    //endregion

    //region -- Start Stop --
    @Override
    protected void onPause() {
        super.onPause();
        String TAG = "PAUSE of START";
        try {
            if (clientSocket.socket != null)
            {
                clientSocket.socket.close();
                clientSocket.GetSocketStatus(TAG);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onPause: "+ ex.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String TAG = "START of START";
        try {
            if (clientSocket != null)
                clientSocket.GetSocketStatus(TAG);
    //        if (CurrentSession.isConnected == true)
    //        {
    //            startActivity(new Intent(StartActivity.this, MainActivity.class));
    //            isGo = true;
    //        }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onStart: "+ ex.toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String TAG = "RESTART of START";
        btnConnect.setText(getResources().getString(R.string.connect));
        try {
            if (clientSocket != null)
                clientSocket.GetSocketStatus(TAG);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onRestart: " +ex.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String TAG = "DESTROY of START";
        try {
            if (clientSocket != null && clientSocket.isClosed() == false)
            {
                clientSocket.disconnect();
                clientSocket.GetSocketStatus(TAG);
            }

        }
        catch (Exception ex)
        {
            Log.e(TAG, "onDestroy: "+ex.toString());
        }
    }

    //endregion

    //region -- Can go Back --
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
            builder.setIcon(R.drawable.warning);
            builder.setTitle(getResources().getString(R.string.ms_title));
            builder.setMessage(getResources().getString(R.string.ms_content));
            // Add the buttons
            builder.setPositiveButton(getResources().getString(R.string.ms_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
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
    }

    //endregion

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

        //region -- Start Interface --
        @JavascriptInterface
        public void getListIp() {

            String lsMaster = sharepredPreferencesCls.getString(LIST_MASTER);
            //String result = "";
            if (lsMaster == null)
            {
                lsMaster = "[]";
            }
            else if (lsMaster.trim().length() == 0)
            {
                lsMaster = "[]";
            }

            final String finalResult = lsMaster;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = findViewById(R.id.wbMain);
                    String s = String.format("showListIp(%s)", finalResult);
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
        }

        //endregion
    }

    //region Navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            //Toast.makeText(StartActivity.this, "Tính năng đang được phát triển!", Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(StartActivity.this, Html.fromHtml("<font style='background-color:#FFFF00' font-size='30px' color='#FF0000' ><b>Tính năng đang được phát triển!</b></font>"), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (id == R.id.nav_send) {
            //Toast.makeText(StartActivity.this, "Tính năng đang được phát triển!", Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(StartActivity.this, Html.fromHtml("<font style='background-color:#FFFF00' font-size='30px' color='#FF0000' ><b>Tính năng đang được phát triển!</b></font>"), Toast.LENGTH_LONG);
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
        WebView webView = new WebView(StartActivity.this);
        webView.loadUrl("file:///android_asset/www/about.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        builder.setView(webView).show();
    }
    //endregion
}
