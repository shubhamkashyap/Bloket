package com.bloket.android.utilities.picker;

import android.net.Uri;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bloket.android.R;
import com.l4digital.fastscroll.FastScroller;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactPickerAdapter extends RecyclerView.Adapter implements FastScroller.SectionIndexer, Filterable {

    private ArrayList<ContactPickerDataPair> mContactsList;
    private ArrayList<ContactPickerDataPair> mFilteredList;
    private ContactSelectListener mListener;
    private String mSearchText = "";
    private StyleSpan mBoldSpan;

    ContactPickerAdapter(ArrayList<ContactPickerDataPair> mContactsList, ContactSelectListener mListener) {
        this.mContactsList = mContactsList;
        this.mFilteredList = mContactsList;
        this.mListener = mListener;
        this.mBoldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
    }

    @Override
    public int getItemViewType(int mPosition) {
        return mFilteredList.get(mPosition).getRowType();
    }

    @SuppressWarnings("NullableProblems")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup mParent, int mViewType) {
        if (mViewType == 0) {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.ac_contact_picker_row, mParent, false);
            return new ContactViewHolder(mView);
        } else {
            View mView = LayoutInflater.from(mParent.getContext()).inflate(R.layout.ac_contact_picker_sec, mParent, false);
            return new HeaderViewHolder(mView);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int mPosition) {
        if (mHolder instanceof ContactViewHolder) {
            ContactViewHolder mContactsHolder = ((ContactViewHolder) mHolder);
            ContactPickerDataPair mDataPair = mFilteredList.get(mPosition);

            // Set contact image
            if (mDataPair.isSelected()) {
                mContactsHolder.mContactImage.setImageResource(R.drawable.ic_action_check);
                mContactsHolder.mContactFirstLetter.setVisibility(View.INVISIBLE);
            } else {
                if (mDataPair.getPhotoUri() == null) {
                    mContactsHolder.mContactFirstLetter.setVisibility(View.VISIBLE);
                    mContactsHolder.mContactFirstLetter.setText(mDataPair.getDisplayName().substring(0, 1));
                    mContactsHolder.mContactImage.setImageResource(R.drawable.ic_contact_default);
                } else {
                    mContactsHolder.mContactFirstLetter.setVisibility(View.INVISIBLE);
                    mContactsHolder.mContactImage.setImageURI(Uri.parse(mDataPair.getPhotoUri()));
                }
            }

            // Highlight filtered display name
            String mDisplayName = mDataPair.getDisplayName();
            String mLowerCaseName = mDisplayName.toLowerCase();
            if (mLowerCaseName.contains(mSearchText)) {
                int mPosStr = mLowerCaseName.indexOf(mSearchText);
                int mPosEnd = mPosStr + mSearchText.length();
                Spannable mSpanString = Spannable.Factory.getInstance().newSpannable(mDisplayName);
                mSpanString.setSpan(mBoldSpan, mPosStr, mPosEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                mContactsHolder.mContactName.setText(mSpanString);
            } else {
                mContactsHolder.mContactName.setText(mDisplayName);
            }

            mContactsHolder.mPhoneNumber.setText(mDataPair.getPhoneNumber());
            mContactsHolder.mPhoneLabel.setText(mDataPair.getPhoneLabel());
            mContactsHolder.mView.setOnClickListener(mView -> {
                setItemSelected(mDataPair.getPhoneNumberDigits());
                if (mListener != null) {
                    mListener.onContactSelected(mDataPair, getSelectedContactsCount());
                }
                notifyDataSetChanged();
            });
        } else {
            if (mPosition >= 0)
                ((HeaderViewHolder) mHolder).mHeaderText.setText(mFilteredList.get(mPosition).getDisplayName());
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mFilteredList.get(position).getDisplayName().substring(0, 1);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence mCharSequence) {
                mSearchText = mCharSequence.toString().toLowerCase();
                if (mSearchText.isEmpty()) {
                    mFilteredList = mContactsList;
                } else {
                    ArrayList<ContactPickerDataPair> mList = new ArrayList<>();
                    for (ContactPickerDataPair mPair : mContactsList) {
                        if (mPair.getRowType() == 0 && mPair.getSearchableText().toLowerCase().contains(mSearchText))
                            mList.add(mPair);
                    }
                    mFilteredList = mList;
                }
                FilterResults mFilteredResults = new FilterResults();
                mFilteredResults.values = mFilteredList;
                return mFilteredResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ContactPickerDataPair>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private int getItemPosition(String mNumber) {
        int mPosition = 0;
        for (ContactPickerDataPair mDataPair : mFilteredList) {
            if (mDataPair.getPhoneNumberDigits().equals(mNumber)) return mPosition;
            mPosition++;
        }
        return -1;
    }

    private void setItemSelected(String mNumber) {
        int mPosition = getItemPosition(mNumber);
        mFilteredList.get(mPosition).setSelected(!mFilteredList.get(mPosition).isSelected());
    }

    void setContactPreSelected(String mContactId, String mPhoneNumber) {
        int mPosition = getItemPosition(mPhoneNumber.replaceAll("[^0-9]", ""));
        if (mPosition < 0 || !mFilteredList.get(mPosition).getContactId().equals(mContactId))
            return;
        mFilteredList.get(mPosition).setSelected(!mFilteredList.get(mPosition).isSelected());
    }

    void setContactAllSelected(boolean isAll) {
        for (ContactPickerDataPair mDataPair : mContactsList) {
            if (mDataPair.getContactId() != null) mDataPair.setSelected(isAll);
            if (mListener != null) {
                mListener.onContactSelected(mDataPair, getSelectedContactsCount());
            }
        }
        notifyDataSetChanged();
    }

    int getSelectedContactsCount() {
        return ((getSelectedContacts() != null) ? getSelectedContacts().size() : 0);
    }

    ArrayList<ContactPickerDataPair> getSelectedContacts() {
        ArrayList<ContactPickerDataPair> selectedContacts = new ArrayList<>();
        for (ContactPickerDataPair mContact : mContactsList) {
            if (mContact.isSelected()) {
                selectedContacts.add(mContact);
            }
        }
        return selectedContacts;
    }

    interface ContactSelectListener {
        void onContactSelected(ContactPickerDataPair mContact, int mTotalSelectedContacts);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CircularImageView mContactImage;
        TextView mContactFirstLetter, mContactName, mPhoneNumber, mPhoneLabel;

        ContactViewHolder(View mView) {
            super(mView);
            this.mView = mView;
            mContactImage = mView.findViewById(R.id.ivContactImage);
            mContactFirstLetter = mView.findViewById(R.id.tvContactFirstLetter);
            mContactName = mView.findViewById(R.id.tvContactName);
            mPhoneNumber = mView.findViewById(R.id.tvPhoneNumber);
            mPhoneLabel = mView.findViewById(R.id.tvPhoneLabel);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mHeaderText;

        HeaderViewHolder(View mView) {
            super(mView);
            mHeaderText = mView.findViewById(R.id.tvHeaderText);
        }
    }
}