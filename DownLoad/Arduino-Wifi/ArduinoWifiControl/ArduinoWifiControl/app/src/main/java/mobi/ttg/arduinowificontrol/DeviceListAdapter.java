package mobi.ttg.arduinowificontrol;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by duong on 2/14/2016.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private ArrayList<DeviceItem> listDeviceItem = new ArrayList<DeviceItem>();
    private Context context;
    private Database database;

    public DeviceListAdapter(Context context, Database database){
        this.context = context;
        this.database = database;
        listDeviceItem = this.database.getAllDevices();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDes;
        ToggleButton tgToggle;
        CardView cardView;
        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_device_title);
            tvDes = (TextView) v.findViewById(R.id.tv_device_des);
            tgToggle = (ToggleButton) v.findViewById(R.id.tg_toggle);
            cardView = (CardView) v.findViewById(R.id.cv_device_item);
        }
    }
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DeviceItem deviceItem = listDeviceItem.get(position);
        holder.tvName.setText(deviceItem.getName());
        holder.tvDes.setText(deviceItem.getDes());
        holder.tgToggle.setChecked(deviceItem.isOn());
        holder.tgToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, deviceItem.getCommand()+" id: "+deviceItem.getId(), Toast.LENGTH_SHORT).show();
                deviceItem.setOn(isChecked);
                if(isChecked){
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#BBDEFB"));
                }else{
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#edefef"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDeviceItem.size();
    }

}
