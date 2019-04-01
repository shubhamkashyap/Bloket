package com.bloket.android.modules.blacklist;

import android.animation.ObjectAnimator;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

public class BlacklistAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<BlacklistDataPair> mList;
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;
    private DatabaseHelper mDbHelper;
    private int mRotationAngle = 0;


    BlacklistAdapter(Context mContext, ArrayList<BlacklistDataPair> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mContentResolver = mContext.getContentResolver();
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mDbHelper = new DatabaseHelper(mContext);
    }

    @Override
    public int getItemViewType(int mPosition) {
        if (mPosition == 0) {
            return 0;
        } else {
            return mList.get(mPosition - 1).getType();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup mParent, int mViewType) {
        if (mViewType == 0) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.fm_blacklist_preferences, mParent, false);
            return new PreferencesViewHolder(mView);
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
        if (mHolder instanceof PreferencesViewHolder) {
            PreferencesViewHolder mDataHolder = ((PreferencesViewHolder) mHolder);
            if (mPreferences == null)
                mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            mDataHolder.mEnableBlock.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_CL_ENABLED", false));
            mDataHolder.mBlockAll.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_ALL", false));
            mDataHolder.mBlockContacts.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_CONTACTS", false));
            mDataHolder.mBlockUnknown.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_UNKNOWN", false));
            mDataHolder.mBlockPrivate.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_PRIVATE", false));

            if (mDataHolder.mBlockAll.isChecked()) {
                mDataHolder.mBlockContacts.setChecked(true);
                mDataHolder.mBlockContacts.setEnabled(false);
                mDataHolder.mBlockUnknown.setChecked(true);
                mDataHolder.mBlockUnknown.setEnabled(false);
                mDataHolder.mBlockPrivate.setChecked(true);
                mDataHolder.mBlockPrivate.setEnabled(false);
            }
        }

        if (mHolder instanceof ContactViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
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
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_CONTACT, mDataPair.getDisplayName(), mDataPair.getMatchData()));
        }

        if (mHolder instanceof NumberViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
            NumberViewHolder mDataHolder = ((NumberViewHolder) mHolder);
            mDataHolder.mPhoneNumber.setVisibility(View.GONE);
            mDataHolder.mContactName.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText(mDataPair.getMatchData().substring(0, 1));
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_NUMBER, "Number", mDataPair.getMatchData()));
        }

        if (mHolder instanceof StartsViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
            StartsViewHolder mDataHolder = ((StartsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number starts with");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText("X..");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_STARTS, "Starts with", mDataPair.getMatchData()));
        }

        if (mHolder instanceof EndsViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
            EndsViewHolder mDataHolder = ((EndsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number ends with");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText("..X");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_ENDS, "Ends with", mDataPair.getMatchData()));
        }

        if (mHolder instanceof ContainsViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
            ContainsViewHolder mDataHolder = ((ContainsViewHolder) mHolder);
            mDataHolder.mContactName.setText("Number contains");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData());
            mDataHolder.mContactLetter.setText(".X.");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_CONTAINS, "Contains", mDataPair.getMatchData()));
        }

        if (mHolder instanceof RangeViewHolder) {
            BlacklistDataPair mDataPair = mList.get(mPosition - 1);
            RangeViewHolder mDataHolder = ((RangeViewHolder) mHolder);
            mDataHolder.mContactName.setText("Range of number");
            mDataHolder.mPhoneNumber.setText(mDataPair.getMatchData() + " - " + mDataPair.getRangeData());
            mDataHolder.mContactLetter.setText("X.X");
            mDataHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
            mDataHolder.mDeleteItem.setOnClickListener(mItemView -> showOptionsDialog(mPosition - 1, DatabaseHelper.TYPE_RANGE, mDataPair.getDisplayName(), mDataPair.getMatchData() + " - " + mDataPair.getRangeData()));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
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
            setBlockSchedule(mPosition);
            mDialog.dismiss();
        });

        TextView mSetMessage = mContentView.findViewById(R.id.tvSetMessage);
        mSetMessage.setOnClickListener(mView -> {
            setReplyMessage(mPosition);
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
    private void setBlockSchedule(final int mPosition) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(mContext).inflate(R.layout.dg_generic_set_time, null, false);
        final Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(mContentView);
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);

        String mSchedule[] = mDbHelper.getBlacklistSchedule(mList.get(mPosition).getId());
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
                mDbHelper.updBlacklistSchedule(mList.get(mPosition).getId(), "", "");
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
                mDbHelper.updBlacklistSchedule(mList.get(mPosition).getId(), String.valueOf(mDateStr), String.valueOf(mDateEnd));
                UIHelper.hideSoftKeyboard(mContext, mPositive);
                mDialog.dismiss();
            } catch (Exception mException) {
                mScheduleEnd.setError("Entry a valid date & time");
            }
        });
        mDialog.show();
    }

    private void setReplyMessage(final int mPosition) {
        @SuppressLint("InflateParams") View mContentView = LayoutInflater.from(mContext).inflate(R.layout.dg_generic_set_message, null, false);
        final Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(mContentView);
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        EditText mReplyMessage = mContentView.findViewById(R.id.etReplyMessage);
        mReplyMessage.setText(mDbHelper.getBlacklistMessage(mList.get(mPosition).getId()));
        Button mNegative = mContentView.findViewById(R.id.btNegative);
        mNegative.setOnClickListener(mButtonView -> {
            UIHelper.hideSoftKeyboard(mContext, mReplyMessage);
            mDialog.dismiss();
        });
        Button mPositive = mContentView.findViewById(R.id.btPositive);
        mPositive.setOnClickListener(mButtonView -> {
            String mMessage = mReplyMessage.getText().toString();
            if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
            mDbHelper.updBlacklistMessage(mList.get(mPosition).getId(), mMessage);
            UIHelper.hideSoftKeyboard(mContext, mReplyMessage);
            mDialog.dismiss();

        });
        mDialog.show();
    }

    private void removeTypeContact(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistContact(Integer.valueOf(mList.get(mPosition).getContactId()), mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeNumber(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistNumber(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeStarts(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistStarts(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeEnds(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistEnds(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeContains(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistContains(mList.get(mPosition).getMatchData());
        removeListItem(mPosition);
    }

    private void removeTypeRange(final int mPosition) {
        if (mDbHelper == null) mDbHelper = new DatabaseHelper(mContext);
        mDbHelper.delBlacklistRange(mList.get(mPosition).getMatchData(), mList.get(mPosition).getRangeData());
        removeListItem(mPosition);
    }

    class PreferencesViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mCallPrefs;
        LinearLayout mCallPrefItems;
        ImageView mCallPrefExpand;
        Switch mEnableBlock;
        CheckBox mBlockAll, mBlockContacts, mBlockUnknown, mBlockPrivate;

        PreferencesViewHolder(View mView) {
            super(mView);
            mEnableBlock = mView.findViewById(R.id.swEnableBlock);
            mEnableBlock.setOnClickListener(this::onClick);
            mCallPrefs = mView.findViewById(R.id.rlCallPrefs);
            mCallPrefs.setOnClickListener(this::onClick);
            mBlockAll = mView.findViewById(R.id.cbBlockAll);
            mBlockAll.setOnClickListener(this::onClick);
            mBlockContacts = mView.findViewById(R.id.cbBlockContacts);
            mBlockContacts.setOnClickListener(this::onClick);
            mBlockUnknown = mView.findViewById(R.id.cbBlockUnknown);
            mBlockUnknown.setOnClickListener(this::onClick);
            mBlockPrivate = mView.findViewById(R.id.cbBlockPrivate);
            mBlockPrivate.setOnClickListener(this::onClick);
            mCallPrefExpand = mView.findViewById(R.id.ivCallPrefExpand);
            mCallPrefItems = mView.findViewById(R.id.llCallPrefItems);
        }

        private void onClick(View mView) {
            if (mPreferences == null)
                mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor mEditor = mPreferences.edit();

            switch (mView.getId()) {
                case R.id.swEnableBlock:
                    if (mEnableBlock.isChecked()) {
                        mEditor.putBoolean("BL_PF_BLOCK_CL_ENABLED", true);
                    } else {
                        mEditor.putBoolean("BL_PF_BLOCK_CL_ENABLED", false);
                    }
                    mEditor.apply();
                    break;

                case R.id.rlCallPrefs:
                    ObjectAnimator mAnimator = ObjectAnimator.ofFloat(mCallPrefExpand, "rotation", mRotationAngle, mRotationAngle + 180);
                    mAnimator.setDuration(250);
                    if (mCallPrefItems.getVisibility() == View.GONE) {
                        mCallPrefItems.setVisibility(View.VISIBLE);
                        mAnimator.start();
                        mRotationAngle = (mRotationAngle + 180) % 360;
                    } else {
                        mCallPrefItems.setVisibility(View.GONE);
                        mAnimator.start();
                        mRotationAngle = (mRotationAngle + 180) % 360;
                    }

                    break;

                case R.id.cbBlockAll:
                    onAllChecked(mPreferences, mEditor);
                    mEditor.apply();
                    break;

                case R.id.cbBlockContacts:
                    if (mBlockContacts.isChecked()) {
                        mEditor.putBoolean("BL_PF_BLOCK_CONTACTS", true);
                    } else {
                        mEditor.putBoolean("BL_PF_BLOCK_CONTACTS", false);
                    }

                    if (mBlockUnknown.isChecked()) {
                        mBlockAll.setChecked(true);
                        onAllChecked(mPreferences, mEditor);
                    }
                    mEditor.apply();
                    break;

                case R.id.cbBlockUnknown:
                    if (mBlockUnknown.isChecked()) {
                        mEditor.putBoolean("BL_PF_BLOCK_UNKNOWN", true);
                    } else {
                        mEditor.putBoolean("BL_PF_BLOCK_UNKNOWN", false);
                    }

                    if (mBlockContacts.isChecked()) {
                        mBlockAll.setChecked(true);
                        onAllChecked(mPreferences, mEditor);
                    }
                    mEditor.apply();
                    break;

                case R.id.cbBlockPrivate:
                    if (mBlockPrivate.isChecked()) {
                        mEditor.putBoolean("BL_PF_BLOCK_PRIVATE", true);
                    } else {
                        mEditor.putBoolean("BL_PF_BLOCK_PRIVATE", false);
                    }
                    mEditor.apply();
                    break;
            }
        }

        private void onAllChecked(SharedPreferences mPreferences, SharedPreferences.Editor mEditor) {
            if (mBlockAll.isChecked()) {
                mBlockContacts.setChecked(true);
                mBlockContacts.setEnabled(false);
                mBlockUnknown.setChecked(true);
                mBlockUnknown.setEnabled(false);
                mBlockPrivate.setChecked(true);
                mBlockPrivate.setEnabled(false);
                mEditor.putBoolean("BL_PF_BLOCK_ALL", true);
            } else {
                mBlockContacts.setChecked(false);
                mBlockContacts.setEnabled(true);
                mBlockUnknown.setChecked(false);
                mBlockUnknown.setEnabled(true);
                mBlockPrivate.setChecked(mPreferences.getBoolean("BL_PF_BLOCK_PRIVATE", false));
                mBlockPrivate.setEnabled(true);
                mEditor.putBoolean("BL_PF_BLOCK_ALL", false);
                mEditor.putBoolean("BL_PF_BLOCK_CONTACTS", false);
                mEditor.putBoolean("BL_PF_BLOCK_UNKNOWN", false);
            }
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