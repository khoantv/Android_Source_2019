package vst.jimmy.socket;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import vst.jimmy.db.SharepredPreferencesCls;

/**
 * Created by tieub on 30/10/2017.
 */

public class ClientSocket{

    public String ipAddress;
    public int port;
    private static String TAG = "CURRENT_SESSION";
    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "port";
    private static SharepredPreferencesCls sharepredPreferencesCls;

    public Socket socket;
    public ServerListener serverListener;
    public BufferedWriter out;
    public BufferedReader in;

    public ClientSocket(String ip, int port){
        this.ipAddress = ip;
        this.port = port;
    }

    public void connect(){
        new ConnectToServerTask().execute();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {

        }
        serverListener.connectStatusChange(false);
    }

    public void reConnect(Context mContext)
    {
        if (socket != null && socket.isClosed() == true)
        {
            sharepredPreferencesCls = new SharepredPreferencesCls(mContext);
            String _ip = sharepredPreferencesCls.getString(IP_ADDRESS);
            String _port = sharepredPreferencesCls.getString(PORT);
            if(_ip.isEmpty() || _port.isEmpty())
                return;

            ipAddress = _ip ;
            port = Integer.parseInt(_port);

            new ConnectToServerTask().execute();
        }
    }

    public void sendMessenge(String messenge){
        if(socket != null && socket.isConnected()){
            try {
                out.write(messenge);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            serverListener.connectStatusChange(false);
        }
    }

    public void GetSocketStatus(String TAG)
    {
        if (socket == null) {
            Log.d(TAG, "socket = NULL");
            return;
        }
        else
        {
            Log.d(TAG, "socket NOT NULL");
        }
        if (socket.isClosed()) {
            Log.d(TAG, "socket: CLOSED");
        }
        else
        {
            Log.d(TAG, "socket: NOT CLOSE");
        }
    }

    public boolean isClosed()
    {
        if (socket==null) return  false;
        return socket.isClosed();
    }

    public class ConnectToServerTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                socket = new Socket(ipAddress, port);
                if(socket.isConnected()){
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean connnected) {
            if (serverListener!= null)
                 serverListener.connectStatusChange(connnected);
            if(connnected){
                new ServerListenner().execute();
            }
        }
    }
    public class ServerListenner extends AsyncTask<Void, String, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            String messenge = null;
            try {
                //region -- mới thêm để sửa lỗi đọc phản hồi 00:40 03/11--
                if (socket != null && socket.isClosed())
                {
                    new ConnectToServerTask().execute();
                }
                //endregion
                messenge = in.readLine();
                while (messenge != null){
                    publishProgress(messenge);
                    messenge = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            serverListener.MessengeFromServer(values[0]);
        }
    }
    public void setServerListener(ServerListener serverListener){
        this.serverListener = serverListener;
    }
    public interface ServerListener{
        void connectStatusChange(boolean status);
        void MessengeFromServer(String messenge);
    }

}

