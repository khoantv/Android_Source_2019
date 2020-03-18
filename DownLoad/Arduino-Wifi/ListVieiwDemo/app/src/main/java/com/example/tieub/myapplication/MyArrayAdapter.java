package com.example.tieub.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tieub on 06/11/2017.
 */

public class MyArrayAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Students> ls;


    public MyArrayAdapter(@NonNull Context context, int resource, ArrayList<Students> ls) {
        super(context, resource);
        this.context = context;
        this.ls = ls;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout, parent, false);

        //chỉ là test thôi, bạn có thể bỏ If đi
        if(ls.size()>0 && position>=0)
        {
            TextView txtId = (TextView) rowView.findViewById(R.id.txtId);
            TextView txtName = (TextView) rowView.findViewById(R.id.txtName);

            txtId.setText(ls.get(position).getId());
            txtName.setText(ls.get(position).getName());
        }
        return rowView;
    }
}
