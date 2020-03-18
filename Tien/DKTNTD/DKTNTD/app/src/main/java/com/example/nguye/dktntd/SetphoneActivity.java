package com.example.nguye.dktntd;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
        edtPhone = (EditText) findViewById(R.id.edtMoisture);
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

}
