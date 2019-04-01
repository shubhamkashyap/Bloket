package com.bloket.android.homescreen;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.bloket.android.R;
import com.bloket.android.modules.blacklist.BlacklistFragment;
import com.bloket.android.modules.contacts.ContactsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class HomeScreenListener implements View.OnClickListener {
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;

    HomeScreenListener(Activity mActivity, FragmentManager mFragmentManager, ViewPager mViewPager) {
        this.mActivity = mActivity;
        this.mFragmentManager = mFragmentManager;
        this.mViewPager = mViewPager;
    }

    @Override
    public void onClick(View mView) {
        Fragment mFragment = mFragmentManager.findFragmentById(R.id.mpViewPager);
        switch (mViewPager.getCurrentItem()) {
            case 1:
                ((ContactsFragment) mFragment).fabClickAction();
                break;

            case 3:
                ((BlacklistFragment) mFragment).fabClickAction();
                break;

            case 4:
                break;

            default:
                Toast.makeText(mActivity, "Invalid " + mFragment.getClass(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
