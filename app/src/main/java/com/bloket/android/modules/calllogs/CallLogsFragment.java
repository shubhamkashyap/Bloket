package com.bloket.android.modules.calllogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloket.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CallLogsFragment extends Fragment {

    public static CallLogsFragment newInstance() {
        return new CallLogsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater mInflater, @Nullable ViewGroup mContainer, @Nullable Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fm_main_screen_blocklogs, mContainer, false);
        return mView;
    }
}