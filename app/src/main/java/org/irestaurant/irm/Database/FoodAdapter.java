package org.irestaurant.irm.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import org.irestaurant.irm.CategoryActivity;
import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private List<Food> foodList, filterList;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResEmail;
    CustomFilter cs;


    public FoodAdapter(Context context, List<Food> foodList){
        this.context = context;
        this.foodList = foodList;
        this.filterList = foodList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == Config.VIEWTYPEGROUP){
            ViewGroup group = (ViewGroup)inflater.inflate(R.layout.item_groupmenu, viewGroup, false);
            GroupViewHolder groupViewHolder = new GroupViewHolder (group);
            return groupViewHolder;
        }else {
            ViewGroup group = (ViewGroup)inflater.inflate(R.layout.item_list_food, viewGroup, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder (group);
            return itemViewHolder;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return foodList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof GroupViewHolder){
            GroupViewHolder groupViewHolder = (GroupViewHolder)viewHolder;
            groupViewHolder.tvGroupMenu.setText(foodList.get(i).getGroup());

            groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)context).startActivityForResult(new Intent(context, CategoryActivity.class), Config.RESULT_CODE);
                }
            });
        }else if (viewHolder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder)viewHolder;
            itemViewHolder.tvFoodName.setText(foodList.get(i).getFoodname());
            DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatPrice.applyPattern("###,###,###");
            itemViewHolder.tvFoodPrice.setText(formatPrice.format(Integer.valueOf(foodList.get(i).getFoodprice())));

        }

    }

//    @Override
//    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
//        sessionManager = new SessionManager(context);
//        HashMap<String, String> user = sessionManager.getUserDetail();
//        getResEmail = user.get(sessionManager.RESEMAIL);
//        final String foodId = foodList.get(i).foodId;
//        final String[] pos = {foodId};
//        final String foodName = foodList.get(i).getFoodname();
//        final String foodPrice = foodList.get(i).getFoodprice();
//
//        viewHolder.tvFoodName.setText(foodName);
//        DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//        formatPrice.applyPattern("###,###,###");
//        viewHolder.tvFoodPrice.setText(formatPrice.format(Integer.valueOf(foodPrice)));
//
//        viewHolder.tvFoodName.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (!pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = "0";
//
//                }else {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//                }
//                return true;
//            }
//        });
//        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (!pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = "0";
//
//                }else {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//                }
//                return true;
//            }
//        });
//        viewHolder.btnEdit.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (!pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = "0";
//
//                }else {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//                }
//                return true;
//            }
//        });
//        viewHolder.btnDelete.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (!pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = "0";
//
//                }else {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//                }
//                return true;
//            }
//        });
//        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//                }
//            }
//        });
//        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pos[0].equals("0")) {
//
//                }
//            }
//        });
//        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pos[0].equals("0")) {
//                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
//                    animate.setDuration(200);
//                    animate.setFillAfter(true);
//                    viewHolder.layoutItem.startAnimation(animate);
//                    pos[0] = foodId;
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("Bạn có muốn xóa món " + foodList.get(i).getFoodname() + " không ?");
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//
//                        }
//                    });
//                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            deleteFood(foodName, foodId);
//                        }
//                    });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public Filter getFilter() {
        if (cs  == null){
            cs  = new CustomFilter();
        }
        return cs;
    }



    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<Food> filter = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getFoodname().toUpperCase().contains(constraint)) {
//                        Food food = new Food(filterList.get(i).getFoodname(),filterList.get(i).getFoodprice());
//                        filter.add(food);
                    }
                }
                results.count = filter.size();
                results.values =filter;
            }else {
                results.count = filterList.size();
                results.values =filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foodList = (List<Food>) results.values;
            notifyDataSetChanged();
        }
    }

    private void deleteFood (final String foodname, final String id){
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Đã xóa món "+foodname , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.dacoloi, Toast.LENGTH_SHORT).show();
                Log.d("Loi", e.getMessage());
            }
        });
    }
    private void updateFood (){

    }

    private class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupMenu;
        LinearLayout layoutGroup;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupMenu = itemView.findViewById(R.id.tv_groupmenu);
            layoutGroup = itemView.findViewById(R.id.layout_group);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodPrice;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_foodname);
            tvFoodPrice = itemView.findViewById(R.id.tv_foodprice);
        }
    }
}
