package kmt.jimmy.smartcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //region . Variables .
    ImageView imbPower, imbCheckInfor, imbOption, imbProtect;
    TextView txtProtect, txtPower;
    private final String PHONE_KEY = "PHONE_NUMBER";
    private static MainActivity mainActivity;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }
    private boolean isProtect = true, isPower = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

        //region ---- Show phone if exist.
        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if (phone == null || phone.equals("")) {
            startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
        }
        //endregion
    }

    //region . Init Control .
    private void Init() {
        imbCheckInfor = (ImageView) findViewById(R.id.imbCheckInfor);
        imbOption = (ImageView) findViewById(R.id.imbOption);
        imbPower = (ImageView) findViewById(R.id.imbPowerOff);
        imbProtect = (ImageView) findViewById(R.id.imbProtect);
        txtPower = (TextView) findViewById(R.id.txtPower);
        txtProtect = (TextView) findViewById(R.id.txtProtect);

        imbPower.setTag(R.drawable.power_off);
        imbProtect.setTag(R.drawable.unlock);

    }
    //endregion

    //region . Send SMS Function .
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

    //region . Set Protect Button .
    public void fSetProtect(View v) {
        ImageView imbProtect = (ImageView) v;

        if (isProtect)
        {
            imbProtect.setImageResource(R.drawable.unlock);
            txtProtect.setText(R.string.on_protect);
            sendSMSMessage("ON_PR");
            isProtect = false;
        }
        else {
            imbProtect.setImageResource(R.drawable.lock);
            txtProtect.setText(R.string.off_protect);
            sendSMSMessage("OFF_PR");
            isProtect = true;
        }

    }
    //endregion

    //region . Set Power Button .
    public void fSetPower(View v) {
        ImageView imbPower = (ImageView) v;

        if (isPower)
        {
            imbPower.setImageResource(R.drawable.power_on);
            txtPower.setText(R.string.power_on);
            sendSMSMessage("ON_PO");
            isPower = false;
        }
        else {
            imbPower.setImageResource(R.drawable.power_off);
            txtPower.setText(R.string.power_off);
            sendSMSMessage("OFF_PO");
            isPower = true;
        }
    }
    //endregion

    public void fGetInformation(View v) {
        sendSMSMessage("KTTK");
    }

    public void fOption(View v)
    {
        startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
    }

    public void setValueChange() {


    }

}
