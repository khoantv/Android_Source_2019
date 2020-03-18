package kmt.jimmy.demoesp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ClientSocket.ServerListener{

    EditText edtIP, edtPort, edtRequest;
    WebView wb;
    String ip, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
        if(connected){
            connectStatusChange(true);
        }
    }

    private void InitView() {
        edtIP = (EditText) findViewById(R.id.edtIp);
        edtPort = (EditText) findViewById(R.id.edtPort);
        edtRequest = (EditText) findViewById(R.id.edtRequest);
        wb = (WebView) findViewById(R.id.wbESP);
    }

    public void btnConnect_Click(View v)
    {
        //region -- cách 1
//        wb.getSettings().setJavaScriptEnabled(true);
//        ip = edtIP.getText().toString();
//        port = edtPort.getText().toString();
//        wb.loadUrl(ip+":"+port);
        //endregion

        if(!connected){
            String ip = edtIP.getText().toString();
            int port = Integer.valueOf(edtPort.getText().toString());
            clientSocket = new ClientSocket(ip, port);
            clientSocket.setServerListener(MainActivity.this);
            ((Button)findViewById(R.id.btnConnect)).setText("CONNECTING...");
            clientSocket.connect();
        }else{
            clientSocket.disconnect();
            ((Button)findViewById(R.id.btnConnect)).setText("CONNECT");
        }
    }

    public void btnSend_Click(View v)
    {
        //region Cách 1:
        String request = edtRequest.getText().toString();
        //wb.loadUrl(ip+":"+port);
        //endregion
        try {
            if(connected)
            {
                clientSocket.sendMessenge(request);
                edtRequest.setText("");
            }
            else
            {
                Toast.makeText(MainActivity.this, "You are not connect!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this, "Ex: "+ ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //region -- Cách 2 --
    private ClientSocket clientSocket;
    private boolean connected = false;

    @Override
    public void connectStatusChange(boolean status) {
        this.connected = status;
        ((Button)findViewById(R.id.btnConnect)).setText(status ? "DISCONNECT" : "CONNECT");
        this.edtIP.setEnabled(!status);
        this.edtPort.setEnabled(!status);
    }

    @Override
    public void newMessengeFromServer(String messenge) {
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
            if (clientSocket!= null)
                clientSocket.connect();
        }
    }
    //endregion
}
