package com.bloket.android.utilities.picker;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bloket.android.R;
import com.bloket.android.utilities.helpers.ui.UIHelper;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ContactPickerActivity extends AppCompatActivity implements View.OnClickListener {

    private ContactPickerAdapter mAdapter;
    public static final String RESULT = "RESULT";
    private FastScrollRecyclerView mRecyclerView;
    private SearchView mSearchView;
    private String mSearchText = "";
    public static final int PICKER_REQUEST_CODE = 991;
    private static final int PERM_READ_CONTACTS = 0;
    private boolean mChoiceModeSingle;
    private Dialog mProgressDialog;
    private TextView mSelect, mFinish;
    private boolean mAllSelected;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.ac_contact_picker);

        // Set up toolbar
        Toolbar mToolbar = findViewById(R.id.tbToolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Contacts");
        }

        // Get choice mode
        mChoiceModeSingle = getIntent().getBooleanExtra("CHOICE_MODE", false);

        // Set up search view
        mSearchView = findViewById(R.id.svSearchView);
        View mSearchBackground = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        mSearchBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
        mSearchView.setQueryHint("Search contacts");
        mSearchView.setOnClickListener(mTempView -> mSearchView.setIconified(false));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String mSearchText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String mSearchText) {
                ContactPickerActivity.this.mSearchText = mSearchText;
                if (mAdapter != null) mAdapter.getFilter().filter(mSearchText);
                return false;
            }
        });

        // Set up controls
        mSelect = findViewById(R.id.tvSelect);
        mFinish = findViewById(R.id.tvFinish);
        mSelect.setVisibility(View.GONE);
        mFinish.setOnClickListener(this);
        mFinish.setEnabled(false);

        // In case of usage of select button
        // mSelect.setEnabled(!mChoiceModeSingle);

        // Set up contact list
        mRecyclerView = findViewById(R.id.rvRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show progress bar
        mProgressDialog = UIHelper.makeProgressDialog(this);
        mProgressDialog.show();

        // Load contact list
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERM_READ_CONTACTS);
        } else {
            loadContactList();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        switch (mItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();

            default:
                setResult(RESULT_CANCELED);
                finish();
        }
        return super.onOptionsItemSelected(mItem);
    }

    @Override
    public void onRequestPermissionsResult(int mRequestCode, @NonNull String[] mPermissions, @NonNull int[] mResult) {
        switch (mRequestCode) {
            case PERM_READ_CONTACTS:
                if (mResult.length > 0 && mResult[0] == PackageManager.PERMISSION_GRANTED)
                    loadContactList();
                else {
                    boolean mShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                    if (!mShowRationale)
                        UIHelper.showPermissionSnack(this, "read contacts");
                    else
                        UIHelper.showImageSnack(this, "Permission denied.");
                }
                break;

            default:
                break;
        }
        super.onRequestPermissionsResult(mRequestCode, mPermissions, mResult);
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.tvSelect:
                mAllSelected = !mAllSelected;
                if (mAdapter != null)
                    mAdapter.setContactAllSelected(mAllSelected);
                if (mAllSelected)
                    mSelect.setText(getString(R.string.cp_unselect_all));
                else
                    mSelect.setText(getString(R.string.cp_select_all));
                break;

            case R.id.tvFinish:
                closePicker();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSearchText.equals("")) {
            mSearchView.setQuery("", true);
            mSearchView.setIconified(true);
            mSearchView.clearFocus();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void loadContactList() {
        ContactPickerTask mAsyncTask = new ContactPickerTask(this, mContactList -> {
            mAdapter = new ContactPickerAdapter(mContactList, (mContact, mTotalSelectedContacts) -> {
                mFinish.setEnabled(mTotalSelectedContacts > 0);
                if (mTotalSelectedContacts > 0) {
                    mFinish.setText(getString(R.string.cp_finish_enabled, String.valueOf(mTotalSelectedContacts)));
                } else {
                    mFinish.setText(getString(R.string.cp_finish_disabled));
                }
                if (mChoiceModeSingle) closePicker();
            });
            mRecyclerView.setAdapter(mAdapter);
            mSearchView.setQueryHint("Search among your " + sizeContactList(mContactList) + " contact numbers");

            // Set pre selected contact
            if (!mChoiceModeSingle) {
                ArrayList<ContactPickerIdNumber> mPreSelectList = getIntent().getParcelableArrayListExtra("PRE_SELECT_LIST");
                if (mPreSelectList != null) {
                    for (ContactPickerIdNumber mContact : mPreSelectList)
                        mAdapter.setContactPreSelected(mContact.getContactId(), mContact.getPhoneNumber());

                    int mSelectCount = mAdapter.getSelectedContactsCount();
                    if (mSelectCount > 0) {
                        mFinish.setEnabled(true);
                        mFinish.setText(getString(R.string.cp_finish_enabled, String.valueOf(mSelectCount)));
                    }
                }
            }

            // Close progress dialog
            if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
        });
        mAsyncTask.execute();
    }

    private int sizeContactList(ArrayList<ContactPickerDataPair> mContactList) {
        int mSize = 0;
        for (ContactPickerDataPair mContact : mContactList)
            if (mContact.getContactId() != null) mSize++;
        return mSize;
    }

    private void closePicker() {
        Intent mResultIntent = new Intent();
        mResultIntent.putExtra(RESULT, ContactPickerResult.buildResult(mAdapter.getSelectedContacts()));
        setResult(RESULT_OK, mResultIntent);
        finish();
    }
}