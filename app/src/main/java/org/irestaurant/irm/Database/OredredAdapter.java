package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.R;

import java.util.HashMap;
import java.util.List;

public class OredredAdapter extends RecyclerView.Adapter<OredredAdapter.ViewHolder> {
    private Context context;
    private List<Ordered> orderedList;

    String getResEmail;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;
    SessionManager sessionManager;

    public OredredAdapter(Context context,List<Ordered> orderedList) {
        this.context = context;
        this.orderedList = orderedList;
    }

    @Override
    public OredredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ordered, parent, false);
        return new OredredAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return orderedList.size();
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);

        String orderId = orderedList.get(i).orderedId;
        String foodname = orderedList.get(i).getFoodname();
        String amount = orderedList.get(i).getAmount();
        viewHolder.tvFoodname.setText(foodname);
        viewHolder.tvAmount.setText(amount);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvFoodname, tvAmount;


        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvFoodname = mView.findViewById(R.id.tv_foodname);
            tvAmount = mView.findViewById(R.id.tv_amount);
        }
    }

}
