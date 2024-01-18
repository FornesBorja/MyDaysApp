package com.example.mydaysapp;

import static android.graphics.Color.rgb;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Toolbar tb;
    Button anyadir_animo;
    FirebaseAuth mAuth;
    TextView textViewMonth;
    CompactCalendarView compactCalendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=findViewById(R.id.tb);
        setSupportActionBar(tb);
        textViewMonth = findViewById(R.id.textViewMonth);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        // Obtener la fecha actual
        Date currentDate = Calendar.getInstance().getTime();
        textViewMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hacer que el CompactCalendarView se desplace a la fecha actual
                compactCalendarView.setCurrentDate(currentDate);
                 updateLabel(currentDate);
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String usuario=mAuth.getUid();
        cargarDatosFirebase(usuario);



        setDayColumnNames();



        // Formatear la fecha actual para obtener el nombre del mes
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = monthFormat.format(currentDate);
        SimpleDateFormat yearFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            yearFormat = new SimpleDateFormat("YYYY", Locale.getDefault());
        }
        String year = yearFormat.format(currentDate);

        // Mostrar el nombre del mes en una etiqueta (TextView, por ejemplo)
        TextView textViewMonth = findViewById(R.id.textViewMonth);
        textViewMonth.setText(monthName+ " "+year );

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Obtener la fecha actual
                Date currentDate = Calendar.getInstance().getTime();

                // Verificar si la fecha clicada es igual o anterior a la fecha actual
                if (dateClicked.before(currentDate) || dateClicked.equals(currentDate)) {
                    // Implementa la lógica para cambiar el color según el estado de ánimo seleccionado
                    eligeMoodColor(compactCalendarView, dateClicked);
                } else {
                    // Fecha futura, muestra un mensaje o realiza alguna acción según tus necesidades
                    Toast.makeText(MainActivity.this, "No puedes seleccionar un estado de ánimo para el futuro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateLabel(firstDayOfNewMonth);            }
        });

    }

    private void updateLabel(Date date) {
        // Formatear la fecha para obtener el nombre del mes
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = monthFormat.format(date);
        SimpleDateFormat yearFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            yearFormat = new SimpleDateFormat("YYYY", Locale.getDefault());
        }
        String year = yearFormat.format(date);

        // Mostrar el nombre del mes en una etiqueta (TextView, por ejemplo)
        textViewMonth.setText(monthName+ " "+year );
        // Mostrar el nombre del mes en la etiqueta
    }
    private void cargarDatosFirebase(String usuario)
    {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference eventosCollection = db.collection("calendario").document(usuario).getParent();

            eventosCollection.whereEqualTo("usuario", userId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                // Aquí, 'document' representa cada documento en la colección 'calendario'
                                // Puedes acceder a los datos del documento utilizando getData()
                                Map<String, Object> eventData = document.getData();

                                // Extraer la información específica del evento
                                int color=Integer.parseInt(eventData.get("color").toString());
                                long fecha = Long.parseLong(eventData.get("fecha").toString());
                                String estadoAnimo = eventData.get("animo").toString();

                                Event moodEvent = new Event(color,fecha, estadoAnimo);
                                compactCalendarView.addEvent(moodEvent);

                            }
                        } else {
                            Log.w("Error", "Error obteniendo documentos.", task.getException());
                        }
                    });
        }
    }
    private void setDayColumnNames() {
        // Obtener los nombres de los días de la semana en mayúsculas
        String[] dayColumnNames = new String[]{"L", "M", "X", "J", "V", "S", "D"};

        // Configurar los nombres de los días de la semana en CompactCalendarView
        compactCalendarView.setDayColumnNames(dayColumnNames);
    }
    private void eligeMoodColor(CompactCalendarView compactCalendarView, Date dateClicked)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el Estado de Ánimo")
                .setItems(new CharSequence[]{"Triste", "Feliz", "Neutral"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Asigna un color según el estado de ánimo seleccionado
                        int color = getColorForMood(which);
                        long fecha=dateClicked.getTime();
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        FirebaseAuth mAuth=FirebaseAuth.getInstance();
                        String usuario=mAuth.getUid();

                        String animo=getMoodLabel(which);

                        Event moodEvent = new Event(color,fecha, animo);
                        compactCalendarView.addEvent(moodEvent);
                        subirAFirebase(usuario, fecha, animo, color);
                        // Añade un evento al calendario con el color correspondiente


                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private static void subirAFirebase(String usuario, long fecha, String animo, int color)
    {
        // Obtén una instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crea un nuevo objeto Evento con tus datos
        Map<String, Object> dia = new HashMap<>();
        dia.put("usuario", usuario);
        dia.put("fecha", fecha);
        dia.put("animo", animo);
        dia.put("color", color);
        String usufecha=usuario+fecha;

        db.collection("calendario").document(usufecha)
                .set(dia)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documento escrito correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error escribiendo el documento", e);
                    }
                });
    }




    private int getColorForMood(int mood) {
        switch (mood) {
            case 0: // Triste
                return rgb(240,201,135);
            case 1: // Feliz
                return rgb(137, 189, 158);
            case 2: // Neutral
                return rgb(60, 21, 59);
            default:
                return rgb(60, 21, 59);
        }
    }

    private String getMoodLabel(int mood) {
        switch (mood) {
            case 0: // Feliz
                return "Feliz";
            case 1: // Triste
                return "Triste";
            case 2: // Neutral
                return "Neutral";
            default:
                return "Desconocido";
        }
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
            finish();
            return true;
        } else if (opc.getItemId() == R.id.opciones) {
            Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
            startActivity(intent);
            return true;
        }else
        {
           return super.onOptionsItemSelected(opc);
        }

    }
}





