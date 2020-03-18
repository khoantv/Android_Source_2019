package com.example.kamran.login;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by tieub on 18/07/2017.
 */

public class CheckLogin extends AsyncTask<String,String,String>
{
    String z = "";
    Boolean isSuccess = false;
    Context mContext;
    Connection con;

    public CheckLogin(Context mContext)
    {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute()
    {
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String r)
    {
        //progressBar.setVisibility(View.GONE);
        Toast.makeText(mContext, r, Toast.LENGTH_SHORT).show();
        if(isSuccess)
        {
            Toast.makeText(mContext , "Login Successfull" , Toast.LENGTH_LONG).show();
            //finish();
        }
    }
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            con = jdbcClass.getConnection();
            if (con == null)
            {
                z = "Check Your Connection!";
            }
            else
            {
                System.out.println("Connected!");
                String query = "select * from Manager";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                z ="";
                if (isSuccess=true)
                {
                    while (rs.next()) {
                        System.out.println(rs.getString("Name") + ", " + rs.getString("PhoneNUmber"));
                        z += rs.getString("Name") + ", " + rs.getString("PhoneNumber") + "\n";
                    }
                    rs.close();
                }
                else
                {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = ex.getMessage();
        }
        return z;
    }
}
