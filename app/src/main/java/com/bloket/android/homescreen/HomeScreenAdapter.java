package com.bloket.android.homescreen;

import com.bloket.android.modules.blacklist.BlacklistFragment;
import com.bloket.android.modules.calllogs.CallLogsFragment;
import com.bloket.android.modules.contacts.ContactsFragment;
import com.bloket.android.modules.dialer.DialerFragment;
import com.bloket.android.modules.whitelist.WhitelistFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeScreenAdapter extends FragmentPagerAdapter {

    HomeScreenAdapter(FragmentManager mFragmentManager) {
        super(mFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DialerFragment.newInstance();
            case 1:
                return ContactsFragment.newInstance();
            case 2:
                return CallLogsFragment.newInstance();
            case 3:
                return BlacklistFragment.newInstance();
            case 4:
                return WhitelistFragment.newInstance();
            default:
                return DialerFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}