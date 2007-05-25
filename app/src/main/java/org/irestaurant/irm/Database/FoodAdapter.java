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
import java.util.List;

public class FoodAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<Food> foodList;

    public FoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> foodList) {
        super(context, resource, foodList);
        this.context = context;
        this.resource = resource;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_food,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvFoodname = (TextView)convertView.findViewById(R.id.tv_foodname);
            viewHolder.tvFoodPrice = (TextView)convertView.findViewById(R.id.tv_foodprice);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        Food food= foodList.get(position);
        viewHolder.tvFoodname.setText(food.getFoondname());
        viewHolder.tvFoodPrice.setText(decimalFormat.format(Integer.valueOf(food.getFoodprice())));

        return convertView;
    }

    private class ViewHolder{
        private TextView tvFoodname, tvFoodPrice;
    }
}
