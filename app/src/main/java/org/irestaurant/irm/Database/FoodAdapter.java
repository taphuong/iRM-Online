package org.irestaurant.irm.Database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.irestaurant.irm.MenuActivity;
import org.irestaurant.irm.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends ArrayAdapter {
    EditText edtPrice, edtFoodname;
    private Context context;
    private int resource;
    private List<Food> foodList;
    LayoutInflater inflater;
    private int layout;
    DatabaseFood databaseFood;
    MenuActivity menuActivity;

    public FoodAdapter(@NonNull Context context, int layout, @NonNull List<Food> foodList, MenuActivity menuActivity) {
        super(context, layout, foodList);
        this.context = context;
        this.layout = layout;
        this.foodList = foodList;
        this.menuActivity = menuActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        databaseFood = new DatabaseFood(context);

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_food,parent,false);
        }
        final FoodViewHolder vholder = new FoodViewHolder(convertView);
        final Food food = foodList.get(position);
        int stt = position +1;
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        vholder.tvFood.setText(stt+". "+food.getFoondname());

        vholder.tvPrice.setText(formatter.format(Integer.valueOf(food.getFoodprice())));

        vholder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v) {
                if (vholder.pos ==1) {
                    TranslateAnimation animate = new TranslateAnimation(-vholder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos =0;
                }

            }
        });
        vholder.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View v) {
                if (vholder.pos == 0) {
                    TranslateAnimation animate = new TranslateAnimation(0, -vholder.layoutButton.getWidth(), 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos = 1;

                }else {
                    TranslateAnimation animate = new TranslateAnimation(-vholder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos =0;
                }
                return true;
            }
        });
        vholder.setOnFocusChangeListener(new ItemFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && vholder.pos ==1){
                    TranslateAnimation animate = new TranslateAnimation(-vholder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos =0;

                }
            }
        });

        vholder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vholder.pos==1) {
                    TranslateAnimation animate = new TranslateAnimation(-vholder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos =0;

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_addfood);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(false);
                    Button btnClose     = dialog.findViewById(R.id.btn_close);
                    Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
                    edtFoodname = dialog.findViewById(R.id.edt_foodname);
                    edtFoodname.setText(food.getFoondname());
                    edtPrice = dialog.findViewById(R.id.edt_foodprice);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    edtPrice.setText(formatter.format(Integer.valueOf(food.getFoodprice())));
                    TextView tvTitle = dialog.findViewById(R.id.themmon);
                    tvTitle.setText("Thay đổi");
                    dialog.show();
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    edtPrice.addTextChangedListener(onTextChangedListener());
                    btnConfirm.setText("Lưu");
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = edtFoodname.getText().toString();
                            String price = edtPrice.getText().toString().replaceAll(",","");;
                            if (name.isEmpty()){
                                edtFoodname.setError("Nhập tên món ăn (uống)");
                                edtFoodname.requestFocus();
                            }else if (price.isEmpty()){
                                edtPrice.setError("Nhập đơn giá");
                                edtPrice.requestFocus();
                            }else {
                                food.setId(Integer.parseInt(food.getId()+""));
                                food.setFoondname(edtFoodname.getText().toString());
                                food.setFoodprice(edtPrice.getText().toString().replaceAll(",",""));
                                int result = databaseFood.updateFood(food);
                                if (result>0){

                                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    menuActivity.setLvFood();
                                }
                            }
                        }
                    });
                }
            }
        });
        vholder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vholder.pos==1) {
                    TranslateAnimation animate = new TranslateAnimation(-vholder.layoutButton.getWidth(), 0, 0, 0);
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    vholder.layoutItem.startAnimation(animate);
                    vholder.pos =0;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Bạn có muốn xóa món "+food.getFoondname()+ " không ?" );
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
                            databaseFood.deleteFood(food.getId());
                            Toast.makeText(context, "Đã xóa món "+food.getFoondname(), Toast.LENGTH_SHORT).show();
//                            menuActivity = new MenuActivity();
                            menuActivity.setLvFood();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        return convertView;
    }

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

}
