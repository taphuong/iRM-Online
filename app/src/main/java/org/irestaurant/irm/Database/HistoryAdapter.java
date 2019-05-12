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
    private List<History> historyList;

    public HistoryAdapter(@NonNull Context context, int layout, @NonNull List<History> historyList) {
        super(context, layout, historyList);
        this.context = context;
        this.layout = layout;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tv_date);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            viewHolder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            viewHolder.tvTotal = (TextView)convertView.findViewById(R.id.tv_total);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        History history = historyList.get(position);
        viewHolder.tvDate.setText(history.getDate());
        viewHolder.tvTime.setText(history.getTime());
        viewHolder.tvNumber.setText(history.getNumber());
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(history.getTotal())));

        return convertView;
    }

    private class ViewHolder{
        private TextView tvDate, tvTime, tvNumber, tvTotal;
    }
}
