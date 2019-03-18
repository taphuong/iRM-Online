package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.irestaurant.irm.R;

import java.util.List;

public class NumberAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private List<Number> numberList;

    public NumberAdapter(@NonNull Context context, int resource, @NonNull List<Number> numberList) {
        super(context, resource, numberList);
        this.context = context;
        this.resource = resource;
        this.numberList = numberList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            viewHolder.tvTotal = (TextView)convertView.findViewById(R.id.tv_total);
            viewHolder.ivStatus = convertView.findViewById(R.id.iv_status);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Number number = numberList.get(position);
        viewHolder.tvNumber.setText(number.getNumber());
        viewHolder.tvTotal.setText("");
        if (number.getStatus().equals("free")){
            viewHolder.ivStatus.setBackgroundResource(R.drawable.rounded_food);
            viewHolder.tvTotal.setText("trống");
        } else if (number.getStatus().equals("busy")){
            viewHolder.ivStatus.setBackgroundResource(R.drawable.rounded_busy);
        }

        return convertView;
    }
    private class ViewHolder{
        private TextView tvNumber, tvTotal;
        private ImageView ivStatus;
    }
}
