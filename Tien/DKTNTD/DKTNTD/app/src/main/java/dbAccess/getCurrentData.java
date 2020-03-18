package dbAccess;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nguye.dktntd.LoginActivity;
import com.example.nguye.dktntd.MainActivity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tieub on 03/08/2017.
 */

public class getCurrentData extends AsyncTask<String, Void, HistoryData> {

    String z = "";
    Boolean isSuccess = false;
    Context mContext;
    Connection con;

    @Override
    protected HistoryData doInBackground(String... params) {
        // TODO: attempt authentication against a network service.
        int deviceId;
        HistoryData data = null;
        try {
            // Simulate network access.
            deviceId = Integer.parseInt(params[0].trim());

            con = jdbcClass.getConnection();
            //Thread.sleep(2000);
            if (con == null)
            {
                z = "Check Your Connection!";
            }
            else
            {
                z = "";
                System.out.println("Connected!");
                //String query = String.format("CHECK_ACCOUNT '%s', '%s'", userNam, password);
                //String query = ("select 1 FROM  dbo.[Account] WHERE dbo.[Account].UserName = 'khoan' AND dbo.[Account].[Password] = '12345'");
                //System.out.println("query: "+ query);
                //Statement stmt = con.createStatement();
                //ResultSet rs = stmt.executeQuery(query);
                //String SPsql = "EXEC CHECK_ACCOUNT ?,?";   // for stored proc taking 2 parameters
                //PreparedStatement ps = con.prepareStatement(SPsql);
//                    PreparedStatement ps = con.prepareCall("{call CHECK_ACCOUNT @UserName=?,@Password=?}");
//                    ResultSet rs;
//                    ps.setEscapeProcessing(true);
//                    ps.setQueryTimeout(10000);
//                    ps.setString(1, userNam);
//                    ps.setString(2, password);
//                    rs = ps.executeQuery();

                ResultSet rs;
                CallableStatement proc = con.prepareCall("EXEC GetCurrentData ?");
                // Register Input Parameters
                proc.setInt("@DeviceId", deviceId);
                // Execute the stored procedure
                rs = proc.executeQuery();

                z ="";
                String valueHumidity, valueTemperature, waterLevel, power;
                if (rs.wasNull()==false)
                {
                    isSuccess = true;
                    while (rs.next()) {
                        data = new HistoryData();
                        data.setValueHumidity(Float.parseFloat(rs.getString("ValueHumidity")));
                        data.setValueTemperature(Float.parseFloat(rs.getString("ValueTemperature")));
                        data.setWaterLevel(Float.parseFloat(rs.getString("WaterLevel")));
                        data.setPower(Float.parseFloat(rs.getString("Power")));
                    }
                    rs.close();
                }
                else
                {
                    z = "";
                    isSuccess = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            z = "";
            isSuccess = false;
        }
        return  data;
    }

    @Override
    protected void onPostExecute(final HistoryData data) {
        if (data != null)
        {
             //Gọi hàm set giá trị
            if (MainActivity.getMainActivity() != null)
            {
                MainActivity.getMainActivity().setValueChange(data.getValueHumidity()+"", data.getValueTemperature()+"", data.getWaterLevel()+"", data.getPower()+"");
            }
            else
            {
                Log.d("SMSReceiver", "MainActivity is null");
            }
        }
    }

    @Override
    protected void onCancelled() {

    }
}