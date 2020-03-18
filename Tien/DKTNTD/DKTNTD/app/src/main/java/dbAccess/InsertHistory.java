package dbAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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

    protected String doInBackground(String... urls) {
        try {
            return performPostCall(urls[0]);
        } catch (Exception e) {
            this.exception = e;
            return e.toString();
        }
    }

    public String  performPostCall(String jSon) {

        HttpURLConnection conn = null;
        URL url;
        String response = "";
        try {
            //String requestURL = "http://mobifonemap.com/Home/getMessage";
            //String requestURL = "http://210.2.86.17/plesk-site-preview/mobifonemap.com/210.2.86.17/Home/getMessage";
            String requestURL = "http://www.mobifonemap.com/Home/getMessage";
            url = new URL(requestURL);

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("JsonText", jSon);

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
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                InputStreamReader inputStream = null;
                try {
                    inputStream = new InputStreamReader(conn.getInputStream());
                    BufferedReader br=new BufferedReader(inputStream);
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
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
                response += "NOT OK. \n";
            }

        } catch (Exception e) {
            e.printStackTrace();
            response += "Error when get response: \n" + e.toString();
        }
        finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

    protected void onPostExecute(String result) {
        System.out.println("Result: "+ result);
        Toast.makeText(mContext, result , Toast.LENGTH_LONG).show();
    }

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
}