package com.example.mydaysapp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydaysapp.notifications.AlarmReceiver;
import com.example.mydaysapp.notifications.NotificationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FormularioPastillasActivity extends AppCompatActivity {

    int hora, minuto;
    String horaAlarma, pastillaNombre, diaPastilla=" ";
    CheckBox lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    EditText pastillaNombreET;
    Button guardarBTN;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pastillas);
        pastillaNombreET=findViewById(R.id.pastillaNombreET);
        lunes=findViewById(R.id.checkBox1);
        martes=findViewById(R.id.checkBox2);
        miercoles=findViewById(R.id.checkBox3);
        jueves=findViewById(R.id.checkBox4);
        viernes=findViewById(R.id.checkBox5);
        sabado=findViewById(R.id.checkBox6);
        domingo=findViewById(R.id.checkBox7);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();

        String usuario=mAuth.getCurrentUser().getUid();
        NotificationUtils.createNotificationChannel(getApplicationContext());


        guardarBTN=findViewById(R.id.guardarBTN);
        guardarBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (pastillaNombreET.getText().toString() != "") {
                    pastillaNombre = pastillaNombreET.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(), "Tienes que poner un nombre válido", Toast.LENGTH_SHORT).show();
                }
                if (!lunes.isChecked() && !martes.isChecked() && !miercoles.isChecked() && !jueves.isChecked() && !viernes.isChecked() && !sabado.isChecked() && !domingo.isChecked()) {

                    Toast.makeText(getApplicationContext(), "Tienes que elegir al menos un día", Toast.LENGTH_SHORT).show();
                    finish();
                } else
                {
                    if (lunes.isChecked()) {
                        diaPastilla = "L ";

                    }
                    if (martes.isChecked()) {
                        diaPastilla = diaPastilla + " M ";

                    }
                    if (miercoles.isChecked()) {
                        diaPastilla = diaPastilla + " X ";

                    }
                    if (jueves.isChecked()) {
                        diaPastilla = diaPastilla + " J ";

                    }
                    if (viernes.isChecked()) {
                        diaPastilla = diaPastilla + " V ";

                    }
                    if (sabado.isChecked()) {
                        diaPastilla = diaPastilla + " S ";

                    }
                    if (domingo.isChecked()) {
                        diaPastilla = diaPastilla + " D ";

                    }
                    Intent intent = new Intent();
                    intent.putExtra("diaPastilla", diaPastilla);
                    intent.putExtra("pastillaNombre", pastillaNombre);
                    intent.putExtra("horaAlarma", horaAlarma);
                    // Establece el resultado como OK y adjunta el Intent
                    setResult(RESULT_OK, intent);
                    // Cierra la Activity del formulario
                    finish();
                    subirAFirebasePastillas(usuario, diaPastilla, pastillaNombre, horaAlarma);
                    setRepeatingAlarm(diaPastilla, horaAlarma);


                }
            }

        });

    }
    private void setRepeatingAlarm(String diasSeleccionados, String horaAlarma) {
        String[] partes = horaAlarma.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Configurar para que se repita cada semana
            for (int i = 0; i < diasSeleccionados.length(); i++) {
                char dia = diasSeleccionados.charAt(i);
                int dayOfWeek = getDayOfWeek(dia);

                if (dayOfWeek != -1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                    calendar.set(Calendar.HOUR_OF_DAY, hora);
                    calendar.set(Calendar.MINUTE, minutos);
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_IMMUTABLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    }
                }
            }
        }
    }

    private int getDayOfWeek(char day) {
        switch (day) {
            case 'L':
                return Calendar.MONDAY;
            case 'M':
                return Calendar.TUESDAY;
            case 'X':
                return Calendar.WEDNESDAY;
            case 'J':
                return Calendar.THURSDAY;
            case 'V':
                return Calendar.FRIDAY;
            case 'S':
                return Calendar.SATURDAY;
            case 'D':
                return Calendar.SUNDAY;
            default:
                return -1;
        }
    }


    private void subirAFirebasePastillas(String usuario, String diaPastilla, String pastillaNombre, String horaAlarma)
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pastilla = new HashMap<>();
        pastilla.put("usuario", usuario);
        pastilla.put("dia", diaPastilla);
        pastilla.put("nombre", pastillaNombre);
        pastilla.put("hora", horaAlarma);
        db.collection("pastillas").document()
                .set(pastilla)
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

    public void SeleccionarHora(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hora = selectedHour;
                minuto = selectedMinute;
                horaAlarma=hora+":"+minuto;
            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hora, minuto, true);

        timePickerDialog.setTitle("Selecciona hora");
        timePickerDialog.show();

    }

}