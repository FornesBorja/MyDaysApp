package com.example.mydaysapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpcionesActivity extends AppCompatActivity {
    Button cerrarSesion, cargarIMG, CorreoV, CorreoC;
    ImageView foto;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1; // Constante para la solicitud de selección de imagen
    private Uri selectedImageUri = null; // Variable para almacenar la URI de la imagen seleccionada
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    EditText correoTV;
    String nuevoCorreo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cerrarSesion = findViewById(R.id.cerrarSesion_btn);
        foto = findViewById(R.id.imageView2);
        cargarIMG = findViewById(R.id.subirIMG_BTN);
        correoTV= findViewById(R.id.mostrarCorreo);
        CorreoV=findViewById(R.id.CorreoV);
        CorreoC=findViewById(R.id.correoC);
        CorreoC.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
               nuevoCorreo=correoTV.getText().toString();
                if (esCorreoElectronicoValido(nuevoCorreo))
                cambiarDireccionCorreo(nuevoCorreo);
            }
        });
        CorreoV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Se ha enviado correo de verificación", Toast.LENGTH_SHORT);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No se puede enviar correo de verificación.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }

            }
        });
        if (user != null && user.isEmailVerified()) {
            // El usuario está verificado, deshabilita el botón
            CorreoV.setEnabled(false);
        } else {
            // El usuario no está verificado, habilita el botón
            CorreoV.setEnabled(true);
        }
        if (user != null) {
            String correoUsuario = user.getEmail();
            correoTV.setText(correoUsuario);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Obtener la URI de descarga si existe
        storageReference.child("images/" + currentUser.getUid() + ".jpg").getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // La URI de descarga está disponible aquí
                    selectedImageUri = uri;

                    // Mostrar la imagen seleccionada en el ImageView utilizando Picasso
                    Picasso.get().load(selectedImageUri).into(foto);
                })
                .addOnFailureListener(exception -> {
                    // Manejar errores al obtener la URI de descarga
                    Log.e("OpcionesActivity", "Error al obtener la URI de descarga: " + exception.getMessage());
                });

        cargarIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir el selector de imágenes
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void cambiarDireccionCorreo(String nuevoCorreo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(nuevoCorreo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // La dirección de correo electrónico ha sido actualizada exitosamente
                                Toast.makeText(getApplicationContext(), "Correo electrónico actualizado", Toast.LENGTH_SHORT).show();
                            } else {
                                // Manejar el error al actualizar la dirección de correo electrónico
                                Toast.makeText(getApplicationContext(), "Error al actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    // Método para manejar el resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Mostrar la imagen seleccionada en el ImageView utilizando Picasso
            Picasso.get().load(selectedImageUri).into(foto);

            // Llamar al método uploadImage para subir la imagen a Firebase Storage
            uploadImage(selectedImageUri);
        }
    }

    // Método para subir la imagen a Firebase Storage
    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            // Crear una referencia al archivo en Firebase Storage
            StorageReference ref = storageReference.child("images/" + currentUser.getUid() + ".jpg");

            // Subir la imagen
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Manejar el éxito de la carga
                        Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar fallos en la carga
                        Toast.makeText(this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    public static boolean esCorreoElectronicoValido(String correo) {
        // Expresión regular para validar una dirección de correo electrónico
        String expresionRegular = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

        // Compilar la expresión regular en un patrón
        Pattern pattern = Pattern.compile(expresionRegular);

        // Crear un objeto Matcher
        Matcher matcher = pattern.matcher(correo);

        // Verificar si la dirección de correo electrónico coincide con la expresión regular
        return matcher.matches();
    }
}