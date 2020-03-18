package vst.jimmy.socket;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import vst.jimmy.activity.R;
import vst.jimmy.db.SharepredPreferencesCls;

/**
 * Created by Jimmy on 09/02/2018.
 */

public class CurrentSession extends Application{

    private static String TAG = "CURRENT_SESSION";
    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "port";
    public static ClientSocket clientSocket;
    public static boolean isConnected = false;

    private static SharepredPreferencesCls sharepredPreferencesCls;

    public static void reConnect(Context mContext)
    {
        sharepredPreferencesCls = new SharepredPreferencesCls(mContext);
        if (CurrentSession.clientSocket == null)
        {
            String _ip = sharepredPreferencesCls.getString(IP_ADDRESS);
            String _port = sharepredPreferencesCls.getString(PORT);
            if(_ip.isEmpty() || _port.isEmpty())
                return;
            CurrentSession.clientSocket = new ClientSocket(_ip, Integer.parseInt(_port));
        }

        CurrentSession.clientSocket.setServerListener((ClientSocket.ServerListener) mContext);
        if (isConnected == false)
            CurrentSession.clientSocket.connect();
    }

    public static void closeConnection()
    {
        if (CurrentSession.clientSocket != null && isConnected == false)
        {
            CurrentSession.clientSocket.disconnect();
        }
    }

    public static void GetSocketStatus(String TAG)
    {
        if (CurrentSession.clientSocket == null) {
            Log.d(TAG, "CurrentSession.clientSocket: NULL");
            return;
        }
        else
        {
            Log.d(TAG, "CurrentSession.clientSocket: NOT NULL");
        }
        if (CurrentSession.clientSocket.socket == null) {
            Log.d(TAG, "CurrentSession.clientSocket.socket: NULL");
            return;
        }
        else
        {
            Log.d(TAG, "CurrentSession.clientSocket.socket: NOT NULL");
        }
        if (CurrentSession.clientSocket.socket.isClosed()) {
            Log.d(TAG, "CurrentSession.clientSocket.socket: CLOSED");
        }
        else
        {
            Log.d(TAG, "CurrentSession.clientSocket.socket: NOT CLOSE");
        }
    }
}
