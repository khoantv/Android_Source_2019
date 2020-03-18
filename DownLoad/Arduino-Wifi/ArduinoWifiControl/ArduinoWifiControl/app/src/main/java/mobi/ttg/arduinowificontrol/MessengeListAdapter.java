package mobi.ttg.arduinowificontrol;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by duong on 2/14/2016.
 */
public class MessengeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MessengeItem> listMessengeItem = new ArrayList<MessengeItem>();
    private Context context;

    public MessengeListAdapter(Context context, ArrayList<MessengeItem> listMessengeItem){
        this.context = context;
        this.listMessengeItem = listMessengeItem;
    }
    public void addMessenge(MessengeItem messengeItem){
        listMessengeItem.add(messengeItem);
        notifyItemInserted(getItemCount()-1);

    }

    public static class ViewHolderIn extends RecyclerView.ViewHolder {
        TextView tvMessenge;
        public ViewHolderIn(View v) {
            super(v);
            tvMessenge = (TextView) v.findViewById(R.id.tv_messenge);

        }
    }
    public static class ViewHolderOut extends RecyclerView.ViewHolder {
        TextView tvMessenge;
        public ViewHolderOut(View v) {
            super(v);
            tvMessenge = (TextView) v.findViewById(R.id.tv_messenge);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v;
        if(viewType == MessengeItem.TYPE_IN){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messenge_item_in, parent, false);
            vh = new ViewHolderIn(v);
        }else if(viewType == MessengeItem.TYPE_OUT){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messenge_item_out, parent, false);
            vh = new ViewHolderOut(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessengeItem messengeItem = listMessengeItem.get(position);
        if(holder instanceof ViewHolderIn){
            ((ViewHolderIn)holder).tvMessenge.setText(messengeItem.getBody());
        }else if(holder instanceof ViewHolderOut){
            ((ViewHolderOut)holder).tvMessenge.setText(messengeItem.getBody());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listMessengeItem.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return listMessengeItem.size();
    }

}
