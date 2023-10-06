package com.example.mydaysapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity
{
    private static final long SPLASH_DELAY = 900;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Configura cualquier inicialización adicional que necesites hacer aquí

        // Utiliza un Handler para retrasar el inicio de la siguiente actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Inicia la siguiente actividad después del tiempo de retardo
                startActivity(new Intent(SplashActivity.this,
                        MainActivity.class));

                finish();
            }
        }, SPLASH_DELAY);
    }
}