package com.bloket.android.modules.groups;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<GroupsDataPair> mGroupsList;

    GroupsAdapter(Context mContext, ArrayList<GroupsDataPair> mGroupsList) {
        this.mContext = mContext;
        this.mGroupsList = mGroupsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
