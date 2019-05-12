package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {
    private Context context;
    private List<Recent> recentList;

    public RecentAdapter(Context context, List<Recent> recentList) {
        this.context = context;
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String foodname = recentList.get(i).getFoodname();
        String amount = recentList.get(i).getAmount();
        String total = recentList.get(i).getTotal();
        DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatPrice.applyPattern("###,###,###");
        viewHolder.tvTotal.setText(formatPrice.format(Integer.valueOf(total)));
        viewHolder.tvAmount.setText(amount);
        viewHolder.tvFoodname.setText(foodname);

    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvFoodname, tvAmount, tvTotal;
        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvFoodname = mView.findViewById(R.id.tv_foodname);
            tvAmount = mView.findViewById(R.id.tv_amount);
            tvTotal = mView.findViewById(R.id.tv_total);
        }
    }
}
