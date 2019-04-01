package com.bloket.android.utilities.helpers.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bloket.android.R;
import com.google.android.material.snackbar.Snackbar;

public class UIHelper {

    private static final int SNACKBAR_TEXT_ID = 0x7f0c006b;


    public static int dpToPx(final Context mContext, final float mDensityPixel) {
        final float mScale = mContext.getResources().getDisplayMetrics().density;
        return (int) ((mDensityPixel * mScale) + 0.5f);
    }

    @SuppressLint("PrivateResource")
    public static void showImageSnack(Activity mActivity, String mMessage) {
        Snackbar mSnackbar = Snackbar.make(mActivity.findViewById(android.R.id.content), mMessage, Snackbar.LENGTH_LONG);
        try {
            View mSnackLayout = mSnackbar.getView();
            TextView mTextView = mSnackLayout.findViewById(SNACKBAR_TEXT_ID);
            mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tab_blacklist, 0, 0, 0);
            mTextView.setCompoundDrawablePadding(dpToPx(mActivity, 16));
            mTextView.setGravity(Gravity.CENTER_VERTICAL);
        } catch (Exception mException) {
            Log.d("BLOKET_LOGS", "UIHelper: Error setting icon to snackbar.\n" + mException.toString());
        }
        mSnackbar.show();
    }

    public static void showPermissionSnack(Activity mActivity, String mMessage) {
        Snackbar mSnackBar = Snackbar
                .make(mActivity.findViewById(android.R.id.content), "Please allow permission to " + mMessage + ".", Snackbar.LENGTH_LONG)
                .setAction("ALLOW", mTempView -> {
                    Intent mIntent = new Intent();
                    mIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri mUri = Uri.fromParts("package", mActivity.getPackageName(), null);
                    mIntent.setData(mUri);
                    mActivity.startActivity(mIntent);
                });
        try {
            View mSnackLayout = mSnackBar.getView();
            TextView mTextView = mSnackLayout.findViewById(SNACKBAR_TEXT_ID);
            mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tab_whitelist, 0, 0, 0);
            mTextView.setCompoundDrawablePadding(UIHelper.dpToPx(mActivity, 16));
            mTextView.setGravity(Gravity.CENTER_VERTICAL);
        } catch (Exception mException) {
            Log.d("BLOKET_LOGS", "UIHelper: Error setting icon to snackbar, Error :: " + mException.toString());
        }
        mSnackBar.show();
    }

    public static Dialog makeProgressDialog(Context mContext) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(mContext).inflate(R.layout.dg_generic_pre, null);
        Dialog mProgressDialog = new Dialog(mContext);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(mContentView);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }

    public static void hideSoftKeyboard(Context mContext, View mView) {
        InputMethodManager mManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (mManager != null) mManager.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }
}
