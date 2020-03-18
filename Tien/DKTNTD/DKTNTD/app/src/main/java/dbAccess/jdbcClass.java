package dbAccess;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by tieub on 03/08/2017.
 */

public class jdbcClass {
    Connection con;
    String un,pass,db,ip;
    static String user, password, database, server;

    public static Connection getConnection()
    {
        server = "171.244.8.187";
        database = "ManagementIrrigation";
        user = "khoan";
        password = "55555";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server +"; database= "+ database + ";user=" + user+ ";password=" + password + ";";
            System.out.print(ConnectionURL);
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}