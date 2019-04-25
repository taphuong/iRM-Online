package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.R;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList){
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_food, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvFoodName.setText(foodList.get(i).getFoondname());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        viewHolder.tvFoodPrice.setText(decimalFormat.format(Integer.valueOf(foodList.get(i).getFoodprice())));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvFoodName, tvFoodPrice;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvFoodName = mView.findViewById(R.id.tv_foodname);
            tvFoodPrice = mView.findViewById(R.id.tv_foodprice);
        }
    }
}
