package kmt.jimmy.healthtakecare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT < 23){
            //your code here
        }else {
            requestContactPermission();
        }
    }

    final private int PERMISSION_REQUEST_CODE = 123;

    private void requestContactPermission() {

        int hasContactPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS);

        if(hasContactPermission != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]   {Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);
        }else {
            Toast.makeText(MainActivity.this, "Contact Permission is already granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Permission", "Read SMS permission has now been granted. Showing result.");
                    Toast.makeText(this,"Read SMS Permission is Granted",Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Permission", "Read SMS permission was NOT granted.");
                }
                break;
        }
    }
}
