package com.example.tieub.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //final String Arr[] = {"TEO", "TY", "BIN", "BO"};
        ArrayList<Students> ls = new ArrayList<Students>();
        ls.add(new Students(1, "Khoan"));
        ls.add(new Students(2, "HÙng"));
        ls.add(new Students(3, "Hiếu"));
        ls.add(new Students(4, "Biên"));


        ListView lv = (ListView) findViewById(R.id.lv);

        MyArrayAdapter adapter = new MyArrayAdapter(this, R.layout.layout, ls);
        lv.setAdapter(adapter);

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //Cách 1:
//                //String name =  Arr[position];
//                // Cách 2:
//                String name = (String) adapterView.getItemAtPosition(position);
//                Toast.makeText(MainActivity.this, "name = "+ name , Toast.LENGTH_SHORT).show();
//            }
//        });


    }
}
