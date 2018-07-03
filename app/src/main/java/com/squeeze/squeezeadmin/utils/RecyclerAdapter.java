package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.EmployeeBean;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private JsonArray mJsonArray;
    private Context mCtx;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView firstName;
        public TextView lastName;
        public ImageView editButton;
        public ImageView lockButton;
        public TextView dateBeg;
        public TextView dateEnd;

        public ViewHolder(View itemView) {
            super(itemView);

             firstName = (TextView) itemView.findViewById(R.id.firstNameRecycler);
             lastName = (TextView) itemView.findViewById(R.id.lastNameRecycler);
             editButton = (ImageView) itemView.findViewById(R.id.editButtonRecycler);
             lockButton = (ImageView) itemView.findViewById(R.id.lockAuthRecycler);
             dateBeg = (TextView) itemView.findViewById(R.id.dateBegRecycler);
             dateEnd = (TextView) itemView.findViewById(R.id.dateEndRecycler);
        }
    }

    public RecyclerAdapter(Context context, JsonArray jsonArray) {
        mCtx = context;
        mJsonArray = jsonArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mCtx).inflate(R.layout.list_employees_recycler_layout,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        if(mJsonArray == null) return;
        Gson gson = new Gson();
        EmployeeBean employee = gson.fromJson(mJsonArray.get(0),EmployeeBean.class);
        holder.firstName.setText(employee.getFirstName());
        holder.lastName.setText(employee.getLastName());
        holder.editButton.setOnClickListener(null); //TODO: Renvoyer vers la modif
        DisplayUtils.setLockIcon(holder.lockButton,employee.authorized,mCtx);
        holder.dateBeg.setText(DisplayUtils.getTimestampDate(employee.getAuthStarting()));
        holder.dateEnd.setText(DisplayUtils.getTimestampDate(employee.getAuthEnding()));

    }

    @Override
    public int getItemCount() {
        if(mJsonArray == null || mJsonArray.size() == 0) return 0;
        return mJsonArray.size();
    }
}
