package com.bloket.android.homescreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.bloket.android.R;
import com.bloket.android.appintro.AppIntroActivity;
import com.bloket.android.modules.blacklist.BlacklistFragment;
import com.bloket.android.modules.contacts.ContactsFragment;
import com.bloket.android.modules.whitelist.WhitelistFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HomeScreenActivity extends AppCompatActivity {


    private MaterialMenuDrawable mMaterialMenu;
    private FloatingActionButton mFloatingButton;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);

        // Start app intro
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("BL_PF_INTRO", false)) {
            Intent intent = new Intent(this, AppIntroActivity.class); // Call the AppIntro java class
            startActivity(intent);

            // Write to preferences
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.putBoolean("BL_PF_INTRO", true);
            mEditor.apply();
            this.finish();
        } else {

            // Set up layout
            setContentView(R.layout.ac_main_screen);

            // Set up toolbar
            Toolbar mToolbar = findViewById(R.id.mpToolbar);
            if (mToolbar != null) setSupportActionBar(mToolbar);

            // Set up hamburger menu
            mMaterialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
            mMaterialMenu.setTransformationDuration(350);
            if (mToolbar != null) mToolbar.setNavigationIcon(mMaterialMenu);

            // Floating action button
            mFloatingButton = findViewById(R.id.mpFabButton);

            // Set up viewpager
            setViewPager();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check and set as default handler
        setDefaultHandler();
    }

    @SuppressWarnings("ConstantConditions")
    private void setViewPager() {

        // Set up pager adapter
        mViewPager = findViewById(R.id.mpViewPager);
        HomeScreenAdapter mAdapter = new HomeScreenAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int mPosition, float mPosOffset, int mPosOffsetPixels) {
                hideSoftKeyboard(getApplicationContext(), mViewPager);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int mPosition) {
                switch (mPosition) {
                    case 1:
                        if (mFloatingButton != null) {
                            mFloatingButton.setImageResource(R.drawable.ic_action_add);
                            mFloatingButton.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (mFloatingButton != null) {
                            mFloatingButton.setImageResource(R.drawable.ic_action_plus);
                            mFloatingButton.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 4:
                        if (mFloatingButton != null) {
                            mFloatingButton.setImageResource(R.drawable.ic_action_plus);
                            mFloatingButton.setVisibility(View.VISIBLE);
                        }
                        break;

                    default:
                        if (mFloatingButton != null) mFloatingButton.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int mState) {
            }
        });

        // Set up tabs
        TabLayout mTabLayout = findViewById(R.id.mpTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_dialer);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_contacts);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_blocklogs);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_blacklist);
        mTabLayout.getTabAt(4).setIcon(R.drawable.ic_tab_whitelist);

        // Set up tab icon color
        mTabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorWhitePressed), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(2).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorWhitePressed), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(3).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorWhitePressed), PorterDuff.Mode.MULTIPLY);
        mTabLayout.getTabAt(4).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorWhitePressed), PorterDuff.Mode.MULTIPLY);

        // Set up tab listener
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab mTab) {
                mTab.getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab mTab) {
                mTab.getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorWhitePressed), PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onTabReselected(TabLayout.Tab mTab) {

            }
        });
    }

    private void hideSoftKeyboard(Context mContext, View mView) {
        InputMethodManager mManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (mManager != null) mManager.hideSoftInputFromWindow(mView.getWindowToken(), 0);

    }

    private void setDefaultHandler() {
        if (isDefaultDialer()) return;
        Intent mIntent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        mIntent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivity(mIntent);
    }

    private boolean isDefaultDialer() {
        TelecomManager mManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        if (mManager != null)
            return getPackageName().equals(mManager.getDefaultDialerPackage());
        return false;
    }

    public void fabClickAction(View mView) {
        try {
            if (mViewPager == null) return;
            String mFragmentTag = "android:switcher:" + R.id.mpViewPager + ":" + mViewPager.getCurrentItem();
            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(mFragmentTag);
            switch (mViewPager.getCurrentItem()) {
                case 1:
                    if (mFragment != null) ((ContactsFragment) mFragment).fabClickAction();
                    break;

                case 3:
                    if (mFragment != null) ((BlacklistFragment) mFragment).fabClickAction();
                    break;

                case 4:
                    if (mFragment != null) ((WhitelistFragment) mFragment).fabClickAction();
                    break;

                default:
                    break;
            }
        } catch (Exception mException) {
            Toast.makeText(this, "Oops!!! " + mViewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
        }
    }
}