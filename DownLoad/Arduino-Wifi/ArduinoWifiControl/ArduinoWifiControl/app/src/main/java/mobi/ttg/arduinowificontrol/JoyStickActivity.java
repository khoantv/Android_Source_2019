package mobi.ttg.arduinowificontrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import mobi.ttg.customviews.JoyStickView;

/**
 * Created by duong on 2/23/2016.
 */
public class JoyStickActivity extends Activity implements ClientSocket.ServerListener, View.OnClickListener {
    private Button btT, btB, btC, btR, btL;
    private ProgressBar prSpeed;
    private JoyStickView joyStickView;
    private ClientSocket clientSocket;
    private Settings settings;
    private Animation btAnim;
    private boolean socketConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_joystick);
        initViews();
        settings = new Settings(this);
        clientSocket = new ClientSocket(settings.getString(Settings.IP_ADDRESS), settings.getInt(Settings.PORT));
        clientSocket.setServerListener(this);
    }

    private void initViews() {
        btAnim = AnimationUtils.loadAnimation(this, R.anim.bt_anim);
        btT = (Button) findViewById(R.id.bt_t);
        btB = (Button) findViewById(R.id.bt_b);
        btC = (Button) findViewById(R.id.bt_c);
        btR = (Button) findViewById(R.id.bt_r);
        btL = (Button) findViewById(R.id.bt_l);
        btT.setOnClickListener(this);
        btB.setOnClickListener(this);
        btC.setOnClickListener(this);
        btR.setOnClickListener(this);
        btL.setOnClickListener(this);
        prSpeed = (ProgressBar) findViewById(R.id.pr_speed);
        joyStickView = (JoyStickView) findViewById(R.id.joy_stick_view);
        joyStickView.setSpeedProgress(prSpeed);
    }

    @Override
    protected void onStart() {
        super.onStart();
        clientSocket.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clientSocket.disconnect();
    }

    @Override
    public void connectStatusChange(boolean status) {
        this.socketConnected = status;
        if(status){
            joyStickView.setClientSocket(clientSocket);
            clientSocket.sendMessenge("OK");
        }
    }

    @Override
    public void newMessengeFromServer(String messenge) {

    }

    @Override
    public void onClick(View v) {
        v.startAnimation(btAnim);
        if(!socketConnected){
            return;
        }
        switch (v.getId()){
            case R.id.bt_b:
                clientSocket.sendMessenge("DBB;");
                break;
            case R.id.bt_c:
                clientSocket.sendMessenge("DBC;");
                break;
            case R.id.bt_l:
                clientSocket.sendMessenge("DBL;");
                break;
            case R.id.bt_r:
                clientSocket.sendMessenge("DBR;");
                break;
            case R.id.bt_t:
                clientSocket.sendMessenge("DBT;");
                break;
        }
    }
}
