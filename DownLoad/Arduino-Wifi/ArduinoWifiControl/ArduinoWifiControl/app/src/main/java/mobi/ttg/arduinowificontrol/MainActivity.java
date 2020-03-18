package mobi.ttg.arduinowificontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ClientSocket.ServerListener, ViewPager.OnPageChangeListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private Settings settings;
    private ClientSocket clientSocket;
    private EditText edIP;
    private EditText edPort;
    private Button btConnect;
    private RecyclerView rcConversation, rcListDevice;
    private boolean connected = false;
    private MessengeListAdapter messengeListAdapter;
    private DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        settings = new Settings(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        messengeListAdapter = new MessengeListAdapter(this, new ArrayList<MessengeItem>());
        deviceListAdapter = new DeviceListAdapter(this, new Database(this));
    }

    @Override
    public void connectStatusChange(boolean status) {
        this.connected = status;
        this.btConnect.setText(status ? "DISCONNECT" : "CONNECT");
        this.edIP.setEnabled(!status);
        this.edPort.setEnabled(!status);
        this.mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void newMessengeFromServer(String messenge) {
        messengeListAdapter.addMessenge(new MessengeItem(MessengeItem.TYPE_IN, messenge));
        rcConversation.scrollToPosition(messengeListAdapter.getItemCount()-1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==3){
            Intent i = new Intent(this,JoyStickActivity.class);
            startActivity(i);
            mViewPager.setCurrentItem(position-1);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class PlaceholderFragment extends Fragment {

        private int sectionNumber;

        public PlaceholderFragment(){

        }

        public PlaceholderFragment(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            switch (sectionNumber){
                case 1:
                    View v1 = inflater.inflate(R.layout.fragment_connect, container, false);
                    TextView tvWifiStatus = (TextView) v1.findViewById(R.id.tv_wifiStatus);
                    TextView tvWifiSSID = (TextView) v1.findViewById(R.id.tv_wifiSSID);
                    edIP = (EditText) v1.findViewById(R.id.ed_ipAddress);
                    edPort = (EditText) v1.findViewById(R.id.ed_port);
                    btConnect = (Button) v1.findViewById(R.id.bt_connect);

                    if(settings.getString(Settings.IP_ADDRESS)!= null){
                        edIP.setText(settings.getString(Settings.IP_ADDRESS));
                    }
                    if(settings.getInt(Settings.PORT) != -1){
                        edPort.setText(Integer.toString(settings.getInt(Settings.PORT)));
                    }
                    if(connected){
                        connectStatusChange(true);
                    }
                    btConnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!connected){
                                String ip = edIP.getText().toString();
                                int port = Integer.valueOf(edPort.getText().toString());
                                clientSocket = new ClientSocket(ip, port);
                                clientSocket.setServerListener(MainActivity.this);
                                btConnect.setText("Connecting...");
                                clientSocket.connect();
                                settings.putString(Settings.IP_ADDRESS, ip);
                                settings.putInt(Settings.PORT, port);
                            }else{
                                clientSocket.disconnect();
                            }
                        }
                    });
                    return v1;
                case 2:
                    View v2 = inflater.inflate(R.layout.fragment_chat, container, false);
                    rcConversation = (RecyclerView) v2.findViewById(R.id.rc_conversation);
                    rcConversation.setHasFixedSize(true);
                    LinearLayoutManager m2LayoutManager = new LinearLayoutManager(getContext());
                    m2LayoutManager.setStackFromEnd(true);
                    rcConversation.setLayoutManager(m2LayoutManager);
                    rcConversation.setAdapter(messengeListAdapter);
                    final EditText edInputMessenge = (EditText) v2.findViewById(R.id.ed_input_messenge);
                    Button btSend = (Button) v2.findViewById(R.id.bt_send_messenge);
                    btSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String messenge = edInputMessenge.getText().toString();
                            edInputMessenge.getText().clear();
                            clientSocket.sendMessenge(messenge, messengeListAdapter);
                            rcConversation.scrollToPosition(messengeListAdapter.getItemCount() - 1);
                        }
                    });
                    return v2;
                case 3:
                    View v3 = inflater.inflate(R.layout.fragment_device_control, container, false);
                    rcListDevice = (RecyclerView) v3.findViewById(R.id.rc_list_device);
                    rcListDevice.setHasFixedSize(true);
                    rcListDevice.setLayoutManager(new LinearLayoutManager(getContext()));
                    rcListDevice.setAdapter(deviceListAdapter);
                    return v3;
                case 4:
                    return null;
            }
            return null;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new PlaceholderFragment(position + 1);
        }


        @Override
        public int getCount() {
            return connected?4:1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CONNECT";
                case 1:
                    return "CHAT";
                case 2:
                    return "DEVICE CONTROL";
                case 3:
                    return "JOYSTICK";
            }
            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.connected){
            clientSocket.disconnect();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!connected){
            clientSocket.connect();
        }
    }
}
