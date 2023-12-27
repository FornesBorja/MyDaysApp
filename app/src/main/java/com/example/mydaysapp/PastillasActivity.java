package com.example.mydaysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;

import com.github.sundeepk.compactcalendarview.domain.Event;
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
        {
            return super.onOptionsItemSelected(item);

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