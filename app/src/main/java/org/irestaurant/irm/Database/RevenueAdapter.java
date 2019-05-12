package org.irestaurant.irm.Database;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.R;
import org.irestaurant.irm.RevenueActivity;
import org.irestaurant.irm.RevenueDetailActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.ViewHolder> {
    private Context context;
    private List<Revenue> revenueList;
    SessionManager sessionManager;
    String getResEmail;

    public RevenueAdapter(Context context, List<Revenue> revenueList) {
        this.context = context;
        this.revenueList = revenueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_revenue, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        sessionManager = new SessionManager(context);
        Map<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        final String date = revenueList.get(i).getDate();
        final String rdate = revenueList.get(i).revenueId;
        String total = revenueList.get(i).getTotal();
        viewHolder.tvDate.setText(date);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        viewHolder.tvTotal.setText(formatter.format(Integer.valueOf(total)));
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RevenueDetailActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("rdate", rdate);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return revenueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvDate, tvTotal;


        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvDate = mView.findViewById(R.id.tv_date);
            tvTotal = mView.findViewById(R.id.tv_total);
        }
    }
}
