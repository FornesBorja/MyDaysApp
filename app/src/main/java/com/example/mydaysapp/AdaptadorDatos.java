package com.example.mydaysapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorDatos extends RecyclerView.Adapter<AdaptadorDatos.ViewHolderDatos>
{
    ArrayList<String> NombrePastilla;
    ArrayList<String> DiaToma;
    ArrayList<String> HoraToma;

    public AdaptadorDatos(ArrayList<String> NombrePastilla, ArrayList<String> DiaToma, ArrayList<String> HoraToma)
    {
        this.NombrePastilla = NombrePastilla;
        this.DiaToma = DiaToma;
        this.HoraToma = HoraToma;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pastilla, null,false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(NombrePastilla.get(position), DiaToma.get(position),HoraToma.get(position));
    }

    @Override
    public int getItemCount() {
        return NombrePastilla.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder
    {
        TextView nombrePastilla, diaPastilla, horaPastilla;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            nombrePastilla=(TextView) itemView.findViewById(R.id.nombre_pasTV);
            diaPastilla=(TextView) itemView.findViewById(R.id.dias_pasTV);
            horaPastilla=(TextView) itemView.findViewById(R.id.horas_pasTV);

        }

        public void asignarDatos(String nombre, String dia, String hora)
        {
            nombrePastilla.setText(nombre);
            diaPastilla.setText(dia);
            horaPastilla.setText(hora);

        }
    }
    
}
