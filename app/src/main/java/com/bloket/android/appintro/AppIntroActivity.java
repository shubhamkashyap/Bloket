package com.bloket.android.appintro;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;

import com.bloket.android.R;
import com.bloket.android.homescreen.HomeScreenActivity;

import androidx.annotation.Nullable;
import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class AppIntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);

        String mPermissions[];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPermissions = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS};
        } else {
            mPermissions = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS};
        }

        addSlide(new SlideFragmentBuilder()
                .title("Bloket")
                .description("\n\nBlocking made easy!")
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.im_intro_bloket)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Permissions")
                .description("\n\nBloket needs your\npermission to intercept\ncalls and messages.")
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .neededPermissions(mPermissions)
                .image(R.drawable.im_intro_permission)
                .build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isDefaultDialer())
            addSlide(new SlideFragmentBuilder()
                            .title("Default Handler")
                            .description("\n\nBloket needs to be your\ndefault call and SMS app to block\ncalls and messages.")
                            .backgroundColor(R.color.colorPrimary)
                            .buttonsColor(R.color.colorPrimaryDark)
                            .image(R.drawable.im_intro_success)
                            .build(),
                    new MessageButtonBehaviour(mView -> {
                        Intent mIntent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
                        mIntent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
                        startActivity(mIntent);
                    }, "Set Default"));
    }

    @Override
    public void onFinish() {
        super.onFinish();
        Intent mIntent = new Intent(AppIntroActivity.this, HomeScreenActivity.class);
        startActivity(mIntent);
    }

    private boolean isDefaultDialer() {
        TelecomManager mManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        if (mManager != null)
            return getPackageName().equals(mManager.getDefaultDialerPackage());
        return false;
    }
}
