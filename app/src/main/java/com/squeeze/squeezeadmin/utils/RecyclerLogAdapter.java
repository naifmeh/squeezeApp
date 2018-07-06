package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.squeeze.squeezeadmin.R;

public class RecyclerLogAdapter extends RecyclerView.Adapter<RecyclerLogAdapter.LogViewHolder> {
    private static final String TAG = RecyclerLogAdapter.class.getSimpleName();

    private RecyclerLogAdapterListener mListener;
    private Context mCtx;
    private JsonArray mJsonArray;

    public RecyclerLogAdapter(Context context, JsonArray jsonArray,
                              RecyclerLogAdapterListener listener) {
        mCtx = context;
        mJsonArray = jsonArray;
        mListener = listener;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mCtx).inflate(R.layout.logs_recycler_layout, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        if (mJsonArray == null) return;

        String sentence = mJsonArray.get(position).getAsString();
        holder.mContent.setText(sentence);
    }

    @Override
    public int getItemCount() {
        if (mJsonArray == null || mJsonArray.size() == 0) return 0;
        return mJsonArray.size();
    }

    public interface RecyclerLogAdapterListener {

    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        public TextView mContent;

        public LogViewHolder(View itemView) {
            super(itemView);

            mContent = (TextView) itemView.findViewById(R.id.logContentTextView);
        }

    }
}
