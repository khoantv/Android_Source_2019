package kmt.jimmy.smartpump;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity { // implements onChangeData

    ImageView imbCheckInfor, imbOption;
    ToggleButton togPower;
    SeekBar seekMoisture;
    TextView txtPercent;
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
        setContentView(R.layout.activity_main);
        Init();

        mainActivity = MainActivity.this;

        //region ---- Show phone if exist.
        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if (phone == null || phone.equals("")) {
            startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
        }
        //endregion

        //region . Set value change of Seekbar .
        this.seekMoisture.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // Khi giá trị progress thay đổi.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                txtPercent.setText(progressValue + "/" + seekBar.getMax());
                //Log.i(LOGTAG, "Changing seekbar's progress");
            }

            // Khi người dùng bắt đầu cử chỉ kéo thanh gạt.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(LOGTAG, "Started tracking seekbar");
            }

            // Khi người dùng kết thúc cử chỉ kéo thanh gạt.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtPercent.setText(progress + "/" + seekBar.getMax());
                Log.i(LOGTAG, "Stopped tracking seekbar");
            }
        });
        //endregion
    }

    //region . Init Control .
    private void Init() {
        imbCheckInfor = (ImageView) findViewById(R.id.imbCheckInfor);
        imbOption = (ImageView) findViewById(R.id.imbOption);
        togPower = (ToggleButton) findViewById(R.id.togPower);
        seekMoisture = (SeekBar) findViewById(R.id.sbMoisture);
        txtPercent = (TextView) findViewById(R.id.txtPercent);
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

    //region . Set Power Button .
    public void fSetPower(View v) {
        ToggleButton imbPower = (ToggleButton) v;
        if (imbPower.isChecked())
        {
            //sendSMSMessage("ON_PO");
            Toast.makeText(MainActivity.this, "ON" , Toast.LENGTH_SHORT).show();
        }
        else
        {
            //sendSMSMessage("OFF_PO");
            Toast.makeText(MainActivity.this, "OFF" , Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region . Check Infor .
    public void fGetInformation(View v) {
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
    }
    //endregion

    //region . Setting .
    public void fOption(View v)
    {
        startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
    }
    //endregion

    private int getDrawableId(ImageView iv) {
        return (Integer) iv.getTag();
    }

    public void setValueChange(boolean isOn, String moisture) {
        Toast.makeText(MainActivity.this, "2. "+ isOn + " - " + moisture, Toast.LENGTH_SHORT).show();
        Log.d(LOGTAG, "2. "+ isOn + " - " + moisture);
        int progressValue = Integer.parseInt(moisture);

        SeekBar seekMoisture = (SeekBar) findViewById(R.id.sbMoisture);
        TextView txtPercent = (TextView) findViewById(R.id.txtPercent);

        seekMoisture.setProgress(progressValue);
        txtPercent.setText(progressValue + "/" + seekMoisture.getMax());
        if (isOn)
        {
            togPower.setChecked(true);
        }
        else
        {
            togPower.setChecked(false);
        }
    }

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
