package com.globant.iotwifimanager;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.globant.model.APInfo;

import java.util.List;

/**
 * Created by javier on 04/04/17.
 */

public class APInfoAdapter extends RecyclerView.Adapter<APInfoAdapter.MyViewHolder> {
    private List<APInfo> mList;
    private int selectedPos;
    private AdapterCallback mCallback;

    public APInfoAdapter(List<APInfo> items, AdapterCallback callback) {
        mList = items;
        mCallback = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ap_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvSSID.setText(mList.get(position).getSSID());
        holder.tvSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPos = position;
                mCallback.onClick();
            }
        });
        holder.tvDesription.setText(mList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public APInfo getSelectedItem(){
        return mList.get(selectedPos);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSSID;
        private TextView tvDesription;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSSID = (TextView) itemView.findViewById(R.id.tvSSID);
            tvDesription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }

    public interface AdapterCallback {
        void onClick();
    }
}
