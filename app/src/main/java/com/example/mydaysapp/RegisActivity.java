package com.example.mydaysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisActivity extends AppCompatActivity {
    Button registro;
    EditText nombreReg, emailReg, contraReg;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        mFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        registro=findViewById(R.id.registro_btn);
        emailReg=findViewById(R.id.correoreg_et);
        contraReg=findViewById(R.id.contrareg_et);

        registro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email=emailReg.getText().toString().trim();
                String password=contraReg.getText().toString().trim();

                if (email.isEmpty()&&password.isEmpty())
                {
                    Toast toast = Toast.makeText(RegisActivity.this, "No puedes dejar todo en blanco", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(email.isEmpty()||password.isEmpty())
                {
                    Toast toast = Toast.makeText(RegisActivity.this, "Tienes que escribir todos los datos", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override

            public void onSuccess(AuthResult authResult) {
                finish();
                startActivity(new Intent(RegisActivity.this, MainActivity.class));
                Toast toast = Toast.makeText(RegisActivity.this, "Usuario registrado", Toast.LENGTH_SHORT);
                toast.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast toast = Toast.makeText(RegisActivity.this, "Usuario no registrado, revisa el formato del correo", Toast.LENGTH_SHORT);
                toast.show();
            }
        });}}
