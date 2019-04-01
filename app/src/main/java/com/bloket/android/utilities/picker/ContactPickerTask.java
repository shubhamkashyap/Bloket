package com.bloket.android.utilities.picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class ContactPickerTask extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ArrayList<ContactPickerDataPair> mContactList;
    private ContactPickerTask.ContactsResponse mResponse;

    ContactPickerTask(Activity mActivity, ContactPickerTask.ContactsResponse mResponse) {
        this.mActivity = mActivity;
        this.mResponse = mResponse;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected Void doInBackground(Void... mVoid) {
        try {

            // Initialize contact list
            mContactList = new ArrayList<>();

            // Fetch contacts
            ContentResolver mContactResolver = mActivity.getContentResolver();
            ContentProviderClient mProviderClient = mContactResolver.acquireContentProviderClient(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            Cursor mCursor = mProviderClient.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.LABEL},
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0",
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            // Add data to contact list
            if (mCursor != null && mCursor.getCount() > 0) {
                while (mCursor.moveToNext()) {
                    String mLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mActivity.getResources(), mCursor.getInt(4), mCursor.getString(5)).toString();
                    mContactList.add(new ContactPickerDataPair(
                            mCursor.getString(0),
                            mCursor.getString(1),
                            mCursor.getString(2),
                            mCursor.getString(3),
                            mLabel,
                            0));
                }
            }
            mCursor.close();
            mProviderClient.release();

            // Alphabetically group the list
            char mPrev = ' ';
            for (int mCount = 0; mCount < mContactList.size(); mCount++) {
                ContactPickerDataPair mPair = mContactList.get(mCount);
                if (mPair.getDisplayName().charAt(0) != mPrev) {
                    mContactList.add(mCount, new ContactPickerDataPair(null, mPair.getDisplayName().substring(0, 1).toUpperCase(), null, null, null, 1));
                    mPrev = mPair.getDisplayName().charAt(0);
                    mCount++;
                }
            }
        } catch (Exception mException) {
            Log.d("BLOKET_LOGS", "ContactPickerTask: Error fetching contacts for contact picker, Details: " + mException.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void mVoid) {
        mResponse.onTaskCompletion(mContactList);
    }

    public interface ContactsResponse {

        void onTaskCompletion(ArrayList<ContactPickerDataPair> mContactList);

    }
}