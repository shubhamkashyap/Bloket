package com.bloket.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageInterceptor extends BroadcastReceiver {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onReceive(Context mContext, Intent mIntent) {

        // Intercept messages
        if (mIntent.getAction() != null && Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(mIntent.getAction())) {
            for (SmsMessage mMessage : Telephony.Sms.Intents.getMessagesFromIntent(mIntent)) {
                Log.d("BLOKET_LOGS", "New message intercepted in Bloket");
                Log.d("BLOKET_LOGS", "Sender: " + mMessage.getOriginatingAddress());
                Log.d("BLOKET_LOGS", "Message: " + mMessage.getMessageBody());
            }
        }
    }
}
