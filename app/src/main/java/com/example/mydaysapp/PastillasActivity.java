package com.example.mydaysapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydaysapp.notifications.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class PastillasActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_ADD_ITEM = 1;
    ArrayList<String> NombrePastilla;
    ArrayList<String> DiaToma;
    ArrayList<String> HoraToma;
    private AdaptadorDatos adaptador1; // Declarar la referencia al adaptador como una variable de clase

    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastillas);
        Toolbar tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        recycler= findViewById(R.id.lista_pastillasRV);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        NombrePastilla =new ArrayList<String>();
        DiaToma =new ArrayList<String>();
        HoraToma =new ArrayList<String>();
        adaptador1= new AdaptadorDatos(NombrePastilla,DiaToma,HoraToma);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String usuario = mAuth.getCurrentUser().getUid();
        cargarDatosPastillasFirebase(usuario);

        recycler.setAdapter(adaptador1);
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_pastillas,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.anyadir)
        {
            //codigo
            Intent intent = new Intent(this, FormularioPastillasActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM);
            return true;
        }else
        { if (item.getItemId() == R.id.quitar)
        {
            quitarPastillaSeleccionada();
            return true;
        }else
        {
            return super.onOptionsItemSelected(item);

        }
        }
    }
    private void cargarDatosPastillasFirebase(String usuario)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference eventosCollection = db.collection("pastillas");

            eventosCollection.whereEqualTo("usuario", userId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Aquí, 'document' representa cada documento en la colección 'pastillas'
                                // Puedes acceder a los datos del documento utilizando getData()
                                Map<String, Object> datosPill = document.getData();

                                // Extraer la información específica del evento
                                String dia= datosPill.get("dia").toString();
                                String hora = datosPill.get("hora").toString();
                                String nombre = datosPill.get("nombre").toString();

                                NombrePastilla.add(nombre);
                                HoraToma.add(hora);
                                DiaToma.add(dia);
                            }
                            adaptador1.notifyDataSetChanged(); // Mueve esta línea fuera del bucle for
                        } else {
                            Log.w("Error", "Error obteniendo documentos.", task.getException());
                        }
                    });
        }
    }
    private void quitarPastillaSeleccionada() {
        // Verificar si hay al menos una pastilla seleccionada
        if (!NombrePastilla.isEmpty()) {
            // Obtener la posición de la última pastilla añadida
            int ultimaPosicion = NombrePastilla.size() - 1;

            // Obtener la información de la última pastilla añadida
            String nombrePastillaEliminar = NombrePastilla.get(ultimaPosicion);
            String diaPastillaEliminar = DiaToma.get(ultimaPosicion);
            String horaAlarmaEliminar = HoraToma.get(ultimaPosicion);

            // Eliminar la pastilla del RecyclerView
            NombrePastilla.remove(ultimaPosicion);
            DiaToma.remove(ultimaPosicion);
            HoraToma.remove(ultimaPosicion);
            adaptador1.notifyDataSetChanged();

            // Eliminar la pastilla de Firebase
            eliminarPastillaFirebase(nombrePastillaEliminar, diaPastillaEliminar, horaAlarmaEliminar);
        } else {
            Toast.makeText(this, "No hay pastillas para quitar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarPastillaFirebase(String nombrePastilla, String diaPastilla, String horaAlarma) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference pastillasCollection = db.collection("pastillas");

            pastillasCollection
                    .whereEqualTo("usuario", userId)
                    .whereEqualTo("nombre", nombrePastilla)
                    .whereEqualTo("dia", diaPastilla)
                    .whereEqualTo("hora", horaAlarma)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Eliminar el documento de Firebase
                                pastillasCollection.document(document.getId()).delete();
                            }
                        } else {
                            Log.w("Error", "Error obteniendo documentos.", task.getException());
                        }
                    });
            cancelRepeatingAlarm();
        }
    }
    private void cancelRepeatingAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Cancelar la alarma
            alarmManager.cancel(pendingIntent);

            // También puedes cancelar el PendingIntent
            pendingIntent.cancel();

            Log.d(TAG, "Alarma cancelada");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ITEM && resultCode == RESULT_OK) {
            // Obtiene los datos del Intent
            String nuevoDatoNombre = data.getStringExtra("pastillaNombre");

            String nuevoDatoDia = data.getStringExtra("diaPastilla");

            String nuevoDatoHora = data.getStringExtra("horaAlarma");



            // Agrega el nuevo dato a la lista de datos que alimenta el RecyclerView
            // (esto puede variar dependiendo de cómo estés manejando tus datos)
            NombrePastilla.add(nuevoDatoNombre);
            HoraToma.add(nuevoDatoHora);
            DiaToma.add(nuevoDatoDia);

            // Notifica al adaptador sobre el cambio en los datos
            adaptador1.notifyDataSetChanged();
        }
    }

}