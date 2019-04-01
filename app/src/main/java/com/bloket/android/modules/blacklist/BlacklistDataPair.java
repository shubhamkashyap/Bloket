package com.bloket.android.modules.blacklist;

public class BlacklistDataPair {

    private int mId, mType, mGroupId;
    private String mGroupName, mContactId, mMatchData, mRangeData, mStartTime, mEndTime, mReplyMessage;
    private String mDisplayName, mPhotoUri;

    public BlacklistDataPair(int mId, int mType, int mGroupId, String mGroupName, String mContactId, String mMatchData, String mRangeData, String mStartTime, String mEndTime, String mReplyMessage) {
        this.mId = mId;
        this.mType = mType;
        this.mGroupId = mGroupId;
        this.mGroupName = mGroupName;
        this.mContactId = mContactId;
        this.mMatchData = mMatchData;
        this.mRangeData = mRangeData;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mReplyMessage = mReplyMessage;
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

    public String getReplyMessage() {
        return mReplyMessage;
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
