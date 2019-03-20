package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecentAdapter extends ArrayAdapter {
    private Context context;
    private int layout;
    private List<Ordered> orderedList;

    public RecentAdapter(@NonNull Context context, int layout, @NonNull List<Ordered> orderedList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recent,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvFoodname = (TextView)convertView.findViewById(R.id.tv_foodname);
            viewHolder.tvAmount = (TextView)convertView.findViewById(R.id.tv_amount);
            viewHolder.tvTotal = (TextView)convertView.findViewById(R.id.tv_total);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Ordered ordered = orderedList.get(position);
        viewHolder.tvFoodname.setText(ordered.getFoodname());
        viewHolder.tvAmount.setText(ordered.getAmount());
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(ordered.getTotal())));

        return convertView;
    }

    private class ViewHolder{
        private TextView tvFoodname, tvAmount, tvTotal;
    }
}
