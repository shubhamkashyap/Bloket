package com.bloket.android.modules.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bloket.android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsFragment extends Fragment {

    RecyclerView mRecyclerView;
    GroupsAdapter mAdapter;

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater mInflater, @Nullable ViewGroup mContainer, @Nullable Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fm_groups_layout, mContainer, false);
        try {
            FloatingActionButton mFloatingButton = getActivity().findViewById(R.id.mpFabButton);
            mFloatingButton.setImageResource(R.drawable.ic_action_add);
            mFloatingButton.setOnClickListener(mTempView -> {
                Toast.makeText(getContext(), "Add group", Toast.LENGTH_SHORT).show();
            });

            mRecyclerView = mView.findViewById(R.id.gpRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadGroupList();
        } catch (Exception mException) {

        }
        return mView;
    }

    private void loadGroupList() {
        GroupsListTask mAsyncTask = new GroupsListTask(getContext(), mGroupsList -> {
            mAdapter = new GroupsAdapter(getContext(), mGroupsList);
            mRecyclerView.setAdapter(mAdapter);
        });
        mAsyncTask.execute();
    }
}