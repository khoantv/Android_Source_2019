package example.jimmy.folioreaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.folioreader.FolioReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FolioReader folioReader = FolioReader.get();
        folioReader.openBook("file:///android_asset/15_luat_moi.epub");
    }
}
