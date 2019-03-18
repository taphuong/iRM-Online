package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.irestaurant.irm.R;

import java.util.List;

public class OredredAdapter extends ArrayAdapter {
    private Context context;
    private int layout;
    private List<Ordered> orderedList;

    public OredredAdapter(@NonNull Context context, int layout,@NonNull List<Ordered> orderedList) {
        super(context, layout, orderedList);
        this.context = context;
        this.layout = layout;
        this.orderedList = orderedList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvFoodname = (TextView)convertView.findViewById(R.id.tv_foodname);
            viewHolder.tvAmount = (TextView)convertView.findViewById(R.id.tv_amount);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Ordered ordered = orderedList.get(position);
        viewHolder.tvFoodname.setText(ordered.getFoodname());
        viewHolder.tvAmount.setText(ordered.getAmount());

        return convertView;
    }

    private class ViewHolder{
        private TextView tvFoodname, tvAmount;
    }

}
