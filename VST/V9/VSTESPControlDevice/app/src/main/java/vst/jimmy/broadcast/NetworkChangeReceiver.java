package vst.jimmy.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jimmy on 26/12/2017.
 */

public final class NetworkChangeReceiver extends BroadcastReceiver {

    private NetworkChangeResult mListener;

    @SuppressWarnings("unused")
    public NetworkChangeReceiver() {
    }

    public NetworkChangeReceiver(NetworkChangeResult mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //boolean isConnected = NetworkUtil.getConnectivityStatus(context);
        //if (mListener != null)
        //Log.d("NetworkChangeReceiver", "isConnected: "+ isConnected);
        //mListener.checkInternetConnection(isConnected);
//        else
//        {
//            Log.d("NetworkChangeReceiver", "mListener: null");
//        }
    }
}