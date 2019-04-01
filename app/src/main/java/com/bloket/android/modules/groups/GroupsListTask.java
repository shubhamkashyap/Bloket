package com.bloket.android.modules.groups;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

public class GroupsListTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private GroupsResponse mResponse;

    GroupsListTask(Context mContext, GroupsResponse mResponse) {
        this.mContext = mContext;
        this.mResponse = mResponse;
    }

    @Override
    protected Void doInBackground(Void... mVoid) {

        return null;
    }

    public interface GroupsResponse {

        void onTaskCompletion(ArrayList<GroupsDataPair> mGroupsList);

    }
}
