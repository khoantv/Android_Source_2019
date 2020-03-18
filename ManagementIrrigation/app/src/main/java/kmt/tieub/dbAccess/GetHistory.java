package kmt.tieub.dbAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

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
 * Created by tieub on 04/08/2017.
 */

public class GetHistory extends AsyncTask<String, Void, String> {

    private Exception exception;
    private Context mContext;

    public GetHistory(Context mContext)
    {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            if(!CommonCls.isOnline())
            {
                return "";
            }
            return performPostCall(urls[0]);
        } catch (Exception e) {
            this.exception = e;
            return e.toString();
        }
    }

    protected void onPostExecute(String result) {
        System.out.println("Result: "+ result);
        if (result.equals(""))
        {
            Toast.makeText(mContext, "Your Network Is Off", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            HistoryData historyData = new Gson().fromJson(result, HistoryData.class);

            if(historyData != null)
            {
                System.out.println("time: "+historyData.getTimeIrrgation());
                int time = 0;
                time = (int) historyData.getTimeIrrgation()/1;

                String humidity = historyData.getValueHumidity() +"";
                String temperature = historyData.getValueTemperature()+"";
                String level = historyData.getWaterLevel()+"";
                String power = historyData.getPower()+"";

                if(MainActivity.getMainActivity() != null)
                {
                    MainActivity.getMainActivity().setValueChange(historyData);
                }
                else
                {
                    Log.d("SMSReceiver", "MainActivity is null");
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            HistoryData historyData = new HistoryData();
            historyData.setDeviceId(0);
            historyData.setValueHumidity(0);
            historyData.setValueTemperature(0);
            historyData.setWaterLevel(0);
            historyData.setPower(0);
            MainActivity.getMainActivity().setValueChange(historyData);
        }
    }

    public String  performPostCall(String deviceId) {

        HttpURLConnection conn = null;
        URL url;
        String response = "";
        try {
            String requestURL = "http://ais.tnut.mobifonemap.com/Android/getCurentHistoryData";
            url = new URL(requestURL);

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("deviceId", deviceId);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
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
                    System.out.println("response: "+ response.toString());
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
