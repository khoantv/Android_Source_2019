package kmt.truongkhoan.ledcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetMacActivity extends Activity {

    private EditText edtMac;
    private Button btnSetMac;
    private SharedPreference sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //region ---- GetControl
        edtMac = (EditText) findViewById(R.id.edtMac);
        btnSetMac = (Button) findViewById(R.id.btnMac);
        //endregion

        //region ---- Show phone if exist.
        //String phone = LoadPreferences();
        sharedPreference = new SharedPreference();
        String phone = sharedPreference.getValue(getApplicationContext());
        if(phone != null)
        {
            edtMac.setText(phone);
        }
        //endregion

        //region ---- Save Phone
        btnSetMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Hides the soft keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtMac.getWindowToken(), 0);

                    String phone = edtMac.getText().toString().trim().replace(" ", "");
                    //SavePreferences("PHONE", phone);
                    sharedPreference = new SharedPreference();
                    sharedPreference.save(getApplicationContext(), phone);
                    Toast.makeText(getApplicationContext(), "Saved!",
                            Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(), "Can not Save!",
                            Toast.LENGTH_SHORT).show();
                    Log.e("SetPhone", ex.toString());
                }
            }
        });
        //endregion
    }
}
