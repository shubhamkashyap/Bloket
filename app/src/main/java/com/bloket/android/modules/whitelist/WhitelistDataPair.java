package com.bloket.android.modules.whitelist;

public class WhitelistDataPair {

    private int mId, mType, mGroupId;
    private String mGroupName, mContactId, mMatchData, mRangeData, mStartTime, mEndTime;
    private String mDisplayName, mPhotoUri;

    public WhitelistDataPair(int mId, int mType, int mGroupId, String mGroupName, String mContactId, String mMatchData, String mRangeData, String mStartTime, String mEndTime) {
        this.mId = mId;
        this.mType = mType;
        this.mGroupId = mGroupId;
        this.mGroupName = mGroupName;
        this.mContactId = mContactId;
        this.mMatchData = mMatchData;
        this.mRangeData = mRangeData;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
    }

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public String getContactId() {
        return mContactId;
    }

    public String getMatchData() {
        return mMatchData;
    }

    public String getRangeData() {
        return mRangeData;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    void setDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    void setPhotoUri(String mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }
}
