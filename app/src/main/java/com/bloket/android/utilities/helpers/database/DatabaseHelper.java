package com.bloket.android.utilities.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bloket.android.modules.blacklist.BlacklistDataPair;
import com.bloket.android.modules.groups.GroupsDataPair;
import com.bloket.android.modules.whitelist.WhitelistDataPair;
import com.bloket.android.utilities.picker.ContactPickerIdNumber;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database
    private static final String DB_NAME = "BloketDB";
    private static final int DB_VERSION = 1;

    // Tables
    private static final String TB_BLACKLIST = "Blacklist";
    private static final String TB_WHITELIST = "Whitelist";
    // Default values
    public static final int DEFAULT_GROUP_ID = -1;
    private static final String TB_GROUPLIST = "GroupList";
    // Columns
    private static final String CL_SURROGATE_ID = "ID";
    private static final String CL_GROUP_ID = "GroupId";
    private static final String CL_GROUP_NAME = "GroupName";
    private static final String CL_GROUP_DATA = "GroupData";
    private static final String CL_CONTACT_ID = "ContactId";
    private static final String CL_MATCH_DATA = "MatchData";
    private static final String CL_RANGE_DATA = "RangeData";
    private static final String CL_TIME_START = "TimeStart";
    private static final String CL_TIME_END = "TimeEnd";
    private static final String CL_ENTRY_TYPE = "EntryType";
    private static final String CL_TIMESTAMP = "Timestamp";

    // Types
    public static final int TYPE_CONTACT = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_GROUP = 3;
    public static final int TYPE_STARTS = 4;
    public static final int TYPE_ENDS = 5;
    public static final int TYPE_CONTAINS = 6;
    public static final int TYPE_RANGE = 7;
    private static final String CL_REPLY_MSG = "ReplyMessage";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase mDatabase) {

        // TODO: Option to block call and/or messages

        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TB_BLACKLIST + "("
                + CL_SURROGATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + CL_ENTRY_TYPE + " TINYINT,"
                + CL_GROUP_ID + " INTEGER,"
                + CL_GROUP_NAME + " TEXT,"
                + CL_CONTACT_ID + " TEXT,"
                + CL_MATCH_DATA + " TEXT,"
                + CL_RANGE_DATA + " TEXT,"
                + CL_TIME_START + " TEXT,"
                + CL_TIME_END + " TEXT,"
                + CL_REPLY_MSG + " TEXT,"
                + CL_TIMESTAMP + " TEXT )");

        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TB_WHITELIST + "("
                + CL_SURROGATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + CL_ENTRY_TYPE + " TINYINT,"
                + CL_GROUP_ID + " INTEGER,"
                + CL_GROUP_NAME + " TEXT,"
                + CL_CONTACT_ID + " TEXT,"
                + CL_MATCH_DATA + " TEXT,"
                + CL_RANGE_DATA + " TEXT,"
                + CL_TIME_START + " TEXT,"
                + CL_TIME_END + " TEXT,"
                + CL_TIMESTAMP + " TEXT )");

        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TB_GROUPLIST + "("
                + CL_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + CL_GROUP_NAME + " TEXT,"
                + CL_GROUP_DATA + " TEXT,"
                + CL_TIMESTAMP + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase mDatabase, int mOldVersion, int mNewVersion) {

    }

    // Blacklist methods
    public ArrayList<BlacklistDataPair> getBlacklistData() {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT * FROM " + TB_BLACKLIST + " ORDER BY " + CL_TIMESTAMP + " DESC";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            ArrayList<BlacklistDataPair> mData = new ArrayList<>();
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    mData.add(new BlacklistDataPair(
                            mCursor.getInt(0),
                            mCursor.getInt(1),
                            mCursor.getInt(2),
                            mCursor.getString(3),
                            mCursor.getString(4),
                            mCursor.getString(5),
                            mCursor.getString(6),
                            mCursor.getString(7),
                            mCursor.getString(8),
                            mCursor.getString(9)));
                } while (mCursor.moveToNext());
            }
            return mData;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public void putBlacklistData(int mEntryType, int mGroupId, String mGroupName, String mContactId, String mMatchData, String mRangeData, String mTimeStart, String mTimeEnd, String mReplyMessage) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_ENTRY_TYPE, mEntryType);
        mValues.put(CL_GROUP_ID, mGroupId);
        mValues.put(CL_GROUP_NAME, mGroupName);
        mValues.put(CL_CONTACT_ID, mContactId);
        mValues.put(CL_MATCH_DATA, mMatchData);
        mValues.put(CL_RANGE_DATA, mRangeData);
        mValues.put(CL_TIME_START, mTimeStart);
        mValues.put(CL_TIME_END, mTimeEnd);
        mValues.put(CL_REPLY_MSG, mReplyMessage);
        mValues.put(CL_TIMESTAMP, System.currentTimeMillis());
        mDatabase.insert(TB_BLACKLIST, null, mValues);
        mDatabase.close();
    }

    public int chkBlacklistData(String mPhoneNumber, String mSearchableNumber) {
        int mId = -1;
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        Cursor mCursor = null;
        try {
            // Matches entry type contact & number
            String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE (" + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " OR " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + ") AND " + CL_MATCH_DATA + " LIKE '%" + mSearchableNumber + "';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type starts
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND '" + mPhoneNumber + "' LIKE " + CL_MATCH_DATA + " || '%';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type ends
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND '" + mPhoneNumber + "' LIKE '%' || " + CL_MATCH_DATA + ";";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type contains
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND '" + mPhoneNumber + "' LIKE '%' || " + CL_MATCH_DATA + " || '%';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type range
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND '" + mPhoneNumber + "' BETWEEN " + CL_MATCH_DATA + " AND " + CL_RANGE_DATA + ";";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
        return mId;
    }

    public ArrayList<ContactPickerIdNumber> getBlacklistContacts() {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_CONTACT_ID + ", " + CL_MATCH_DATA + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " ORDER BY " + CL_TIMESTAMP + " DESC";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            ArrayList<ContactPickerIdNumber> mData = new ArrayList<>();
            if (mCursor != null && mCursor.moveToFirst()) {
                do mData.add(new ContactPickerIdNumber(mCursor.getString(0), mCursor.getString(1)));
                while (mCursor.moveToNext());
            }
            return mData;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public String[] getBlacklistSchedule(int mId) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_TIME_START + ", " + CL_TIME_END + " FROM " + TB_BLACKLIST + " WHERE " + CL_SURROGATE_ID + " = " + mId + ";";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            if (mCursor != null && mCursor.moveToFirst())
                return new String[]{mCursor.getString(0), mCursor.getString(1)};
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
        return null;
    }

    public String getBlacklistMessage(int mId) {
        String mMessage = "";
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_REPLY_MSG + " FROM " + TB_BLACKLIST + " WHERE " + CL_SURROGATE_ID + " = " + mId + ";";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            if (mCursor != null && mCursor.moveToFirst()) mMessage = mCursor.getString(0);
            return mMessage;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public void updBlacklistSchedule(int mId, String mStartTime, String mEndTime) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_TIME_START, mStartTime);
        mValues.put(CL_TIME_END, mEndTime);
        mDatabase.update(TB_BLACKLIST, mValues, CL_SURROGATE_ID + " = '" + mId + "'", null);
        mDatabase.close();
    }

    public void updBlacklistMessage(int mId, String mReplyMessage) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_REPLY_MSG, mReplyMessage);
        mDatabase.update(TB_BLACKLIST, mValues, CL_SURROGATE_ID + " = '" + mId + "'", null);
        mDatabase.close();
    }

    public boolean chkBlacklistSchedule(int mId) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_TIME_START + ", " + CL_TIME_END + " FROM " + TB_BLACKLIST + " WHERE " + CL_SURROGATE_ID + " = " + mId + ";";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            boolean mResult = true;
            long mCurrentTime = System.currentTimeMillis();
            if (mCursor != null && mCursor.moveToFirst()) {
                if (mCursor.getLong(0) > 0)
                    mResult = mCurrentTime >= mCursor.getLong(0) && mCurrentTime <= mCursor.getLong(1) + 999;
            }
            return mResult;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkBlacklistNumber(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkBlacklistStarts(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkBlacklistEnds(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkBlacklistContains(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkBlacklistRange(String mRangeFrom, String mRangeUpto) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND " + CL_MATCH_DATA + " = '" + mRangeFrom + "' AND " + CL_RANGE_DATA + " = '" + mRangeUpto + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public void delBlacklistContact(int mContactId, String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " AND " + CL_CONTACT_ID + " = " + mContactId + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delBlacklistNumber(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delBlacklistStarts(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delBlacklistEnds(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delBlacklistContains(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delBlacklistRange(String mStartRange, String mEndRange) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_BLACKLIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND " + CL_MATCH_DATA + " = '" + mStartRange + "' AND " + CL_RANGE_DATA + " = '" + mEndRange + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }


    // Whitelist methods
    public ArrayList<WhitelistDataPair> getWhitelistData() {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT * FROM " + TB_WHITELIST + " ORDER BY " + CL_TIMESTAMP + " DESC";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            ArrayList<WhitelistDataPair> mData = new ArrayList<>();
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    mData.add(new WhitelistDataPair(
                            mCursor.getInt(0),
                            mCursor.getInt(1),
                            mCursor.getInt(2),
                            mCursor.getString(3),
                            mCursor.getString(4),
                            mCursor.getString(5),
                            mCursor.getString(6),
                            mCursor.getString(7),
                            mCursor.getString(8)));
                } while (mCursor.moveToNext());
            }
            return mData;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public void putWhitelistData(int mEntryType, int mGroupId, String mGroupName, String mContactId, String mMatchData, String mRangeData, String mTimeStart, String mTimeEnd) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_ENTRY_TYPE, mEntryType);
        mValues.put(CL_GROUP_ID, mGroupId);
        mValues.put(CL_GROUP_NAME, mGroupName);
        mValues.put(CL_CONTACT_ID, mContactId);
        mValues.put(CL_MATCH_DATA, mMatchData);
        mValues.put(CL_RANGE_DATA, mRangeData);
        mValues.put(CL_TIME_START, mTimeStart);
        mValues.put(CL_TIME_END, mTimeEnd);
        mValues.put(CL_TIMESTAMP, System.currentTimeMillis());
        mDatabase.insert(TB_WHITELIST, null, mValues);
        mDatabase.close();
    }

    public int chkWhitelistData(String mPhoneNumber, String mSearchableNumber) {
        int mId = -1;
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        Cursor mCursor = null;
        try {
            // Matches entry type contact & number
            String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE (" + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " OR " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + ") AND " + CL_MATCH_DATA + " LIKE '%" + mSearchableNumber + "';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type starts
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND '" + mPhoneNumber + "' LIKE " + CL_MATCH_DATA + " || '%';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type ends
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND '" + mPhoneNumber + "' LIKE '%' || " + CL_MATCH_DATA + ";";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type contains
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND '" + mPhoneNumber + "' LIKE '%' || " + CL_MATCH_DATA + " || '%';";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);

            // Matches entry type range
            mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND '" + mPhoneNumber + "' BETWEEN " + CL_MATCH_DATA + " AND " + CL_RANGE_DATA + ";";
            mCursor = mDatabase.rawQuery(mQuery, null);
            if (mCursor != null && mCursor.moveToFirst()) return mCursor.getInt(0);
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
        return mId;
    }

    public ArrayList<ContactPickerIdNumber> getWhitelistContacts() {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_CONTACT_ID + ", " + CL_MATCH_DATA + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " ORDER BY " + CL_TIMESTAMP + " DESC";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            ArrayList<ContactPickerIdNumber> mData = new ArrayList<>();
            if (mCursor != null && mCursor.moveToFirst()) {
                do mData.add(new ContactPickerIdNumber(mCursor.getString(0), mCursor.getString(1)));
                while (mCursor.moveToNext());
            }
            return mData;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public String[] getWhitelistSchedule(int mId) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_TIME_START + ", " + CL_TIME_END + " FROM " + TB_WHITELIST + " WHERE " + CL_SURROGATE_ID + " = " + mId + ";";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            if (mCursor != null && mCursor.moveToFirst())
                return new String[]{mCursor.getString(0), mCursor.getString(1)};
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
        return null;
    }

    public void updWhitelistSchedule(int mId, String mStartTime, String mEndTime) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_TIME_START, mStartTime);
        mValues.put(CL_TIME_END, mEndTime);
        mDatabase.update(TB_WHITELIST, mValues, CL_SURROGATE_ID + " = '" + mId + "'", null);
        mDatabase.close();
    }

    public boolean chkWhitelistSchedule(int mId) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_TIME_START + ", " + CL_TIME_END + " FROM " + TB_WHITELIST + " WHERE " + CL_SURROGATE_ID + " = " + mId + ";";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            boolean mResult = true;
            long mCurrentTime = System.currentTimeMillis();
            if (mCursor != null && mCursor.moveToFirst()) {
                if (mCursor.getLong(0) > 0)
                    mResult = mCurrentTime >= mCursor.getLong(0) && mCurrentTime <= mCursor.getLong(1) + 999;
            }
            return mResult;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkWhitelistNumber(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkWhitelistStarts(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkWhitelistEnds(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkWhitelistContains(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public boolean chkWhitelistRange(String mRangeFrom, String mRangeUpto) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "SELECT " + CL_SURROGATE_ID + " FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND " + CL_MATCH_DATA + " = '" + mRangeFrom + "' AND " + CL_RANGE_DATA + " = '" + mRangeUpto + "';";
        Cursor mCursor = mDatabase.rawQuery(mQuery, null);
        try {
            return mCursor.getCount() > 0;
        } finally {
            if (mCursor != null) mCursor.close();
            mDatabase.close();
        }
    }

    public void delWhitelistContact(int mContactId, String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTACT + " AND " + CL_CONTACT_ID + " = " + mContactId + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delWhitelistNumber(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_NUMBER + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delWhitelistStarts(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_STARTS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delWhitelistEnds(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_ENDS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delWhitelistContains(String mPhoneNumber) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_CONTAINS + " AND " + CL_MATCH_DATA + " = '" + mPhoneNumber + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void delWhitelistRange(String mStartRange, String mEndRange) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        String mQuery = "DELETE FROM " + TB_WHITELIST + " WHERE " + CL_ENTRY_TYPE + " = " + TYPE_RANGE + " AND " + CL_MATCH_DATA + " = '" + mStartRange + "' AND " + CL_RANGE_DATA + " = '" + mEndRange + "';";
        mDatabase.execSQL(mQuery);
        mDatabase.close();
    }

    public void putGroupList(String mGroupName, String mGroupData) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put(CL_GROUP_NAME, mGroupName);
        mValues.put(CL_GROUP_DATA, mGroupData);
        mValues.put(CL_TIMESTAMP, System.currentTimeMillis());
        mDatabase.insert(TB_GROUPLIST, null, mValues);
        mDatabase.close();
    }

    public ArrayList<GroupsDataPair> getGroupList() {
        return null;
    }
}