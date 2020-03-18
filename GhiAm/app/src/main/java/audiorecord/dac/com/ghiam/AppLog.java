package audiorecord.dac.com.ghiam;

import android.util.Log;

/**
 * Created by KhiemDauTo on 11/11/2016.
 */

public class AppLog {
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}
