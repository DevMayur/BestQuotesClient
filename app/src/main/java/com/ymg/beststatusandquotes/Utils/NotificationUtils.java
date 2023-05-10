package com.ymg.beststatusandquotes.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ymg.beststatusandquotes.Activity.MainActivity;
import com.ymg.beststatusandquotes.Activity.QuoteActivity;
import com.ymg.beststatusandquotes.R;

public class NotificationUtils extends ContextWrapper
{

    private NotificationManager _notificationManager;
    private Context _context;

    String CHANNEL_ID = "notification channel";
    String TIMELINE_CHANNEL_NAME = "Timeline notification";
    private PrefManager prefManager;
    private SharedPreferences preferences;

    public NotificationUtils(Context base)
    {
        super(base);
        _context = base;
        prefManager = new PrefManager(_context);
        createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public NotificationCompat.Builder setNotification(String title, String body) {


        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = new Intent(this, QuoteActivity.class);
        intent.putExtra("yourpackage.notifyId", CHANNEL_ID);
        intent.putExtra("id", preferences.getInt("id", prefManager.getInt("quoteToday")));
        intent.putExtra("mode", "qteday");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_quotes)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_menu_quotes, "Click here", pIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TIMELINE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager()
    {
        if(_notificationManager == null)
        {
            _notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return _notificationManager;
    }

}
