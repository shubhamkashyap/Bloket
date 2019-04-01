package com.bloket.android.modules.blacklist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.bloket.android.utilities.helpers.database.DatabaseHelper;
import com.bloket.android.utilities.helpers.ui.UIHelper;
import com.bloket.android.utilities.picker.ContactPickerIdNumber;
import com.bloket.android.utilities.picker.ContactPickerResult;

import java.util.ArrayList;

public class BlacklistTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private ArrayList<ContactPickerResult> mSelectedContacts;
    private Dialog mProgressDialog;
    private BlacklistAddResponse mResponse;

    BlacklistTask(Context mContext, ArrayList<ContactPickerResult> mSelectedContacts, BlacklistAddResponse mResponse) {
        this.mContext = mContext;
        this.mSelectedContacts = mSelectedContacts;
        this.mResponse = mResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = UIHelper.makeProgressDialog(mContext);
        mProgressDialog.setOnCancelListener(mDialog -> BlacklistTask.this.cancel(true));
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... mVoid) {
        DatabaseHelper mDbHelper = new DatabaseHelper(mContext);
        ArrayList<ContactPickerIdNumber> mOldSelection = mDbHelper.getBlacklistContacts();
        ArrayList<ContactPickerIdNumber> mNewSelection = new ArrayList<>();
        for (ContactPickerResult mContact : mSelectedContacts)
            mNewSelection.add(new ContactPickerIdNumber(mContact.getContactId(), mContact.getPhoneNumber()));

        ArrayList<ContactPickerIdNumber> mIntersection = new ArrayList<>(mOldSelection);
        mIntersection.retainAll(mNewSelection);
        mNewSelection.removeAll(mIntersection);
        mOldSelection.removeAll(mIntersection);
        for (ContactPickerIdNumber mContact : mOldSelection) {
            if (mContact.getContactId() == null) continue;
            mDbHelper.delBlacklistContact(Integer.valueOf(mContact.getContactId()), mContact.getPhoneNumber());
        }
        for (ContactPickerIdNumber mContact : mNewSelection) {
            if (mContact.getContactId() == null) continue;
            mDbHelper.putBlacklistData(DatabaseHelper.TYPE_CONTACT, DatabaseHelper.DEFAULT_GROUP_ID, "", mContact.getContactId(), mContact.getPhoneNumber(), "", "", "", "");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void mVoid) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        mResponse.onTaskCompletion(true);
    }

    public interface BlacklistAddResponse {

        void onTaskCompletion(boolean mSuccess);

    }
}
