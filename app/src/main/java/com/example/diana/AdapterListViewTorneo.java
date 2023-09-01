package com.example.diana;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class AdapterListViewTorneo<String> extends ArrayAdapter<String> {

    List<String> playerList;


    public AdapterListViewTorneo(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> playerList) {
        super(context, resource, textViewResourceId, playerList);
        this.playerList = playerList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview, parent, false);
        }

        TextView textView = itemView.findViewById(R.id.textViewListView);
        textView.setText(playerList.get(position).toString());
        Button button = itemView.findViewById(R.id.buttonListView);


        // Lógica para habilitar/deshabilitar el botón según la posición
        if (position == 0) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }

        return itemView;
    }
}
