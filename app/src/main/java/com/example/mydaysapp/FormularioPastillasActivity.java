package com.example.mydaysapp;


import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;

public class FormularioPastillasActivity extends AppCompatActivity {

    int hora, minuto;
    String horaAlarma, pastillaNombre, diaPastilla=" ";
    CheckBox lunes, martes, miercoles, jueves, viernes, sabado, domingo;
    EditText pastillaNombreET;
    Button guardarBTN;
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

        guardarBTN=findViewById(R.id.guardarBTN);
        guardarBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
              if(pastillaNombreET.getText().toString()!="")
              {
                  pastillaNombre=pastillaNombreET.getText().toString();
              }else {
                  Toast.makeText(getApplicationContext(),"Tienes que poner un nombre válido", Toast.LENGTH_SHORT).show();
              }
              do{
                    Toast.makeText(getApplicationContext(),"Tienes que elegir al menos un día", Toast.LENGTH_SHORT).show();
                }while(!lunes.isChecked()&&!martes.isChecked()&&!miercoles.isChecked()&&!jueves.isChecked()&&!viernes.isChecked()&&!sabado.isChecked()&&!domingo.isChecked());

                if(lunes.isChecked())
                    {
                        diaPastilla="L ";
                    }
                    if(martes.isChecked())
                    {
                        diaPastilla=diaPastilla+" M ";
                    }
                    if(miercoles.isChecked())
                    {
                        diaPastilla=diaPastilla+" X ";
                    }
                    if(jueves.isChecked())
                    {
                        diaPastilla=diaPastilla+" J ";
                    }
                    if(viernes.isChecked())
                    {
                        diaPastilla=diaPastilla+" V ";
                    }
                    if(sabado.isChecked())
                    {
                        diaPastilla=diaPastilla+" S ";
                    }
                    if(domingo.isChecked())
                    {
                        diaPastilla=diaPastilla+" D ";
                    }
                //subirPastillasFirebase();
                Intent intent = new Intent();
                intent.putExtra("diaPastilla", diaPastilla);
                intent.putExtra("pastillaNombre", pastillaNombre);
                intent.putExtra("horaAlarma", horaAlarma);

                // Establece el resultado como OK y adjunta el Intent
                setResult(RESULT_OK, intent);
                // Cierra la Activity del formulario
                finish();
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