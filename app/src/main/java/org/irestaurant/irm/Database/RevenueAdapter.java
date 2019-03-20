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

public class RevenueAdapter extends ArrayAdapter {
    private Context context;
    private int layout;
    private List<Revenue> revenueList;

    public RevenueAdapter(@NonNull Context context, int layout, @NonNull List<Revenue> revenueList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_revenue,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            viewHolder.tvDiscount = (TextView)convertView.findViewById(R.id.tv_discount);
            viewHolder.tvTotal = (TextView)convertView.findViewById(R.id.tv_total);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Revenue revenue = revenueList.get(position);
        viewHolder.tvNumber.setText(revenue.getNumber());
        viewHolder.tvDate.setText(revenue.getDate());
        viewHolder.tvTime.setText(revenue.getTime());
        if (revenue.getDiscount().equals("0")){viewHolder.tvDiscount.setText("");}
        else {viewHolder.tvDiscount.setText("CK "+revenue.getDiscount()+"%");}
        viewHolder.tvDiscount.setText(revenue.getDate());
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(revenue.getTotalat())));
        return convertView;
    }

    private class ViewHolder{
        private TextView tvNumber, tvDate, tvTime, tvDiscount, tvTotal;
    }
}
