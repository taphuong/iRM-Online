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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.CategoryActivity;
import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private List<Food> foodList, filterList;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;
    SessionManager sessionManager;
    String getResEmail;
    CustomFilter cs;
    private long price;
    String Table, TableId;


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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        sessionManager = new SessionManager(context);
        Map<String, String> user = sessionManager.getUserDetail();
        Table = Config.TABLE;
        TableId = Config.TABLEID;

//        if (Integer.valueOf(Table)<10){
//            TableId = "00"+Table;
//        }else if (Integer.valueOf(Table)<100){
//            TableId = "0"+Table;
//        }else {
//            TableId = Table;
//        }
        getResEmail = user.get(sessionManager.RESEMAIL);
        numberRef = mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER);
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

            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Config.CHECKACTIVITY.equals("FragmentChoose")){
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_addnumber);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        final Button btnMinus = (Button) dialog.findViewById(R.id.btn_minus);
                        final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
                        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                        final RelativeLayout layoutAdd = dialog.findViewById(R.id.layout_add);
                        btnConfirm.setText("Chọn");
                        final TextView tvFood = dialog.findViewById(R.id.themban);
                        tvFood.setText(foodList.get(i).getFoodname());
                        final EditText edtAmount = (EditText) dialog.findViewById(R.id.edt_amount);
                        price = Integer.valueOf(foodList.get(i).getFoodprice());
                        dialog.show();
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnMinus.setVisibility(View.VISIBLE);
                                if (edtAmount.getText().toString().equals("")){
                                    edtAmount.setText("1");
                                } else if (Integer.valueOf(edtAmount.getText().toString())==999){
                                    edtAmount.setText("1000");
                                    btnAdd.setVisibility(View.INVISIBLE);
                                } else {
                                    edtAmount.setText(String.valueOf(Integer.valueOf(edtAmount.getText().toString())+1));
                                }
                            }
                        });
                        btnMinus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnAdd.setVisibility(View.VISIBLE);
                                String stramount = edtAmount.getText().toString();
                                if (stramount.isEmpty() || stramount.equals("2")){
                                    edtAmount.setText("1");
                                    btnMinus.setVisibility(View.INVISIBLE);
                                }  else {
                                    long amount = Integer.valueOf(edtAmount.getText().toString());
                                    edtAmount.setText(String.valueOf(amount-1));
                                }

                            }
                        });
                        edtAmount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String amout = edtAmount.getText().toString();
                                if (amout.isEmpty() || amout.equals("1") || amout.equals("0")) {
                                    btnMinus.setVisibility(View.INVISIBLE);
                                    btnAdd.setVisibility(View.VISIBLE);
                                } else if (Integer.valueOf(amout) > 1000) {
                                    edtAmount.setText("1000");
                                    edtAmount.setSelection(edtAmount.getText().length());
                                    btnAdd.setVisibility(View.INVISIBLE);
                                    btnMinus.setVisibility(View.VISIBLE);
                                } else {
                                    btnMinus.setVisibility(View.VISIBLE);
                                    btnAdd.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                String number = orderedActivity.getNumber;
                                String foodname = foodList.get(i).getFoodname();
                                final String amount = edtAmount.getText().toString();
                                String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                                String total = String.valueOf(price*Integer.valueOf(amount));
                                if (edtAmount.getText().toString().equals("") || edtAmount.getText().toString().equals("0")){
                                    edtAmount.setError("Nhập số phần");
                                    edtAmount.requestFocus();
                                }else {
                                    tvFood.setText("Đang chọn món ...");
                                    layoutAdd.setVisibility(View.GONE);


                                    numberRef.document(TableId).collection("unpaid").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (DocumentSnapshot doc : task.getResult()){
                                                            if (doc.getId().equals((foodList.get(i).foodId))){
                                                                String oldAmount = doc.getString(Config.AMOUNT);
                                                                String foodId = doc.getId();
                                                                String foodname = doc.getString(Config.FOODNAME);
                                                                String price = doc.getString("price");
                                                                String newAmount = String.valueOf(Integer.valueOf(amount)+Integer.valueOf(oldAmount));
                                                                String newTotal = String.valueOf(Integer.valueOf(price)*Integer.valueOf(newAmount));
                                                                updateOrdered(foodname, foodId, newAmount, newTotal, amount, price, dialog);
                                                                return;
                                                            }
                                                        }
                                                        addOrdered(i, amount, dialog);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                }
            });

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

    private void addOrdered (int i, final String amount, final Dialog dialog){
        String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());
        final String foodid = foodList.get(i).foodId;
        final String foodname = foodList.get(i).getFoodname();
        final String total = String.valueOf(Integer.valueOf(amount)*price);
        Map<String, Object> addOrder = new HashMap<>();
        addOrder.put("foodname", foodList.get(i).getFoodname());
        addOrder.put("price", String.valueOf(price));
        addOrder.put("total", total);
        addOrder.put("amount", amount);
        addOrder.put("date",date);
        addOrder.put("time",time);
        numberRef.document(TableId).collection("unpaid").document(foodid).set(addOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Đã thêm "+amount+" phần "+foodname, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                updateTable(total);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    private void updateOrdered(final String foodname, final String foodId, String newAmount, String newTotal, final String amount, final String price, final Dialog dialog){
        Map<String, Object> updateAmout = new HashMap<>();
        updateAmout.put(Config.AMOUNT, newAmount);
        updateAmout.put(Config.TOTAL, newTotal);
        numberRef.document(TableId).collection("unpaid").document(foodId).update(updateAmout).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String tt = String.valueOf(Integer.valueOf(amount)*Integer.valueOf(price));
                updateTableOrdered(tt);
                Toast.makeText(context, "Đã thêm "+amount+" phần "+ foodname, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }
    private void updateTable(final String tt){
        numberRef.document(TableId).update(Config.STATUS,"busy");
        numberRef.document(TableId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String total = documentSnapshot.getString("total");
                    if (total.isEmpty()){
                        total = tt;
                        numberRef.document(TableId).update("total",total);
                    }else {
                        numberRef.document(TableId).update("total",String.valueOf(Integer.valueOf(total)+Integer.valueOf(tt)));
                    }
                }
            }
        });
    }
    private void updateTableOrdered(final String tt){
        numberRef.document(TableId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String total = documentSnapshot.getString("total");
                        numberRef.document(TableId).update("total",String.valueOf(Integer.valueOf(total)+Integer.valueOf(tt)));
                }
            }
        });
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
