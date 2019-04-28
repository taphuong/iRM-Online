package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.irestaurant.irm.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private Context context;
    private List<People> peopleList;

    public PeopleAdapter(Context context, List<People> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_people, viewGroup, false);
        return new PeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter.ViewHolder viewHolder, int i) {
        String email = peopleList.get(i).peopleId;
        String name = peopleList.get(i).getName();
        String image = peopleList.get(i).getImage();
        viewHolder.tvName.setText(name);
        viewHolder.tvEmail.setText(email);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(viewHolder.ivPeople);
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvName, tvEmail;
        private CircleImageView ivPeople;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvName = mView.findViewById(R.id.tv_name);
            tvEmail = mView.findViewById(R.id.tv_email);
            ivPeople = mView.findViewById(R.id.iv_people);
        }
    }
}
