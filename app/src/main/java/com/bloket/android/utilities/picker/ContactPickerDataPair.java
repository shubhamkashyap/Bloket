package com.bloket.android.utilities.picker;

class ContactPickerDataPair {

    private String mContactId, mDisplayName, mPhotoUri, mPhoneNumber, mLabel;
    private int mRowType;
    private boolean mSelected;

    ContactPickerDataPair(String mContactId, String mDisplayName, String mPhotoUri, String mPhoneNumber, String mLabel, int mRowType) {
        this.mContactId = mContactId;
        this.mDisplayName = mDisplayName;
        this.mPhotoUri = mPhotoUri;
        this.mPhoneNumber = mPhoneNumber;
        this.mLabel = mLabel;
        this.mRowType = mRowType;
    }

    String getContactId() {
        return mContactId;
    }

    String getDisplayName() {
        return mDisplayName;
    }

    String getPhotoUri() {
        return mPhotoUri;
    }

    String getPhoneNumber() {
        return mPhoneNumber;
    }

    String getPhoneLabel() {
        return mLabel;
    }

    String getSearchableText() {
        return mDisplayName + " " + mPhoneNumber.replaceAll("[^0-9]", "");
    }

    String getPhoneNumberDigits() {
        if (mPhoneNumber == null) return "";
        return mPhoneNumber.replaceAll("[^0-9]", "");
    }

    int getRowType() {
        return mRowType;
    }

    boolean isSelected() {
        return mSelected;
    }

    void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}