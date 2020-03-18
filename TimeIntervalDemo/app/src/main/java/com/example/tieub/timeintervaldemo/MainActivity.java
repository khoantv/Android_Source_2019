package com.example.tieub.timeintervaldemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private  static final int REPEAT_TIME_IN_SECONDS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), REPEAT_TIME_IN_SECONDS*1000, );
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    show();
            }
        }, 1000, 5000);
    }

    int  i = 0;
    public void show()
    {
        System.out.println("i = "+ i);
        i++;
    }
}
