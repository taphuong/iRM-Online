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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.R;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context context;
    private List<Food> foodList;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    SessionManager sessionManager;
    String getResEmail;
    MenuActivity menuActivity;


    public FoodAdapter(Context context, List<Food> foodList, MenuActivity menuActivity){
        this.context = context;
        this.foodList = foodList;
        this.menuActivity = menuActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_food, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getResEmail = user.get(sessionManager.RESEMAIL);
        final String foodId = foodList.get(i).foodId;
        final String[] pos = {foodId};
        final String foodName = foodList.get(i).getFoodname();
        final String foodPrice = foodList.get(i).getFoodprice();

        viewHolder.tvFoodName.setText(foodName);
        DecimalFormat formatPrice = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatPrice.applyPattern("###,###,###");
        viewHolder.tvFoodPrice.setText(formatPrice.format(Integer.valueOf(foodPrice)));

        viewHolder.tvFoodName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!pos[0].equals("0")) {
                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = "0";

                }else {
                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = foodId;
                }
                return true;
            }
        });
        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!pos[0].equals("0")) {
                    TranslateAnimation animate = new TranslateAnimation(0, -viewHolder.layoutButton.getWidth(), 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = "0";

                }else {
                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = foodId;
                }
                return true;
            }
        });
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos[0].equals("0")) {
                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = foodId;
                }
            }
        });
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos[0].equals("0")) {

                }
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos[0].equals("0")) {
                    TranslateAnimation animate = new TranslateAnimation(-viewHolder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    viewHolder.layoutItem.startAnimation(animate);
                    pos[0] = foodId;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Bạn có muốn xóa món " + foodList.get(i).getFoodname() + " không ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteFood(foodName, foodId);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvFoodName, tvFoodPrice;
        private Button btnEdit, btnDelete;
        private RelativeLayout layoutItem, layoutButton;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvFoodName = mView.findViewById(R.id.tv_foodname);
            tvFoodPrice = mView.findViewById(R.id.tv_foodprice);
            btnDelete = mView.findViewById(R.id.btn_delete);
            btnEdit = mView.findViewById(R.id.btn_edit);
            layoutButton = mView.findViewById(R.id.layout_button);
            layoutItem = mView.findViewById(R.id.layout_item);
        }
    }
    private void deleteFood (final String foodname, final String id){
        mFirestore.collection(Config.RESTAURANTS).document(getResEmail).collection(Config.MENU).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                menuActivity.reloadMenu();
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
}
