package com.example.mydaysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Toolbar tb;
    Button button;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=(Toolbar)findViewById(R.id.tb);
        setSupportActionBar(tb);
        button=findViewById(R.id.button);
        mAuth=FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem opc)
    {
        if (opc.getItemId() == R.id.pastillas
        ) {
            finish();
            Intent intent = new Intent(getApplicationContext(), PastillasActivity.class);
            startActivity(intent);
            return true;
        } else if (opc.getItemId() == R.id.notificaciones) {
            return true;
        } else if (opc.getItemId() == R.id.opciones) {
            return true;
        }else
        {
           return super.onOptionsItemSelected(opc);
        }

    }

        }

