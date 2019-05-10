package org.irestaurant.irm.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import org.irestaurant.irm.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private Context context;
    private List<Invite> inviteList;
    SessionManager sessionManager;
    String getEmail, resEmail, resName, Date;

    public InviteAdapter(Context context, List<Invite> inviteList) {
        this.context = context;
        this.inviteList = inviteList;
    }

    @NonNull
    @Override
    public InviteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invite, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        sessionManager = new SessionManager(context);
        Map<String, String> user = sessionManager.getUserDetail();
        getEmail = user.get(sessionManager.EMAIL);
        resEmail = inviteList.get(i).getResemail();
        resName  = inviteList.get(i).getResname();
        Date     = inviteList.get(i).getDate();
        viewHolder.tvResEmail.setText(resEmail);
        viewHolder.tvResName.setText(resName);
        viewHolder.tvDate.setText(Date);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView tvResName, tvResEmail, tvDate;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            tvResName = mView.findViewById(R.id.tv_resname);
            tvResEmail = mView.findViewById(R.id.tv_resemail);
            tvDate = mView.findViewById(R.id.tv_date);
        }
    }
}
