package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RevenueDetailAdapter extends RecyclerView.Adapter<RevenueDetailAdapter.ViewHolder> {
    private Context context;
    private List<RevenueDetail> revenueDetailList;

    public RevenueDetailAdapter(Context context, List<RevenueDetail> revenueDetailList) {
        this.context = context;
        this.revenueDetailList = revenueDetailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_revenue_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String time = revenueDetailList.get(i).getTime();
        String number = revenueDetailList.get(i).getNumber();
        String total = revenueDetailList.get(i).getTotal();
        viewHolder.tvNumber.setText(number);
        viewHolder.tvTime.setText(time);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(total)));
    }

    @Override
    public int getItemCount() {
        return revenueDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvTime, tvNumber, tvTotal;


        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvTime = mView.findViewById(R.id.tv_time);
            tvNumber = mView.findViewById(R.id.tv_number);
            tvTotal = mView.findViewById(R.id.tv_total);
        }
    }
}
