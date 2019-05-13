package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.irestaurant.irm.FragmentChoose;
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

import javax.annotation.Nullable;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private List<Food> foodList, filterList;
    EditText edtPrice;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    CollectionReference numberRef;
    SessionManager sessionManager;
    String getResEmail, getPosition;
    CustomFilter cs;
    private long price;
    String Table, TableId, sDiscount;
    private int p = -1;


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

        getResEmail = user.get(sessionManager.RESEMAIL);
        getPosition = user.get(sessionManager.POSITION);
        numberRef = mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.NUMBER);
        if (viewHolder instanceof GroupViewHolder){
            GroupViewHolder groupViewHolder = (GroupViewHolder)viewHolder;
            groupViewHolder.tvGroupMenu.setText(foodList.get(i).getGroup());
            if (!foodList.get(i).getFoodprice().equals("0")){
                groupViewHolder.tvDiscount.setText("Giảm: "+foodList.get(i).getFoodprice()+" %");
            }else {
                groupViewHolder.tvDiscount.setText("");
            }

            groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentChoose fragmentChoose = new FragmentChoose();
                    if (Config.CHECKACTIVITY.equals("FragmentChoose")){
                        if (p == i){
                            fragmentChoose.getGroup(getResEmail);
                            p = -1;
                        }else {
                            fragmentChoose.getData(foodList.get(i).getGroup(), getResEmail);
                            p = i;
                        }
                    }else if (Config.CHECKACTIVITY.equals("MenuActivity") && getPosition.equals("admin")){
                        Toast.makeText(context, "Giữ để giảm giá các món ăn trong danh mục", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            groupViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Config.CHECKACTIVITY.equals("MenuActivity") && getPosition.equals("admin")){
                        final String group = foodList.get(i).getGroup();
                        final String groupID = foodList.get(i).foodId;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_discount);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        Button btnClose = dialog.findViewById(R.id.btn_close);
                        Button btnConfirm = dialog.findViewById(R.id.btn_save);
                        final EditText edtDiscount = dialog.findViewById(R.id.edt_discount);
                        TextView tvGroup = dialog.findViewById(R.id.tv_groupmenu);
                        tvGroup.setText(group);
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { dialog.dismiss(); }
                        });
                        edtDiscount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String discount = edtDiscount.getText().toString();
                                if (!discount.isEmpty()){
                                    if (Integer.valueOf(discount)>100){edtDiscount.setText("100"); edtDiscount.setSelection(edtDiscount.getText().length());}
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtDiscount.getText().toString().isEmpty()){
                                    sDiscount = "0";
                                }else {
                                    sDiscount = edtDiscount.getText().toString();
                                }
                                Map<String, Object>discountMap = new HashMap<>();
                                discountMap.put(Config.FOODPRICE, sDiscount);
                                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(groupID).update(discountMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Giảm giá "+sDiscount+" % các món trong danh mục "+group, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        dialog.show();
                    }
                    return false;
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
                                                                String total = doc.getString("total");
                                                                String newAmount = String.valueOf(Integer.valueOf(amount)+Integer.valueOf(oldAmount));
                                                                String newTotal = String.valueOf(Integer.valueOf(price)*Integer.valueOf(newAmount));
                                                                updateOrdered(i, foodname, foodId, newAmount, amount, price, total, dialog);
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

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String foodname = foodList.get(i).getFoodname();
                    final String foodprice = filterList.get(i).getFoodprice();
                    final String ID = foodList.get(i).foodId;
                    String group = filterList.get(i).getGroup();
                    if (getPosition.equals("admin") && Config.CHECKACTIVITY.equals("MenuActivity")){
                        final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context,v);
                        popupMenu.getMenuInflater().inflate(R.menu.ordered_popup,popupMenu.getMenu());
                        popupMenu.setGravity(Gravity.RIGHT);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.popup_edit:
                                        final Dialog dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.dialog_addfood);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.setCanceledOnTouchOutside(false);
                                        Button btnClose     = (Button) dialog.findViewById(R.id.btn_close);
                                        final TextView tvThem = dialog.findViewById(R.id.themmon);
                                        tvThem.setText(foodname);
                                        final Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
                                        btnConfirm.setText("Lưu");
                                        final EditText edtFoodname = (EditText) dialog.findViewById(R.id.edt_foodname);
                                        edtFoodname.setText(foodname);
                                        final RelativeLayout layoutAdd = dialog.findViewById(R.id.layout_add);
                                        edtPrice = (EditText) dialog.findViewById(R.id.edt_foodprice);
                                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                        formatter.applyPattern("#,###,###,###");
                                        edtPrice.setText(formatter.format(Integer.valueOf(foodprice)));

                                        final Spinner spnCategory = dialog.findViewById(R.id.spn_category);
                                        final List<String> listCategory = new ArrayList<>();
                                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.spinner_item,listCategory);
                                        spnCategory.setAdapter(adapter);

                                        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e == null){
                                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                        String categoryID = doc.getDocument().getId();
                                                        switch (doc.getType()){
                                                            case ADDED:
                                                                String first = categoryID.substring(0,1);
                                                                if (first.equals("0")){
                                                                    String categoryName = doc.getDocument().getString(Config.GROUP);
                                                                    listCategory.add(categoryName);
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                                break;
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        btnClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });

                                        edtPrice.addTextChangedListener(onTextChangedListener());
                                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String name = edtFoodname.getText().toString();
                                                String price = edtPrice.getText().toString().replaceAll(",","");
                                                if (name.isEmpty()){
                                                    edtFoodname.setError("Nhập tên món ăn (uống)");
                                                    edtFoodname.requestFocus();
                                                }else if (price.isEmpty()){
                                                    edtPrice.setError("Nhập đơn giá");
                                                    edtPrice.requestFocus();
                                                }else {
                                                    tvThem.setText("Đang thay đổi ...");
                                                    layoutAdd.setVisibility(View.GONE);
                                                    String group = spnCategory.getSelectedItem().toString();
                                                    changeFood(name, price,dialog, group);
                                                }
                                            }
                                        });

                                        dialog.show();
                                        break;
                                    case R.id.popup_delete:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(Html.fromHtml("Bạn muốn xóa món "+"<font color='red'>"+foodList.get(i).getFoodname()+"</font>"+" không?"));
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface dialogInterface, int a) {
                                                mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(ID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialogInterface.dismiss();
                                                        Toast.makeText(context, "Đã xóa món "+ foodname, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                    return false;
                }
            });
        }
    }

    private void changeFood (String foodname, String foodprice, final Dialog dialog, String group){
        String foodID = Config.VNCharacterUtils.removeAccent(foodname).trim();

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put(Config.FOODNAME, foodname);
        nameMap.put(Config.FOODPRICE, foodprice);
        nameMap.put(Config.GROUP, group);
        nameMap.put(Config.VIEWTYPE, Config.VIEWTYPEITEM);
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(foodID).update(nameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.dacoloi+"\n"+e, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    //    FormatPrice
    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                edtPrice.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    edtPrice.setText(formattedString);
                    edtPrice.setSelection(edtPrice.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                edtPrice.addTextChangedListener(this);
            }
        };
    }

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

    private void addOrdered (final int i, final String amount, final Dialog dialog){
        String group = "0"+Config.VNCharacterUtils.removeAccent(foodList.get(i).getGroup());
//        sDiscount = "0";
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(group).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                sDiscount = documentSnapshot.getString(Config.FOODPRICE);
                if (sDiscount == null){
                    sDiscount = "0";
                }else {
                    sDiscount = documentSnapshot.getString(Config.FOODPRICE);
                }
                String date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                String time = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(new Date());
                final String foodid = foodList.get(i).foodId;
                final String foodname = foodList.get(i).getFoodname();
                final String total = String.valueOf((Integer.valueOf(amount)*price)-(Integer.valueOf(amount)*price*Integer.valueOf(sDiscount)/100));
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
                        numberRef.document(TableId).update(Config.STATUS,"busy");
                        Toast.makeText(context, "Đã thêm "+amount+" phần "+foodname, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }
    private void updateOrdered(final int i, final String foodname, final String foodId, final String newAmount, final String amount, final String price,final String total, final Dialog dialog){
        String group = "0"+Config.VNCharacterUtils.removeAccent(foodList.get(i).getGroup());
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(group).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                sDiscount = documentSnapshot.getString(Config.FOODPRICE);
                if (sDiscount == null){
                    sDiscount = "0";
                }else {
                    sDiscount = documentSnapshot.getString(Config.FOODPRICE);
                }
                String newTotal = String.valueOf(Integer.valueOf(total)+(Integer.valueOf(amount)*Integer.valueOf(price))-(Integer.valueOf(sDiscount)*Integer.valueOf(price)/100));
                Map<String, Object> updateAmout = new HashMap<>();
                updateAmout.put(Config.AMOUNT, newAmount);
                updateAmout.put(Config.TOTAL, newTotal);
                numberRef.document(TableId).collection("unpaid").document(foodId).update(updateAmout).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String tt = String.valueOf(Integer.valueOf(amount)*Integer.valueOf(price));
//                        updateTableOrdered(tt);
                        Toast.makeText(context, "Đã thêm "+amount+" phần "+ foodname, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });


    }
//    private void updateTableOrdered(final String tt){
//        numberRef.document(TableId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()){
//                    String total = documentSnapshot.getString("total");
//                        numberRef.document(TableId).update("total",String.valueOf(Integer.valueOf(total)+Integer.valueOf(tt)));
//                }
//            }
//        });
//    }
    private class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupMenu, tvDiscount;
        RelativeLayout layoutGroup;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupMenu = itemView.findViewById(R.id.tv_groupmenu);
            tvDiscount  = itemView.findViewById(R.id.tv_discount);
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
