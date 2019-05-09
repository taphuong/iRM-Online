package org.irestaurant.irm.Database;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.irestaurant.irm.R;

public class FoodViewHolder implements View.OnLongClickListener, View.OnClickListener, View.OnFocusChangeListener {
    TextView tvFood, tvPrice, tvStatus;
    RelativeLayout layoutItem, layoutMain, layoutButton;
    Button btnEdit, btnDelete;
    ItemClickListener itemClickListener;
    ItemLongClickListener itemLongClickListener;
     ItemFocusChangeListener itemFocusChangeListener;

    int pos;
    public FoodViewHolder(View v) {
        layoutItem = (RelativeLayout) v.findViewById(R.id.layout_item);
        layoutMain = v.findViewById(R.id.layout_main);
//        layoutButton = v.findViewById(R.id.layout_button);
        tvFood = (TextView) v.findViewById(R.id.tv_foodname);
//        tvFood.setSelected(true);
        tvPrice = (TextView) v.findViewById(R.id.tv_foodprice);
//        btnEdit = v.findViewById(R.id.btn_edit);
//        btnDelete = v.findViewById(R.id.btn_delete);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        v.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v);
    }
    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener=ic;
    }

    @Override
    public boolean onLongClick(View v) {
        this.itemLongClickListener.onItemLongClick(v);
        return true;
    }

    public void setItemLongClickListener(ItemLongClickListener ilc) {
        this.itemLongClickListener = ilc;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.itemFocusChangeListener.onFocusChange(v,hasFocus);
    }
    public void setOnFocusChangeListener(ItemFocusChangeListener fc){
        this.itemFocusChangeListener = fc;
    }
}
