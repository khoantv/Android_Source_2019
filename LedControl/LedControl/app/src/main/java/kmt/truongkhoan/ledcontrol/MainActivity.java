package kmt.truongkhoan.ledcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    //region ---- Variables.
    private Button btnSendX, btnSendY, btnInX, btnInY, btnDecX, btnDecY;
    SeekBar seekBarX, seekBarY;
    private EditText edtX, edtY, edtXChange, edtYChange, edtHistoryX, edtHistoryY;
    private Button btnPhone, btnReset;
    private int setStatus = 0;
    private SharedPreference sharedPreference;
    private TableRow tbrButton;
    //region Bluetooth
    Handler handler;
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:33:80:87:01";
    //endregion
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //region ---- Get Control
        getControl();
        //endregion

        //region ---- GetPhone
        //phoneNo = LoadPreferences();
        sharedPreference = new SharedPreference();
        address = sharedPreference.getValue(getApplicationContext());
        if(address == null || address.length() <= 0)
        {
            startActivity(new Intent(MainActivity.this, SetMacActivity.class));
        }
        //endregion

        //region .Bluetooth.
        btAdapter = BluetoothAdapter.getDefaultAdapter();        // get Bluetooth adapter
        checkBTState();
        //endregion


        //region ---- Call setPhone Activity
        btnPhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SetMacActivity.class));
            }
        });
        //endregion

        //region ---- Set Button Event
        btnSendX.setOnClickListener(myChange);
        btnSendY.setOnClickListener(myChange);
        btnDecY.setOnClickListener(myChange);
        btnDecX.setOnClickListener(myChange);
        btnInY.setOnClickListener(myChange);
        btnInX.setOnClickListener(myChange);
        //endregion

        //region -- EditText Change Listener
        edtX.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int x =  getNumber(edtX.getText().toString());
                x = checkNumber(x);
                seekBarX.setProgress(x);
            }
        });

        edtY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int y =  getNumber(edtY.getText().toString());
                y = checkNumber(y);
                seekBarY.setProgress(y);
            }
        });
        //endregion

        //region -- SeekBar Change
        seekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int x = seekBarX.getProgress();
                edtX.setText(x+"");
            }
        });

        seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int y = seekBarY.getProgress();
                edtY.setText(y+"");
            }
        });
        //endregion

        edtX.setText("0");
        edtY.setText("0");

        edtHistoryX.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                edtHistoryX.setText("");
                return false;
            }
        });

        edtHistoryY.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                edtHistoryY.setText("");
                return false;
            }
        });

        btnSendX.requestFocus();
    }

    public void getListDevice(View view)
    {
        startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
    }

    //region ---- Switch Change progress
     View.OnClickListener myChange = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int switchID = v.getId();
            progress(switchID);
        }
    };

    private void progress(int switchID)
    {
        int changeX =  getNumber(edtXChange.getText().toString());
        int changeY =  getNumber(edtXChange.getText().toString());
        int x =  getNumber(edtX.getText().toString());
        int y =  getNumber(edtY.getText().toString());

        if(switchID == btnInX.getId())
        {
            x += changeX;
            x =  checkNumber(x);
            edtX.setText(x+"");
        }
        else if(switchID == btnInY.getId())
        {
            y += changeY;
            y =  checkNumber(y);
            edtY.setText(y+"");
        }
        else if(switchID == btnDecX.getId())
        {
            x -= changeX;
            x =  checkNumber(x);
            edtX.setText(x+"");
        }
        else if(switchID == btnDecY.getId())
        {
            y -= changeY;
            y =  checkNumber(y);
            edtY.setText(y+"");
        }
        else if(switchID == btnSendX.getId())
        {
            String command = String.format(":X%d.",x);
            sentData(command);
            edtHistoryX.setText(edtHistoryX.getText().toString().concat(" \n "+ x));
            int textLength = edtHistoryX.getText().length();
            edtHistoryX.setSelection(textLength, textLength);
        }
        else if(switchID == btnSendY.getId())
        {
            String command = String.format(":Y%d.",y);
            sentData(command);
            edtHistoryY.setText(edtHistoryY.getText().toString().concat(" \n "+ y));
            int textLength = edtHistoryY.getText().length();
            edtHistoryY.setSelection(textLength, textLength);
        }
    }

    private int getNumber(String num)
    {
        if (num.isEmpty() || num.trim().length() == 0)
            return 0;
        else
            return Integer.parseInt(num);
    }
    private int checkNumber(int num)
    {
        if (num > 360)
            return 360;
        else if (num < -360)
            return -360;
        return num;
    }
    //endregion

    //region ---- Get Control By ID
    public void getControl()
    {
        btnDecX = (Button) findViewById(R.id.btnDecX);
        btnDecY = (Button) findViewById(R.id.btnDecY);
        btnInX = (Button) findViewById(R.id.btnInX);
        btnInY = (Button) findViewById(R.id.btnInY);
        btnSendX = (Button) findViewById(R.id.btnSendX);
        btnSendY = (Button) findViewById(R.id.btnSendY);
        edtX = (EditText) findViewById(R.id.edtX);
        edtXChange = (EditText) findViewById(R.id.edtXChange);
        edtY = (EditText) findViewById(R.id.edtY);
        edtYChange = (EditText) findViewById(R.id.edtYChange);
        edtHistoryX = (EditText) findViewById(R.id.edtHistoryX);
        edtHistoryY = (EditText) findViewById(R.id.edtHistoryY);

        btnPhone = (Button) findViewById(R.id.btnPhone);

        seekBarX = (SeekBar) findViewById(R.id.seekBarX);
        seekBarY = (SeekBar) findViewById(R.id.seekBarY);
    }
    //endregion

    //region ---- Send SMS
    public void sentData(String sms)
    {
        try {
            mConnectedThread.write(sms);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sent faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            Log.i("SENT_DATA", "Sent faild, please try again later!");
            e.printStackTrace();
        }
    }
    //endregion

    //region . Bluetooth .
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if(btAdapter.isEnabled())
            {
                ((Button) findViewById(R.id.btnOnOff)).setBackgroundResource(R.drawable.bluetooth_on);
            }
            else {
                //((Button) findViewById(R.id.btnOnOff)).setBackgroundResource(R.drawable.bluetooth_off);
                batBluetooth();
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();        // Send to message queue Handler
                } catch (IOException e) {
                    Log.e("Error", e.toString());
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
                Log.i("SENT_DATA", "Send!");
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private void tatBluetooth(){
        ((Button) findViewById(R.id.btnOnOff)).setBackgroundResource(R.drawable.bluetooth_off);
        btAdapter.disable();
    }

    private void batBluetooth(){
        ((Button) findViewById(R.id.btnOnOff)).setBackgroundResource(R.drawable.bluetooth_on);
        btAdapter.enable();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "...onResume - try connect...");

        address = sharedPreference.getValue(getApplicationContext()); // Cập nhật lại địa chỉ MAC.
        if(address != null && address.length() > 0)
        {
            BluetoothDevice device = null;
            try {
                device = btAdapter.getRemoteDevice(address);
            }
            catch (Exception ex)
            {
                Toast.makeText(getBaseContext(), "Kiểm tra lại địa chỉ MAC.", Toast.LENGTH_LONG).show();
            }
            // Set up a pointer to the remote node using it's address.


            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                if (device != null)
                {
                    btSocket = createBluetoothSocket(device);
                    // Discovery is resource intensive.  Make sure it isn't going on
                    // when you attempt to connect and pass your message.
                    btAdapter.cancelDiscovery();

                    // Establish the connection.  This will block until it connects.
                    Log.d(TAG, "...Connecting...");
                    try {
                        btSocket.connect();
                        Log.d(TAG, "....Connection ok...");
                        Toast.makeText(getBaseContext(), "....Connection ok...", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        try {
                            btSocket.close();
                        } catch (IOException e2) {
                            errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }

                    // Create a data stream so we can talk to server.
                    Log.d(TAG, "...Create Socket...");

                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                }
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "...In onPause()...");
        try {
            btSocket.close();
            Toast.makeText(getBaseContext(), "....Connection close....", Toast.LENGTH_LONG).show();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
        catch (Exception e)
        {

        }
    }

    public void onoffBluetooth(View v) {
        if(!btAdapter.isEnabled())
        {
            batBluetooth();
        }
        else {
            tatBluetooth();
        }
    }

    //endregion

    //region ---- Exit
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getString(R.string.warn));
        alert.setIcon(R.drawable.warning);
        alert.setMessage(getString(R.string.warnInfor));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }
    //endregion

    //region ---- menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.mnTSetMac:
                startActivity(new Intent(MainActivity.this, SetMacActivity.class));
                break;
            case R.id.mnOnOff:
                if(!btAdapter.isEnabled())
                {
                    batBluetooth();
                }
                else {
                    tatBluetooth();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}
