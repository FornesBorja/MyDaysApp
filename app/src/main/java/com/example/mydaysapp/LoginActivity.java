package com.example.mydaysapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button iniciarSesion, registrar;
    EditText contraInicio, correoInicio;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniciarSesion=findViewById(R.id.iniciar_btn);
        contraInicio=findViewById(R.id.contraini_et);
        correoInicio=findViewById(R.id.correoini_et);
        mAuth = FirebaseAuth.getInstance();
        registrar=findViewById(R.id.registrar_btn);
        registrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(LoginActivity.this, RegisActivity.class));
                finish();
            }
        });
        iniciarSesion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String password= contraInicio.getText().toString().trim();
                String email= correoInicio.getText().toString().trim();
                if(password.isEmpty() && email.isEmpty())
                {
                    Toast toast = Toast.makeText(LoginActivity.this, "Tienes que escribir un correo y una contraseña", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (password.isEmpty())
                {
                    Toast toast = Toast.makeText(LoginActivity.this, "Tienes que escribir una contraseña", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(email.isEmpty())
                {
                    Toast toast = Toast.makeText(LoginActivity.this, "Tienes que escribir un correo", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    loginUser(email, password);
                }

            }
        });
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast toast = Toast.makeText(LoginActivity.this, "Login fallido", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}