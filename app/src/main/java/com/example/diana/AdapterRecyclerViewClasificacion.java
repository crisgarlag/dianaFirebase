package com.example.diana;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRecyclerViewClasificacion extends RecyclerView.Adapter<AdapterRecyclerViewClasificacion.ClasificacionViewHolder> {

    private List<Usuario> usuarioList;
    private boolean editTextEnabled = false;


    public AdapterRecyclerViewClasificacion(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public ClasificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_clasificacion, parent, false);
        return new ClasificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClasificacionViewHolder holder, int position) {

        holder.bind(usuarioList.get(position), position+1);
        holder.puntuarEditText.setEnabled(editTextEnabled);
        holder.puntuarEditText.setFocusable(editTextEnabled);
        holder.puntuarEditText.setFocusableInTouchMode(editTextEnabled);

    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    

    public void setAllEditTextEnabled(boolean enabled){
        editTextEnabled = enabled;
        notifyDataSetChanged();
    }


    public static class ClasificacionViewHolder extends RecyclerView.ViewHolder {

        private TextView clasificacionTextView, nombreJugadorTextView, puntosTextView;
        private EditText puntuarEditText;

        public EditText getPuntuarEditText() {
            return puntuarEditText;
        }

        public TextView getNombreJugadorTextView() {
            return nombreJugadorTextView;
        }

        public ClasificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            clasificacionTextView = itemView.findViewById(R.id.clasificacionTextView);
            nombreJugadorTextView = itemView.findViewById(R.id.nombreJugadorTextView);
            puntosTextView = itemView.findViewById(R.id.puntosTextView);
            puntuarEditText = itemView.findViewById(R.id.editTextPuntuar);
        }

        public void bind(Usuario usuario, int position){
            clasificacionTextView.setText(String.valueOf(position));
            nombreJugadorTextView.setText(usuario.getAlias());
            puntosTextView.setText(String.valueOf(usuario.getPuntuacion()));

        }
    }
}
