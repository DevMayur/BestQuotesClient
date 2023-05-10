package com.ymg.beststatusandquotes.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast

        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            _builder = _notificationUtils.setNotification("Quotes App", "Todays Quote of the day is here..");
        }
        _notificationUtils.getManager().notify(101, _builder.build());
    }
}
