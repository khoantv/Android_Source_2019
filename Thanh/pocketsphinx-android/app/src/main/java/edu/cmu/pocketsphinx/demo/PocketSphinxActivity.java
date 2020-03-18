/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    private static final String TAG = "Bluetooth:";
    Button btnClick;
    private Button btnTB_1, btnTB_2, btnTB_3, btnTB_4, btnTB_5, btnTB_6, btnTB_7, btnTB_8, btnTB_9, btnTB_10, btnTB_11, btnTB_12;
    private boolean isOnTB1,isOnTB2, isOnTB3, isOnTB4, isOnTB5, isOnTB6, isOnTB7, isOnTB8,isOnTB9, isOnTB10, isOnTB11, isOnTB12;
    TextView txtStatus;
    Handler handler;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:31:F6:18:02";
    private SpeechRecognizer recognizer;
    //private HashMap<String, Integer> captions;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");


        //region ---- Get Control
        getControl();
        //endregion

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                    // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                    // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);                // extract string
                            sb.delete(0, sb.length());                                        // and clear
                            txtStatus.setText("Data from Arduino: " + sbprint);            // update TextView
                        }
                        Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();        // get Bluetooth adapter
        checkBTState();

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(PocketSphinxActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Hãy nói: Tiến, lùi, trái, phải, dừng");
                    nhandang();
                }
            }
        }.execute();

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnClick.getText().toString()=="Bật Bluetooth"){
                    batBluetooth();
                    btnClick.setText("Tắt Bluetooth");
                }
                else {
                    tatBluetooth();
                    btnClick.setText("Bật Bluetooth");
                }
            }
        });

        //region ---- Set OnCheckChange
        btnTB_1.setOnClickListener(myChange);
        btnTB_2.setOnClickListener(myChange);
        btnTB_3.setOnClickListener(myChange);
        btnTB_4.setOnClickListener(myChange);
        btnTB_5.setOnClickListener(myChange);
        btnTB_6.setOnClickListener(myChange);
        btnTB_7.setOnClickListener(myChange);
        btnTB_8.setOnClickListener(myChange);
        btnTB_9.setOnClickListener(myChange);
        btnTB_10.setOnClickListener(myChange);
        btnTB_11.setOnClickListener(myChange);
        btnTB_12.setOnClickListener(myChange);
        //endregion

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

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            tatBluetooth();
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
                    Toast.makeText(PocketSphinxActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
                    break;
                }
                catch (Exception ex)
                {
                    Log.e("Error", ex.toString());
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
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
            catch (Exception ex)
            {
                Log.d(TAG, "...Error data send: " + ex.getMessage() + "...");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }
    
    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        try {
            ((TextView) findViewById(R.id.txtStatus))
                    .setText("noi j di :)");
            if (hypothesis != null) {
                String text = hypothesis.getHypstr();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                ((TextView) findViewById(R.id.txtStatus))
                        .setText(text);
                dieuKhienXeLan(text);
                // kiem tra ket qua va goi ham khac o day
                // VD;
                //if (text.equals("tien")){
                // lam gi do
                //}
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, "...Error data send: " + ex.getMessage() + "...");
        }
    }

    private void dieuKhienXeLan(String text) {
        ((TextView) findViewById(R.id.txtStatus)).setText(text);
        switch(text){
            case "tiến":
                tien();
                break;
            case "lùi_":
                dung();
                break;
            case "trái":
                lui();
                break;
            case "phải":
                phai();
                break;
            case "dừng":
                dung();
                break;
            case "BatBluetooth":
                batBluetooth();
                break;
            case "TatBluetooth":
                tatBluetooth();
                break;
        }
        //recognizer.startListening("dkxelan");
    }

    private void tatBluetooth(){
        btAdapter.disable();
    }

    private void batBluetooth(){
        btAdapter.enable();
    }

    private void tien(){
        mConnectedThread.write("1");
        Log.e(TAG, "...Up...", null);
    }
    private void lui(){
        mConnectedThread.write("4");
        Log.e(TAG, "...Up...", null);
    }
    private void trai(){
        mConnectedThread.write("2");
        Log.e(TAG, "...Up...", null);
    }
    private void phai(){
        mConnectedThread.write("3");
        Log.e(TAG, "...Up...", null);
    }
    private void dung(){
        mConnectedThread.write("0");
        Log.e(TAG, "...Up...", null);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        nhandang();
    }

    private void nhandang() {
        recognizer.stop();
        recognizer.startListening("dkxelan");
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        recognizer = defaultSetup()
                .setSampleRate(16000)
                .setAcousticModel(new File(assetsDir, "dkxelan"))
                .setDictionary(new File(assetsDir, "dkxelan.dict"))
                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)
                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-20f)
                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", false)
                .setBoolean("-allphone_cd", true)
                .getRecognizer();
        recognizer.addListener(this);
        File dkxlGrammar = new File(assetsDir, "dkxelan.gram");
        recognizer.addGrammarSearch("dkxelan", dkxlGrammar);
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        nhandang();
    }

    //region ---- Get Control By ID
    public void getControl()
    {
        btnClick= (Button)findViewById(R.id.btnClick);
        btnTB_1 = (Button) findViewById(R.id.btnTB1);
        btnTB_2 = (Button) findViewById(R.id.btnTB2);
        btnTB_3 = (Button) findViewById(R.id.btnTB3);
        btnTB_4 = (Button) findViewById(R.id.btnTB4);
        btnTB_5 = (Button) findViewById(R.id.btnTB5);
        btnTB_6 = (Button) findViewById(R.id.btnTB6);
        btnTB_7 = (Button) findViewById(R.id.btnTB7);
        btnTB_8 = (Button) findViewById(R.id.btnTB8);
        btnTB_9 = (Button) findViewById(R.id.btnTB9);
        btnTB_10 = (Button) findViewById(R.id.btnTB10);
        btnTB_11= (Button) findViewById(R.id.btnTB11);
        btnTB_12 = (Button) findViewById(R.id.btnTB12);
    }
    //endregion

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
        if(switchID == btnTB_1.getId())
        {
            isOnTB1 = !isOnTB1;
            checkStatus(switchID, 1, isOnTB1);
        }
        else if(switchID == btnTB_2.getId())
        {
            isOnTB2 = !isOnTB2;
            checkStatus(switchID, 2, isOnTB2);
        }
        else if(switchID == btnTB_3.getId())
        {
            isOnTB3 = !isOnTB3;
            checkStatus(switchID, 3, isOnTB3);
        }
        else if(switchID == btnTB_4.getId())
        {
            isOnTB4 = !isOnTB4;
            checkStatus(switchID, 4, isOnTB4);
        }
        else if(switchID == btnTB_5.getId())
        {
            isOnTB5 = !isOnTB5;
            checkStatus(switchID, 5, isOnTB5);
        }
        else if(switchID == btnTB_6.getId())
        {
            isOnTB6 = !isOnTB6;
            checkStatus(switchID, 6, isOnTB6);
        }
        else if(switchID == btnTB_7.getId())
        {
            isOnTB7 = !isOnTB7;
            checkStatus(switchID, 7, isOnTB7);
        }
        else if(switchID == btnTB_8.getId())
        {
            isOnTB8 = !isOnTB8;
            checkStatus(switchID, 8, isOnTB8);
        }
        else if(switchID == btnTB_9.getId())
        {
            isOnTB9 = !isOnTB9;
            checkStatus(switchID, 9, isOnTB9);
        }
        else if(switchID == btnTB_10.getId())
        {
            isOnTB10 = !isOnTB10;
            checkStatus(switchID, 10, isOnTB10);
        }
        else if(switchID == btnTB_11.getId())
        {
            isOnTB11 = !isOnTB11;
            checkStatus(switchID, 11, isOnTB11);
        }
        else if(switchID == btnTB_12.getId())
        {
            isOnTB12 = !isOnTB12;
            checkStatus(switchID, 12, isOnTB12);
        }
    }

    private String checkStatus(int id, Integer index ,boolean isChecked)
    {
        Button btnStatus = (Button) findViewById(id);
        if(isChecked)
        {
            // Send message from here to ON
            btnStatus.setBackgroundResource(R.drawable.on_48);
            Log.i("MainActivity_Led", "Led "+ index +" ON");
        }
        else {
            // Send message from here to OFF
            btnStatus.setBackgroundResource(R.drawable.off_48);
            Log.i("MainActivity_Led", "Led "+ index +" OFF");
        }
        return "";
    }
    //endregion
}
