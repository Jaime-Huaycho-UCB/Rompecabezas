package com.example.rompecabezas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class FragmentRecords extends Fragment {
    public FragmentRecords() {
        super(R.layout.fragment_records);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.lv_records);
        DBRecords db = new DBRecords(getContext());
        ArrayList<Record> tiempos = db.obtenerTiempos();

        RecordAdapter adapter = new RecordAdapter(getContext(), tiempos);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Record recordSeleccionado = tiempos.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar Récord")
                    .setMessage("¿Seguro que deseas eliminar este récord?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        db.eliminarTiempo(recordSeleccionado.id);
                        tiempos.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Récord eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }
}