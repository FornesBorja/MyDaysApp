package com.example.mydaysapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity
{
    Button IniSesion_b, Registrar_b;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        IniSesion_b = findViewById(R.id.IniSesion_button);
        Registrar_b= findViewById(R.id.registrarse_button);
        IniSesion_b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), iniSesionActivity.class);
                startActivity(intent);
            }
        });
        Registrar_b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), RegisActivity.class);
                startActivity(intent);
            }
        });
    }
}