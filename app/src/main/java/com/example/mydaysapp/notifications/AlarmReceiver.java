package com.example.mydaysapp.notifications;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String nombrePastilla = intent.getStringExtra("nombrePastilla");

            // Mostrar la notificación con el nombre de la pastilla
            NotificationUtils.showNotification(context, "Recordatorio", "Tienes que tomarte " + nombrePastilla);
        } catch (Exception e) {
            Log.e(TAG, "Error en onReceive", e);
        }
    }


}
