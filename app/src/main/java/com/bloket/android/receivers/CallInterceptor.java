package com.bloket.android.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.bloket.android.utilities.helpers.database.DatabaseHelper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class CallInterceptor extends BroadcastReceiver {

    private DatabaseHelper mDbHelper;
    private SharedPreferences mPreferences;
    private int mId;

    @Override
    public void onReceive(Context mContext, Intent mIntent) {

        // Intercept calls
        if (mIntent.getAction() != null && !TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(mIntent.getAction())) {
            return;
        }

        // Do not proceed of blocking is disabled
        if (mPreferences == null)
            mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!mPreferences.getBoolean("BL_PF_BLOCK_CL_ENABLED", false))
            return;

        // Get phone number
        String mPhoneNumber = null;
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(mIntent.getStringExtra(TelephonyManager.EXTRA_STATE)))
            mPhoneNumber = mIntent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        // End call if number is blocked
        if (mPhoneNumber != null && isBlocked(mContext, mPhoneNumber)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    TelecomManager mTelecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
                    if (mTelecomManager != null) mTelecomManager.endCall();
                } else {
                    TelephonyManager mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    if (mTelephonyManager == null) return;

                    @SuppressLint("PrivateApi")
                    Method mTelephonyMethod = mTelephonyManager.getClass().getDeclaredMethod("getITelephony");
                    mTelephonyMethod.setAccessible(true);
                    ITelephony mTelephonyService = (ITelephony) mTelephonyMethod.invoke(mTelephonyManager);

                    // Get preferred way of blocking here
                    mTelephonyService.silenceRinger();
                    mTelephonyService.endCall();
                }

                // Send user defined message
                if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
                sendUserMessage(mPhoneNumber, mDbHelper.getBlacklistMessage(mId));

            } catch (Exception mException) {
                Log.d("BLOKET_LOGS", "CallInterceptor: Error:: " + mException);
            }
        }
    }

    private boolean isBlocked(Context mContext, String mPhoneNumber) {
        try {

            // Prepare searchable number
            String mSearchableNumber;
            PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
            DecimalFormat mFormatter = new DecimalFormat("#,#");
            if (mPhoneNumber.startsWith("+")) {
                Phonenumber.PhoneNumber mNumber = mPhoneUtil.parse(mPhoneNumber, "");
                mSearchableNumber = mFormatter.format(mNumber.getNationalNumber());
                mPhoneNumber = String.valueOf(mNumber.getNationalNumber());
            } else {
                mSearchableNumber = mFormatter.format(Long.valueOf(mPhoneNumber));
            }
            mSearchableNumber = mSearchableNumber.replaceAll(",", "%");

            // Check if number is in whitelist
            boolean isWhitelisted = isWhitelisted(mContext, mPhoneNumber, mSearchableNumber);

            // Check user preferences
            if (mPreferences == null)
                mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (mPreferences.getBoolean("BL_PF_BLOCK_ALL", false) && !isWhitelisted)
                return true;

            if (mPreferences.getBoolean("BL_PF_BLOCK_CONTACTS", false) && !isWhitelisted && isSavedContact(mContext, mPhoneNumber))
                return true;

            if (mPreferences.getBoolean("BL_PF_BLOCK_UNKNOWN", false) && !isWhitelisted && !isSavedContact(mContext, mPhoneNumber))
                return true;

            if (mPreferences.getBoolean("BL_PF_BLOCK_PRIVATE", false) && !isWhitelisted && isPrivateNumber(mPhoneNumber))
                return true;

            return !isWhitelisted && isBlacklisted(mContext, mPhoneNumber, mSearchableNumber);

        } catch (Exception mException) {
            Log.d("BLOKET_LOGS", "CallInterceptor: Error while checking if number is blocked");
        }
        return false;
    }

    private boolean isBlacklisted(Context mContext, String mPhoneNumber, String mSearchableNumber) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mId = mDbHelper.chkBlacklistData(mPhoneNumber, mSearchableNumber);
        return mDbHelper.chkBlacklistSchedule(mId);
    }

    private boolean isWhitelisted(Context mContext, String mPhoneNumber, String mSearchableNumber) {
        return false;
    }

    private boolean isSavedContact(Context mContext, String mPhoneNumber) {
        Uri mUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mPhoneNumber));
        try (Cursor mCursor = mContext.getContentResolver().query(mUri, new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null)) {
            if (mCursor != null && mCursor.moveToFirst()) return true;
        }
        return false;
    }

    private boolean isPrivateNumber(String mPhoneNumber) {
        return Long.valueOf(mPhoneNumber) < 0;
    }

    private void sendUserMessage(String mPhoneNumber, String mReplyMessage) {
        if (mReplyMessage.length() < 1) return;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mPhoneNumber, null, mReplyMessage, null, null);
    }
}