package org.irestaurant.irm.Database;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.irestaurant.irm.Interface.IOnFoodGroupClickListener;
import org.irestaurant.irm.R;

import java.util.List;

public class FoodGroupAdapter extends RecyclerView.Adapter<FoodGroupAdapter.MyViewHolder> {

    List<String> foodGroupList;
    IOnFoodGroupClickListener iOnFoodGroupClickListener;

    public void setIOnFoodGroupClickListener(IOnFoodGroupClickListener iOnFoodGroupClickListener) {
        this.iOnFoodGroupClickListener = iOnFoodGroupClickListener;
    }

    public FoodGroupAdapter(List<String> foodGroupList) {
        this.foodGroupList = foodGroupList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_groupmenu, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final int available_position = foodGroupList.indexOf(foodGroupList.get(i));

            myViewHolder.tvGroupMenu.setText(foodGroupList.get(i).toString());
            ColorGenerator generator = ColorGenerator.MATERIAL;
            myViewHolder.layoutGroup.setBackgroundColor(generator.getRandomColor());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    iOnFoodGroupClickListener.onFoodGroupClickListener(foodGroupList.get(i), available_position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodGroupList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutGroup;
        TextView tvGroupMenu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutGroup = itemView.findViewById(R.id.layout_group);
            tvGroupMenu = itemView.findViewById(R.id.tv_groupmenu);

        }
    }
}
