package com.demirgroup.skttakip.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.demirgroup.skttakip.R;
import com.demirgroup.skttakip.view.ProductsActivity;

public class notificationClass {
    private static final String CHANNEL_ID="CHANNEL_ID";
    private static final String CHANNEL_DESCRIPTION="sımple descrıptıon";
    private static final String CHANNEL_NAME="my Channel";

    public static void sendNotification(Context context , String productName){
        // raw klasöründeki ses dosyasının ismini belirtin (örneğin, my_sound.mp3)
        String soundFileName = "ringtones.mp3";
        // raw klasöründeki ses dosyasının URI'sini oluşturun
        int resID = context.getResources().getIdentifier(soundFileName, "raw", context.getPackageName());
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resID);

        RemoteViews customNotificationView = new RemoteViews(context.getPackageName(),R.layout.customnotificationview);
        customNotificationView.setTextViewText(R.id.bildirimContemtyetxt,productName);
        Intent buttonIntent = new Intent(context, ProductsActivity.class);
        PendingIntent buttonPendingIntent = PendingIntent.getActivity(context,0,buttonIntent,0);
        customNotificationView.setOnClickPendingIntent(R.id.notificationButtn,buttonPendingIntent);
        customNotificationView.setTextViewText(R.id.notificationButtn,"tıkla");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.setSound(soundUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build());
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        builder.setContentTitle("S:K:T: YAKLAŞTI");
        builder.setSmallIcon(R.drawable.logoskt);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setCustomContentView(customNotificationView);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        manager.notify(1,builder.build());
    }
}
