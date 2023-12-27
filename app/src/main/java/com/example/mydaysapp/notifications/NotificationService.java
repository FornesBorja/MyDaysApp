package com.example.mydaysapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.mydaysapp.MainActivity;
import com.example.mydaysapp.R;

import android.app.Notification;
import android.app.NotificationChannel;

import android.os.Build;


import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "MyNotificationChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Crea el canal de notificación (necesario para versiones de Android >= Oreo)
        createNotificationChannel();

        // Código a ejecutar cuando se dispara la notificación
        Toast.makeText(this, "¡Es hora de tu notificación!", Toast.LENGTH_SHORT).show();

        // Puedes personalizar la notificación aquí
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notificación Programada")
                .setContentText("¡Es hora de tu notificación!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Se muestra la notificación
        notificationManager.notify(0, builder.build());

        // El servicio no se reiniciará automáticamente si es destruido
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Este servicio no proporciona enlace a otros componentes
        return null;
    }

    // Crea el canal de notificación para versiones de Android >= Oreo
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Recordatorio";
            String description = "Tienes que tomarte una pastilla";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }
    }

}
