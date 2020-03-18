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
 * Created by tieub on 04/08/2017.
 */

public class InsertHistory extends AsyncTask<String, Void, String> {

    private Exception exception;
    private Context mContext;

    public InsertHistory(Context mContext)
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
            Toast.makeText(mContext, "Cannot put data.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
//            HistoryData historyData = new Gson().fromJson(result, HistoryData.class);
//            if(historyData != null)
//            {
//                String humidity = historyData.getValueHumidity() +"";
//                String temperature = historyData.getValueTemperature()+"";
//                String level = historyData.getWaterLevel()+"";
//                String power = historyData.getPower()+"";
//                int time = Integer.parseInt(historyData.getTimeIrrgation()+"");
//                MainActivity.getMainActivity().setValueChange(time, humidity, temperature, level, power);
//            }

            JSONObject jsonObject = new JSONObject(result);
            HistoryData historyData = new HistoryData();
            String humidity = jsonObject.get("ValueHumidity").toString();
            String temperature = jsonObject.get("ValueTemperature").toString();
            String level = jsonObject.get("WaterLevel").toString();
            String power = jsonObject.get("Power").toString();
            int time = Integer.parseInt(jsonObject.get("TimeIrrgation").toString());
            int deviceId = Integer.parseInt(jsonObject.get("DeviceId").toString());
            historyData.setDeviceId(deviceId);
            historyData.setValueHumidity(Float.parseFloat(humidity));
            historyData.setValueTemperature(Float.parseFloat(temperature));
            historyData.setWaterLevel(Float.parseFloat(level));
            historyData.setPower(Float.parseFloat(power));
            MainActivity.getMainActivity().setValueChange(historyData);
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

    public String  performPostCall(String jsonHistory) {

        HttpURLConnection conn = null;
        URL url;
        String response = "";
        try {
            String requestURL = "http://ais.tnut.mobifonemap.com/Android/insertData";
            url = new URL(requestURL);

            HistoryData historyData = new Gson().fromJson(jsonHistory, HistoryData.class);

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("deviceId", historyData.getDeviceId()+"");
            postDataParams.put("valueHumidity", historyData.getValueHumidity()+"");
            postDataParams.put("valueTemperature", historyData.getValueTemperature()+"");

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
