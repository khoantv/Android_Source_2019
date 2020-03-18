package kmt.jimmy.smartpump;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetphoneActivity extends AppCompatActivity {

    private EditText edtPhone;
    private SharedPreferencesCls sharedPreference;
    private final String PHONE_KEY = "PHONE_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setphone);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //region ---- GetControl
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        //endregion

        //region ---- Show phone if exist.
        String phone = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
        if(phone != null && !phone.equals(""))
        {
            edtPhone.setText(phone);
        }
        //endregion
    }

    //region ---- Save Phone
    public  void savePhoneNumber(View v)
    {
        try {
            // Hides the soft keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                imm.hideSoftInputFromWindow(edtPhone.getWindowToken(), 0);
            }

            String phone = edtPhone.getText().toString().trim().replace(" ", "");
            SharedPreferencesCls.setPrefVal(getApplicationContext(), PHONE_KEY, phone);
            Toast.makeText(getApplicationContext(), "Saved!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Can not Save!",
                    Toast.LENGTH_SHORT).show();
            Log.e("SetPhone", ex.toString());
        }
    }
    //endregion

    public void setParameters(View v)
    {
        TimePicker timePicker1;
        timePicker1 = (TimePicker) findViewById(R.id.timePicker);
        int hour = timePicker1.getCurrentHour();
        int min = timePicker1.getCurrentMinute();

        EditText edt = (EditText)findViewById(R.id.edtThreshold);
        if (edt.getText().toString().trim().length() == 0)
        {
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập độ ẩm!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int threshold = Integer.parseInt(edt.getText().toString().trim());
        //Toast.makeText(getApplicationContext(), hour + " - " + min + " - "+ threshold,
        //        Toast.LENGTH_SHORT).show();
        String sms = hour + ";" + min + ";"+ threshold;
        sendSMSMessage(sms);
    }

    //region . Send SMS Function .
    protected void sendSMSMessage(String message) {
        try {
            String phoneNo = SharedPreferencesCls.getPrefVal(getApplicationContext(), PHONE_KEY);
            if (phoneNo == null || phoneNo.equals("")) {
                //startActivity(new Intent(MainActivity.this, SetphoneActivity.class));
                Toast.makeText(SetphoneActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                //Toast.makeText(MainActivity.this, message , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(SetphoneActivity.this, "Can't send SMS.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

}
