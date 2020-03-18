package kmt.jimmy.studyfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;

import kmt.jimmy.dataentity.User;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        //DatabaseReference x = mDatabase.child("users");
        mDatabase.child("users").child(userId).setValue(user);
        //mDatabase.child("users").child(userId).child("username").setValue(name);
    }


    DatabaseReference myRef;
    public void addData(View v)
    {
         writeNewUser("1", "Khoan", "truongkhoan92@mgail.com");
//        EditText edtValue = findViewById(R.id.edtValue);
//        String value = edtValue.getText().toString();
//        myRef.setValue(value);
    }
}
