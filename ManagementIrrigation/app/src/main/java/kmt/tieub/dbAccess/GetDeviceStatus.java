package kmt.tieub.dbAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import kmt.tieub.managementirrigation.CommonCls;
import kmt.tieub.managementirrigation.MainActivity;

/**
 * Created by tieub on 06/08/2017.
 */

public class GetDeviceStatus extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private Context mContext;

    public GetDeviceStatus(Context mContext)
    {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {
            if(!CommonCls.isOnline())
            {
                return "";
            }
            return performPostCall();
        } catch (Exception e) {
            this.exception = e;
            return e.toString();
        }
    }

    protected void onPostExecute(String result) {
        //System.out.println("Result: "+ result);
        if (result.equals(""))
        {
            Toast.makeText(mContext, "Cannot get data.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            if(result != null)
            {
                JSONObject jsonObject = new JSONObject(result);
                Boolean tb1 = (Boolean) jsonObject.get("tb1");
                Boolean tb2 = (Boolean) jsonObject.get("tb2");
                Boolean tb3 = (Boolean) jsonObject.get("tb3");
                MainActivity.getMainActivity().setDeviceStatus(true,tb1,tb2, tb3);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            MainActivity.getMainActivity().setDeviceStatus(false,false, false, false);
        }
    }

    public String  performPostCall() {

        HttpURLConnection conn = null;
        URL url;
        String response = "";
        try {
            String requestURL = "http://ais.tnut.mobifonemap.com/Android/getDeviceStatus";
            url = new URL(requestURL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            int responseCode=conn.getResponseCode();

            String line = "";
            response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                InputStreamReader inputStream = null;
                try {
                    inputStream = new InputStreamReader(conn.getInputStream());
                    BufferedReader br=new BufferedReader(inputStream);
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    //System.out.println("response: "+ response.toString());
                }
                catch (Exception e)
                {
                    System.out.println("Error when get response: "+ e.toString());
                    response += "Error when get response: \n" + e.toString();
                }
                finally {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
            }
            else {
                response = "NOT OK. \n";
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = "Error when get response: \n" + e.toString();
        }
        finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

    //region -- Push Parameters --
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    //endregion
}
