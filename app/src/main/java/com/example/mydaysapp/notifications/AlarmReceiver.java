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
            Log.d(TAG, "onReceive ejecutado");
            NotificationUtils.showNotification(context, "Recordatorio", "Tienes que tomarte tus pastillas");
        } catch (Exception e) {
            Log.e(TAG, "Error en onReceive", e);
        }
    }


}
