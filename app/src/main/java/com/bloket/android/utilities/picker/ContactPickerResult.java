package com.bloket.android.utilities.picker;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ContactPickerResult implements Parcelable {

    public static final Creator<ContactPickerResult> CREATOR = new Creator<ContactPickerResult>() {
        @Override
        public ContactPickerResult createFromParcel(Parcel mParcel) {
            return new ContactPickerResult(mParcel);
        }

        @Override
        public ContactPickerResult[] newArray(int mSize) {
            return new ContactPickerResult[mSize];
        }
    };
    private String mContactId, mDisplayName, mPhotoUri, mPhoneNumber, mLabel;

    private ContactPickerResult(ContactPickerDataPair mDataPair) {
        this.mContactId = mDataPair.getContactId();
        this.mDisplayName = mDataPair.getDisplayName();
        this.mPhotoUri = mDataPair.getPhotoUri();
        this.mPhoneNumber = mDataPair.getPhoneNumber();
        this.mLabel = mDataPair.getPhoneLabel();
    }

    private ContactPickerResult(Parcel mParcel) {
        this.mContactId = mParcel.readString();
        this.mDisplayName = mParcel.readString();
        this.mPhotoUri = mParcel.readString();
        this.mPhoneNumber = mParcel.readString();
        this.mLabel = mParcel.readString();
    }

    static ArrayList<ContactPickerResult> buildResult(ArrayList<ContactPickerDataPair> mContacts) {
        ArrayList<ContactPickerResult> mResult = new ArrayList<>();
        for (ContactPickerDataPair mContact : mContacts) {
            mResult.add(new ContactPickerResult(mContact));
        }
        return mResult;
    }

    public static ArrayList<ContactPickerResult> obtainResult(Intent mData) {
        return mData.getParcelableArrayListExtra(ContactPickerActivity.RESULT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel mParcel, int mFlags) {
        mParcel.writeString(this.mContactId);
        mParcel.writeString(this.mDisplayName);
        mParcel.writeString(this.mPhotoUri);
        mParcel.writeString(this.mPhoneNumber);
        mParcel.writeString(this.mLabel);
    }

    public String getContactId() {
        return mContactId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPhoneLabel() {
        return mLabel;
    }

    public String getPhoneNumberDigits() {
        return mPhoneNumber.replaceAll("[^0-9]", "");
    }
}