package com.example.diana;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdepterRecyclerViewTorneos extends RecyclerView.Adapter<AdepterRecyclerViewTorneos.TorneosViewHolder> {

    private List<String> torneosList;
    private View.OnClickListener onClickListener;

    public AdepterRecyclerViewTorneos(List<String> torneosList) {
        this.torneosList = torneosList;
    }

    @NonNull
    @Override
    public TorneosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_torneo, parent, false);
        view.setOnClickListener(onClickListener);
        return new TorneosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorneosViewHolder holder, int position) {
        holder.bind(torneosList.get(position));

    }

    @Override
    public int getItemCount() {
        return torneosList.size();
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public static class TorneosViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreTorneoTextView;

        public TorneosViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTorneoTextView = itemView.findViewById(R.id.nombreTorneoRecyclerTextView);
        }


        public void bind(String nombreTorneo){
            nombreTorneoTextView.setText(nombreTorneo);
        }

        public TextView getNombreTorneoTextView() {
            return nombreTorneoTextView;
        }
    }
}
