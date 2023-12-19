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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;

import java.util.ArrayList;

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