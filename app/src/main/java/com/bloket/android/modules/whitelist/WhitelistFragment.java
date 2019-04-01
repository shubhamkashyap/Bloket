package com.bloket.android.modules.whitelist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bloket.android.R;
import com.bloket.android.utilities.helpers.database.DatabaseHelper;
import com.bloket.android.utilities.helpers.ui.UIHelper;
import com.bloket.android.utilities.picker.ContactPickerActivity;
import com.bloket.android.utilities.picker.ContactPickerResult;

import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class WhitelistFragment extends Fragment {

    private WhitelistAdapter mAdapter;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;

    public static WhitelistFragment newInstance() {
        return new WhitelistFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater mInflater, @Nullable ViewGroup mContainer, @Nullable Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fm_main_screen_whitelist, mContainer, false);
        mDbHelper = new DatabaseHelper(getContext());
        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
        mRecyclerView = mView.findViewById(R.id.rvRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        return mView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
        mRecyclerView.setAdapter(mAdapter);
        super.onResume();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int mRequestCode, int mResultCode, Intent mData) {
        super.onActivityResult(mRequestCode, mResultCode, mData);
        if (mRequestCode == ContactPickerActivity.PICKER_REQUEST_CODE) {
            if (mResultCode != RESULT_OK) return;
            ArrayList<ContactPickerResult> mSelectedContacts = new ArrayList<>(ContactPickerResult.obtainResult(mData));
            WhitelistTask mAddTask = new WhitelistTask(getContext(), mSelectedContacts, mSuccess -> {
                if (mSuccess) {
                    mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
            mAddTask.execute();
        }
    }

    public void fabClickAction() {
        showAddDialog();
    }

    @SuppressWarnings("ConstantConditions")
    private void showAddDialog() {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add, null, false);
        final Dialog mDialog = new Dialog(getContext());
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(mContentView);

        TextView mAddContact = mContentView.findViewById(R.id.tvAddContact);
        mAddContact.setOnClickListener(mView -> {
            if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
            Intent mIntent = new Intent(getActivity(), ContactPickerActivity.class);
            mIntent.putExtra("CHOICE_MODE", false);
            mIntent.putParcelableArrayListExtra("PRE_SELECT_LIST", mDbHelper.getWhitelistContacts());
            startActivityForResult(mIntent, ContactPickerActivity.PICKER_REQUEST_CODE);
            mDialog.dismiss();
        });

        TextView mAddNumber = mContentView.findViewById(R.id.tvAddNumber);
        mAddNumber.setOnClickListener(mView -> {
            @SuppressLint("InflateParams") View mAddNumberView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add_number, null, false);
            mDialog.setContentView(mAddNumberView);
            EditText mPhoneNumber = mAddNumberView.findViewById(R.id.etPhoneNumber);

            Button mNegative = mAddNumberView.findViewById(R.id.btNegative);
            mNegative.setOnClickListener(mButtonView -> {
                UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                mDialog.dismiss();
            });

            Button mPositive = mAddNumberView.findViewById(R.id.btPositive);
            mPositive.setOnClickListener(mButtonView -> {
                String mNumber = mPhoneNumber.getText().toString();
                Pattern mPattern = Pattern.compile("[0-9+]+");
                if (mPattern.matcher(mNumber).matches()) {
                    if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
                    if (!mDbHelper.chkWhitelistNumber(mNumber)) {
                        mDbHelper.putWhitelistData(DatabaseHelper.TYPE_NUMBER, DatabaseHelper.DEFAULT_GROUP_ID, "", "", mNumber, "", "", "");
                        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                        mRecyclerView.setAdapter(mAdapter);
                        UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                        mDialog.dismiss();
                    } else {
                        mPhoneNumber.setError("Number is already stored in whitelist");
                    }
                } else {
                    mPhoneNumber.setError("Enter a valid number");
                }
            });
        });

        TextView mAddGroup = mContentView.findViewById(R.id.tvAddGroup);
        mAddGroup.setOnClickListener(mView -> {
            mDialog.dismiss();
        });

        TextView mAddStarts = mContentView.findViewById(R.id.tvAddStarts);
        mAddStarts.setOnClickListener(mView -> {
            @SuppressLint("InflateParams") View mAddStartsView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add_starts, null, false);
            mDialog.setContentView(mAddStartsView);
            EditText mPhoneNumber = mAddStartsView.findViewById(R.id.etPhoneNumber);

            Button mNegative = mAddStartsView.findViewById(R.id.btNegative);
            mNegative.setOnClickListener(mButtonView -> {
                UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                mDialog.dismiss();
            });

            Button mPositive = mAddStartsView.findViewById(R.id.btPositive);
            mPositive.setOnClickListener(mButtonView -> {
                String mNumber = mPhoneNumber.getText().toString();
                Pattern mPattern = Pattern.compile("[0-9]+");
                if (mPattern.matcher(mNumber).matches()) {
                    if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
                    if (!mDbHelper.chkWhitelistStarts(mNumber)) {
                        mDbHelper.putWhitelistData(DatabaseHelper.TYPE_STARTS, DatabaseHelper.DEFAULT_GROUP_ID, "", "", mNumber, "", "", "");
                        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                        mRecyclerView.setAdapter(mAdapter);
                        UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                        mDialog.dismiss();
                    } else {
                        mPhoneNumber.setError("Entry is already stored in whitelist");
                    }
                } else {
                    mPhoneNumber.setError("Enter a valid number");
                }
            });
        });

        TextView mAddEnds = mContentView.findViewById(R.id.tvAddEnds);
        mAddEnds.setOnClickListener(mView -> {
            @SuppressLint("InflateParams") View mAddEndsView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add_ends, null, false);
            mDialog.setContentView(mAddEndsView);
            EditText mPhoneNumber = mAddEndsView.findViewById(R.id.etPhoneNumber);

            Button mNegative = mAddEndsView.findViewById(R.id.btNegative);
            mNegative.setOnClickListener(mButtonView -> {
                UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                mDialog.dismiss();
            });

            Button mPositive = mAddEndsView.findViewById(R.id.btPositive);
            mPositive.setOnClickListener(mButtonView -> {
                String mNumber = mPhoneNumber.getText().toString();
                Pattern mPattern = Pattern.compile("[0-9]+");
                if (mPattern.matcher(mNumber).matches()) {
                    if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
                    if (!mDbHelper.chkWhitelistEnds(mNumber)) {
                        mDbHelper.putWhitelistData(DatabaseHelper.TYPE_ENDS, DatabaseHelper.DEFAULT_GROUP_ID, "", "", mNumber, "", "", "");
                        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                        mRecyclerView.setAdapter(mAdapter);
                        UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                        mDialog.dismiss();
                    } else {
                        mPhoneNumber.setError("Entry is already stored in whitelist");
                    }
                } else {
                    mPhoneNumber.setError("Enter a valid number");
                }
            });
        });

        TextView mAddContains = mContentView.findViewById(R.id.tvAddContains);
        mAddContains.setOnClickListener(mView -> {
            @SuppressLint("InflateParams") View mAddContainsView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add_contains, null, false);
            mDialog.setContentView(mAddContainsView);
            EditText mPhoneNumber = mAddContainsView.findViewById(R.id.etPhoneNumber);

            Button mNegative = mAddContainsView.findViewById(R.id.btNegative);
            mNegative.setOnClickListener(mButtonView -> {
                UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                mDialog.dismiss();
            });

            Button mPositive = mAddContainsView.findViewById(R.id.btPositive);
            mPositive.setOnClickListener(mButtonView -> {
                String mNumber = mPhoneNumber.getText().toString();
                Pattern mPattern = Pattern.compile("[0-9]+");
                if (mPattern.matcher(mNumber).matches()) {
                    if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
                    if (!mDbHelper.chkWhitelistContains(mNumber)) {
                        mDbHelper.putWhitelistData(DatabaseHelper.TYPE_CONTAINS, DatabaseHelper.DEFAULT_GROUP_ID, "", "", mNumber, "", "", "");
                        mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                        mRecyclerView.setAdapter(mAdapter);
                        UIHelper.hideSoftKeyboard(getContext(), mPhoneNumber);
                        mDialog.dismiss();
                    } else {
                        mPhoneNumber.setError("Entry is already stored in whitelist");
                    }
                } else {
                    mPhoneNumber.setError("Enter a valid number");
                }
            });
        });

        TextView mAddRange = mContentView.findViewById(R.id.tvAddRange);
        mAddRange.setOnClickListener(mView -> {
            @SuppressLint("InflateParams") View mAddRangeView = LayoutInflater.from(getContext()).inflate(R.layout.dg_generic_add_range, null, false);
            mDialog.setContentView(mAddRangeView);
            EditText mRangeStart = mAddRangeView.findViewById(R.id.etRangeStart);
            EditText mRangeEnd = mAddRangeView.findViewById(R.id.etRangeEnd);

            Button mNegative = mAddRangeView.findViewById(R.id.btNegative);
            mNegative.setOnClickListener(mButtonView -> {
                UIHelper.hideSoftKeyboard(getContext(), mNegative);
                mDialog.dismiss();
            });

            Button mPositive = mAddRangeView.findViewById(R.id.btPositive);
            mPositive.setOnClickListener(mButtonView -> {
                String mStartNumber = mRangeStart.getText().toString();
                String mEndNumber = mRangeEnd.getText().toString();
                Pattern mPattern = Pattern.compile("[0-9]+");
                if (mPattern.matcher(mStartNumber).matches() && mPattern.matcher(mEndNumber).matches()) {
                    long mNumberOne = Long.valueOf(mStartNumber);
                    long mNumberTwo = Long.valueOf(mEndNumber);
                    if (mNumberOne < mNumberTwo) {
                        if (mDbHelper == null) mDbHelper = new DatabaseHelper(getContext());
                        if (!mDbHelper.chkWhitelistRange(String.valueOf(mNumberOne), String.valueOf(mNumberTwo))) {
                            mDbHelper.putWhitelistData(DatabaseHelper.TYPE_RANGE, DatabaseHelper.DEFAULT_GROUP_ID, "", "", String.valueOf(mNumberOne), String.valueOf(mNumberTwo), "", "");
                            mAdapter = new WhitelistAdapter(getContext(), mDbHelper.getWhitelistData());
                            mRecyclerView.setAdapter(mAdapter);
                            UIHelper.hideSoftKeyboard(getContext(), mPositive);
                            mDialog.dismiss();
                        } else {
                            mRangeEnd.setError("Entry is already stored in whitelist");
                        }
                    } else {
                        mRangeEnd.setError("First number must be less than last number");
                    }
                } else {
                    mRangeEnd.setError("Enter a valid range");
                }
            });
        });

        mDialog.show();
    }
}