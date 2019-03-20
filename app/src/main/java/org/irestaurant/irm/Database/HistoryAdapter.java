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

public class HistoryAdapter extends ArrayAdapter {
    private Context context;
    private int layout;
    private List<Revenue> revenueList;

    public HistoryAdapter(@NonNull Context context, int layout, @NonNull List<Revenue> revenueList) {
        super(context, layout, revenueList);
        this.context = context;
        this.layout = layout;
        this.revenueList = revenueList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            viewHolder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            viewHolder.tvTotal = (TextView)convertView.findViewById(R.id.tv_total);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Revenue revenue = revenueList.get(position);
        viewHolder.tvTime.setText(revenue.getTime());
        viewHolder.tvNumber.setText(revenue.getNumber());
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(revenue.getTotalat())));

        return convertView;
    }

    private class ViewHolder{
        private TextView tvTime, tvNumber, tvTotal;
    }
}
