package com.example.nguye.dktntd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import dbAccess.getCurrentData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Switch tb1, tb2, tb3, tb4;
    private SeekBar seekMoisture;
    private TextView txtValueHumidity, txtValueTemperature, txtWaterLevel, txtPower;

    private final String PHONE_KEY = "PHONE_NUMBER";
    private final String LOGTAG = "MAIN_ACTIVITY";

    private static MainActivity mainActivity;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Init();
        mainActivity = MainActivity.this;

        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if (phone == null || phone.equals("")) {
            startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //region -- Nav --
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion

        //region -- Get current Data t show textView --
        new getCurrentData().execute("1");
        //endregion

        //region -- hàm gửi lệnh điều khiển --
        fSetBtnSTT();
        //endregion
    }

    private void Init() {
        txtValueHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtValueTemperature = (TextView) findViewById(R.id.txtTemperature);
        txtWaterLevel = (TextView) findViewById(R.id.txtLevel);
        txtPower = (TextView) findViewById(R.id.txtPower);
        tb1 = (Switch) findViewById(R.id.switchTB1);
        tb2 = (Switch) findViewById(R.id.switchTB2);
        tb3 = (Switch) findViewById(R.id.switchTB3);
        tb4 = (Switch) findViewById(R.id.switchTB4);

        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) sendSMSMessage("TB1 ON");
                else sendSMSMessage("TB1 OFF");
            }
        });
        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) sendSMSMessage("TB2 ON");
                else sendSMSMessage("TB2 OFF");
            }
        });
        tb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) sendSMSMessage("TB3 ON");
                else sendSMSMessage("TB3 OFF");
            }
        });
        tb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) sendSMSMessage("TB4 ON");
                else sendSMSMessage("TB4 OFF");
            }
        });
    }


    //region . Send SMS Function .
    public void fSetBtnSTT(){
//        btnOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSMSMessage("OFF_ON");
//                imgOn.setBackgroundColor(Color.parseColor("#ff1eff00"));
//                Toast.makeText(MainActivity.this, "ON" , Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    protected void sendSMSMessage(String message) {
        try {
            String phoneNo = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
            if (phoneNo == null || phoneNo.equals("")) {
                //startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                //Toast.makeText(MainActivity.this, message , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Can't send SMS.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region . Check Infor .
    public void fGetInformation(View v) {
        //region . Kiểm tra tk máy hiện tại => Không dùng
        //String number = "*101";
        String number = Uri.encode("*101#");
        Uri call = Uri.parse("tel:" + number);
        Intent surf = new Intent(Intent.ACTION_CALL, call);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(surf);
        //endregion
        sendSMSMessage("KTTK");
    }
    //endregion

    public void fOption2(View v)
    {
        startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
    }

    public void setValueChange(String ValueHumidity, String ValueTemperature, String WaterLevel, String Power) {
        if (!ValueHumidity.equals(null)) txtValueHumidity.setText(ValueHumidity);
        if (!ValueTemperature.equals(null)) txtValueTemperature.setText(ValueHumidity);
        if (!WaterLevel.equals(null)) txtWaterLevel.setText(ValueHumidity);
        if (!Power.equals(null)) txtPower.setText(ValueHumidity);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
