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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NumberAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private List<Number> numberList;
    List<Ordered>orderedList;
    long tongtien;
    SessionManager sessionManager;
    String getResEmail;

    public NumberAdapter(@NonNull Context context, int resource, @NonNull List<Number> numberList) {
        super(context, resource, numberList);
        this.context = context;
        this.numberList = numberList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);

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
        String total = number.getTotal();
        if (total.isEmpty()){
            viewHolder.tvTotal.setText("");
        }else {
            DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatPrice.applyPattern("###,###,###");
            viewHolder.tvTotal.setText(formatPrice.format(Integer.valueOf(total)));
        }

        if (number.getStatus().equals("free")){
//            viewHolder.ivStatus.setBackgroundResource(R.drawable.rounded_food);
            viewHolder.ivStatus.setImageResource(R.drawable.rounded_food);
            viewHolder.tvTotal.setText("");
        } else if (number.getStatus().equals("busy")){
//            viewHolder.ivStatus.setBackgroundResource(R.drawable.rounded_busy);
            viewHolder.ivStatus.setImageResource(R.drawable.rounded_busy);

//            tongtien = 0;
//            for (int a =0; a<orderedList.size();a++){
//                tongtien += Integer.valueOf(orderedList.get(a).getTotal());
//            }
//            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//            formatter.applyPattern("#,###,###,###");
//            viewHolder.tvTotal.setText(formatter.format(tongtien));
        }


        return convertView;
    }
    private class ViewHolder{
        private TextView tvNumber, tvTotal;
        private ImageView ivStatus;
    }
    private void setTotal(){

    }
}
