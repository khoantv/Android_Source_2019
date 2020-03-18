package audiorecord.dac.com.ghiam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnGhiAm;
    private EditText edtFolderName;
    private EditText edtFileName;
    private RadioButton rdbInternal;
    private RadioButton rdbExternal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btnGhiAm = (Button) findViewById(R.id.btnGhiAm);
        edtFolderName = (EditText) findViewById(R.id.edtFolderName);
        edtFileName = (EditText) findViewById(R.id.edtFileName);
        rdbInternal = (RadioButton) findViewById(R.id.rdbInternal);
        rdbExternal = (RadioButton) findViewById(R.id.rdbExternal);

        rdbExternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                    rdbInternal.setChecked(false);
                else    {
                    rdbInternal.setChecked(true);
                }
            }
        });

        rdbInternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                    rdbExternal.setChecked(false);
                else    {
                    rdbExternal.setChecked(true);
                }
            }
        });
    }

    public void recordAudio(View v) {
        String FolderName, FileName;
        Boolean isExternal;
        FolderName = edtFolderName.getText().toString();
        FileName =  edtFileName.getText().toString();
        isExternal = rdbInternal.isChecked();
        if (FolderName.isEmpty()) {
            Toast.makeText(MainActivity.this, "Folder Name must be fill.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (FileName.isEmpty()) {
            Toast.makeText(MainActivity.this, "File Text Name must be fill.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("FolderName", FolderName);
        bundle.putString("FileName", FileName);
        bundle.putBoolean("isExternal", isExternal);
        Intent myIntent = new Intent(MainActivity.this , RecordActivity.class); // kết thúc activity cũ mở ra một cái activivy mới, nhìn this với class thì rõ hihi.
        myIntent.putExtra("mypackage", bundle);
        startActivity(myIntent);                // Bắt đầu mở sang activity mới
    }

}
