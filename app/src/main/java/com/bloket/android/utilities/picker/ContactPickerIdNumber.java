package com.bloket.android.utilities.picker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class ContactPickerIdNumber implements Parcelable {

    public static final Creator<ContactPickerIdNumber> CREATOR = new Creator<ContactPickerIdNumber>() {
        @Override
        public ContactPickerIdNumber createFromParcel(Parcel mParcel) {
            return new ContactPickerIdNumber(mParcel);
        }

        @Override
        public ContactPickerIdNumber[] newArray(int mSize) {
            return new ContactPickerIdNumber[mSize];
        }
    };
    private String mContactId, mPhoneNumber;

    public ContactPickerIdNumber(String mContactId, String mPhoneNumber) {
        this.mContactId = mContactId;
        this.mPhoneNumber = mPhoneNumber;
    }

    private ContactPickerIdNumber(Parcel mParcel) {
        mContactId = mParcel.readString();
        mPhoneNumber = mParcel.readString();
    }

    @Override
    public boolean equals(@Nullable Object mObject) {
        return mObject instanceof ContactPickerIdNumber &&
                this.mContactId != null && this.mContactId.equals(((ContactPickerIdNumber) mObject).mContactId) &&
                this.mPhoneNumber != null && this.mPhoneNumber.equals(((ContactPickerIdNumber) mObject).mPhoneNumber);
    }

    @Override
    public void writeToParcel(Parcel mParcel, int mFlags) {
        mParcel.writeString(mContactId);
        mParcel.writeString(mPhoneNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getContactId() {
        return mContactId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }
}