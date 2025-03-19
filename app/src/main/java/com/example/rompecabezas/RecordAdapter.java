package com.example.rompecabezas;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record> {
    public RecordAdapter(@NonNull Context context, @NonNull List<Record> records) {
        super(context, 0, records);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        Record record = getItem(position);
        TextView textViewTiempo = convertView.findViewById(R.id.tvTiempo);
        TextView textViewFecha = convertView.findViewById(R.id.tvFecha);
        ImageView imageView = convertView.findViewById(R.id.ivFicha);

        textViewTiempo.setText("Tiempo: " + record.tiempo + " segundos");
        textViewFecha.setText("Fecha: " + record.fecha);

        if (record.imagenUri != null) {
            try {
                Uri imageUri = Uri.parse(record.imagenUri);
                imageView.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        return convertView;
    }
}