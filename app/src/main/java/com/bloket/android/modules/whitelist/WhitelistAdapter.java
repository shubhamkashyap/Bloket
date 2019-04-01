package com.bloket.android.modules.whitelist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloket.android.R;
import com.bloket.android.utilities.helpers.database.DatabaseHelper;
import com.bloket.android.utilities.helpers.ui.UIHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.sapereaude.maskedEditText.MaskedEditText;

public class WhitelistAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<WhitelistDataPair> mList;
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;
    private DatabaseHelper mDbHelper;
    private int mRotationAngle = 0;


    WhitelistAdapter(Context mContext, ArrayList<WhitelistDataPair> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mContentResolver = mContext.getContentResolver();
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mDbHelper = new DatabaseHelper(mContext);
    }

    @Override
    public int getItemViewType(int mPosition) {
        if (mList.isEmpty()) {
            return 0;
        } else {
            return mList.get(mPosition).getType();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup mParent, int mViewType) {
        if (mViewType == 0) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_empty, mParent, false);
            return new EmptyViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_CONTACT) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new ContactViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_NUMBER) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new NumberViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_STARTS) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new StartsViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_ENDS) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new EndsViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_CONTAINS) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new ContainsViewHolder(mView);
        } else if (mViewType == DatabaseHelper.TYPE_RANGE) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new RangeViewHolder(mView);
        } else {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_main_screen_blacklist_row, mParent, false);
            return new ContactViewHolder(mView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int mPosition) {
        if (mHolder instanceof ContactViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            ContactViewHolder mDataHolder = ((ContactViewHolder) mHolder);

            String mContactName = mDataPair.getDisplayName();
            if (mContactName == null) {
                mContactName = getContactName(mDataPair.getContactId());
                mDataPair.setDisplayName(mContactName);
            }
            String mContactImage = mDataPair.getPhotoUri();
            if (mContactImage == null) {
                mContactImage = getContactImage(mDataPair.getContactId());
                mDataPair.setPhotoUri(mContactImage);
            }

            mDataHolder.mContactName.setText(mContactName);
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            if (mContactImage == null && mContactName != null) {
                mDataHolder.mContactLetter.setVisibility(View.VISIBLE);
                mDataHolder.mContactLetter.setText(mContactName.substring(0, 1));
                mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            } else {
                mDataHolder.mContactLetter.setVisibility(View.INVISIBLE);
                mDataHolder.mContactImage.setImageURI(Uri.parse(mContactImage));
            }
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_CONTACT, mDataPair.getDisplayName(), mDataPair.getMatchData()));
        }

        if (mHolder instanceof NumberViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            NumberViewHolder mDataHolder = ((NumberViewHolder) mHolder);
            mDataHolder.mPhoneNumber.setVisibility(View.GONE);
            mDataHolder.mContactName.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText(mDataPair.getMatchData().substring(0, 1));
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_NUMBER, "Number", mDataPair.getMatchData()));
        }

        if (mHolder instanceof StartsViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            StartsViewHolder mDataHolder = ((StartsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number starts with");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText("X..");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_STARTS, "Starts with", mDataPair.getMatchData()));
        }

        if (mHolder instanceof EndsViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            EndsViewHolder mDataHolder = ((EndsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number ends with");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText("..X");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_ENDS, "Ends with", mDataPair.getMatchData()));
        }

        if (mHolder instanceof ContainsViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            ContainsViewHolder mDataHolder = ((ContainsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number contains");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText(".X.");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_CONTAINS, "Contains", mDataPair.getMatchData()));
        }

        if (mHolder instanceof RangeViewHolder) {
            WhitelistDataPair mDataPair = mList.get(mPosition);
            RangeViewHolder mDataHolder = ((RangeViewHolder) mHolder);
            mDataHolder.mContactName.setText("Range of number");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData() + " - " + mDataPair.getRangeData());
            mDataHolder.mContactLetter.setText("X.X");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition, DatabaseHelper.TYPE_RANGE, mDataPair.getDisplayName(), mDataPair.getMatchData() + " - " + mDataPair.getRangeData()));
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        } else {
            return mList.size();
        }
    }

    private String getContactName(String mContactId) {
        String mContactName = "";
        final Cursor mCursor = mContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{mContactId}, null);
        if (mCursor != null && mCursor.moveToFirst())
            mContactName = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        if (mCursor != null) mCursor.close();
        return mContactName;
    }

    private String getContactImage(String mContactId) {
        String mContactImage = "";
        final Cursor mCursor = mContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts.PHOTO_URI},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{mContactId}, null);
        if (mCursor != null && mCursor.moveToFirst())
            mContactImage = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

        if (mCursor != null) mCursor.close();
        return mContactImage;
    }

    private void removeListItem(int mPosition) {
        mList.remove(mPosition);
        notifyDataSetChanged();
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void showOptionsDialog(final int mPosition, final int mType, final String mTitle, final String mDescription) {
        View mContentView = LayoutInflater.from(mContext).inflate(R.layout.dg_generic_option, null);
        final Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(mContentView);

        TextView mTitleView = mDialog.findViewById(R.id.tvTitle);
        mTitleView.setText(mTitle);
        TextView mDescriptionView = mDialog.findViewById(R.id.tvDescription);
        mDescriptionView.setText(mDescription);

        TextView mSetTime = mContentView.findViewById(R.id.tvSetTime);
        mSetTime.setOnClickListener(mView -> {
            setAllowSchedule(mPosition);
            mDialog.dismiss();
        });

        TextView mDeleteItem = mContentView.findViewById(R.id.tvDeleteItem);
        mDeleteItem.setOnClickListener(mView -> {
            switch (mType) {
                case DatabaseHelper.TYPE_CONTACT:
                    removeTypeContact(mPosition);
                    break;

                case DatabaseHelper.TYPE_NUMBER:
                    removeTypeNumber(mPosition);
                    break;

                case DatabaseHelper.TYPE_STARTS:
                    removeTypeStarts(mPosition);
                    break;

                case DatabaseHelper.TYPE_ENDS:
                    removeTypeEnds(mPosition);
                    break;

                case DatabaseHelper.TYPE_CONTAINS:
                    removeTypeContains(mPosition);
                    break;

                case DatabaseHelper.TYPE_RANGE:
                    removeTypeRange(mPosition);
                    break;

                default:
                    break;
            }
            mDialog.dismiss();
        });
        mDialog.show();
    }

    @SuppressWarnings("ConstantConditions")
    private void setAllowSchedule(final int mPosition) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(mContext).inflate(R.layout.dg_generic_set_time, null, false);
        final Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(mContentView);
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);

        String mSchedule[] = mDbHelper.getWhitelistSchedule(mList.get(mPosition).getId());
        MaskedEditText mScheduleStart = mContentView.findViewById(R.id.etScheduleStart);
        MaskedEditText mScheduleEnd = mContentView.findViewById(R.id.etScheduleEnd);
        mScheduleStart.setText((mSchedule == null || mSchedule[0].equals("")) ? "" : mFormat.format(Long.valueOf(mSchedule[0])));
        mScheduleEnd.setText((mSchedule == null || mSchedule[1].equals("")) ? "" : mFormat.format(Long.valueOf(mSchedule[1])));

        Button mNegative = mContentView.findViewById(R.id.btNegative);
        mNegative.setOnClickListener(mButtonView -> {
            UIHelper.hideSoftKeyboard(mContext, mNegative);
            mDialog.dismiss();
        });

        Button mPositive = mContentView.findViewById(R.id.btPositive);
        mPositive.setOnClickListener(mButtonView -> {
            String mTempStr = mScheduleStart.getText().toString();
            String mTempEnd = mScheduleEnd.getText().toString();
            if (mTempStr.equals("dd-MM-yyyy HH:mm") && mTempEnd.equals("dd-MM-yyyy HH:mm")) {
                if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
                mDbHelper.updWhitelistSchedule(mList.get(mPosition).getId(), "", "");
                UIHelper.hideSoftKeyboard(mContext, mPositive);
                mDialog.dismiss();
                return;
            }

            try {
                long mDateStr = mFormat.parse(mTempStr).getTime();
                long mDateEnd = mFormat.parse(mTempEnd).getTime();
                if (mDateStr >= mDateEnd) {
                    mScheduleEnd.setError("Entry a valid date & time range");
                    return;
                }

                if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
                mDbHelper.updWhitelistSchedule(mList.get(mPosition).getId(), String.valueOf(mDateStr), String.valueOf(mDateEnd));
                UIHelper.hideSoftKeyboard(mContext, mPositive);
                mDialog.dismiss();
            } catch (Exception mException) {
                mScheduleEnd.setError("Entry a valid date & time");
            }
        });
        mDialog.show();
    }

    private void removeTypeContact(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistContact(Integer.valueOf(mList.get(mPosition).getContactId()), mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeNumber(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistNumber(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeStarts(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistStarts(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeEnds(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistEnds(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeContains(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistContains(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeRange(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delWhitelistRange(mList.get(mPosition).getMatchData(), mList.get(mPosition).getRangeData());
        removeListItem(mPosition);
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        EmptyViewHolder(View mView) {
            super(mView);
        }
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        ContactViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        NumberViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }

    class StartsViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        StartsViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }

    class EndsViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        EndsViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }

    class ContainsViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        ContainsViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }

    class RangeViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mContactImage;
        ImageView mDeleteItem;
        TextView mContactLetter, mContactName, mPhoneNumber;

        RangeViewHolder(View mView) {
            super(mView);
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactLetter = mView.findViewById(R.id.tvContactLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mDeleteItem = mView.findViewById(R.id.ivOptions);
        }
    }
}