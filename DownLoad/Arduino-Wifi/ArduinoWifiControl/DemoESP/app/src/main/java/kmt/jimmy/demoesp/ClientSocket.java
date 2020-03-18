package kmt.jimmy.demoesp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by tieub on 26/10/2017.
 */

public class ClientSocket{
    public String ipAddress;
    public int port;
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

    public void sendMessenge(String messenge){
        if(socket.isConnected()){
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
            serverListener.newMessengeFromServer(values[0]);
        }
    }
    public void setServerListener(ServerListener serverListener){
        this.serverListener = serverListener;
    }
    public interface ServerListener{
        void connectStatusChange(boolean status);
        void newMessengeFromServer(String messenge);
    }
}

