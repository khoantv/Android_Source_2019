package vst.jimmy.db;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by JImmy on 26/12/2017.
 */

public class NetworkUtil {

    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }
}

