package com.example.rompecabezas;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentJuego extends Fragment {
    private int tamanoRompecabezas;
    private GridLayout gridPuzzle;
    private ImageView[][] piezas;
    private int espacioBlancoX, espacioBlancoY;
    private ArrayList<Bitmap> piezasImagen;
    private Bitmap imagenSeleccionada;
    private ImageView imagenOriginal;

    public FragmentJuego() {
        super(R.layout.fragment_juego);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            tamanoRompecabezas = getArguments().getInt("tamano_rompecabezas", 3);
            Uri imagenUri = getArguments().getParcelable("imagen_uri");

            if (imagenUri != null) {
                try {
                    imagenSeleccionada = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(), imagenUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (imagenSeleccionada == null) {
            return;
        }

        imagenOriginal = view.findViewById(R.id.imagen_original);
        imagenOriginal.setImageBitmap(imagenSeleccionada);

        gridPuzzle = view.findViewById(R.id.grid_puzzle);
        Button btnVolver = view.findViewById(R.id.btn_volver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).cambiarFragmento(new FragmentInicio());
            }
        });
        iniciarRompecabezas();
    }

    private void iniciarRompecabezas() {
        piezasImagen = dividirImagen(imagenSeleccionada, tamanoRompecabezas);
        gridPuzzle.setColumnCount(tamanoRompecabezas);
        gridPuzzle.setRowCount(tamanoRompecabezas);
        piezas = new ImageView[tamanoRompecabezas][tamanoRompecabezas];

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < tamanoRompecabezas * tamanoRompecabezas - 1; i++) {
            indices.add(i);
        }
        indices.add(-1);
        Collections.shuffle(indices);

        int index = 0;
        for (int i = 0; i < tamanoRompecabezas; i++) {
            for (int j = 0; j < tamanoRompecabezas; j++) {
                ImageView pieza = new ImageView(getContext());
                pieza.setScaleType(ImageView.ScaleType.FIT_CENTER);
                pieza.setAdjustViewBounds(true);

                if (indices.get(index) != -1) {
                    pieza.setImageBitmap(piezasImagen.get(indices.get(index)));
                } else {
                    espacioBlancoX = i;
                    espacioBlancoY = j;
                }
                pieza.setPadding(5, 5, 5, 5);
                final int x = i, y = j;
                pieza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moverPieza(x, y);
                    }
                });
                piezas[i][j] = pieza;
                gridPuzzle.addView(pieza);
                index++;
            }
        }
    }

    private ArrayList<Bitmap> dividirImagen(Bitmap imagen, int tamano) {
        ArrayList<Bitmap> piezas = new ArrayList<>();
        int anchoPieza = imagen.getWidth() / tamano;
        int altoPieza = imagen.getHeight() / tamano;
        for (int i = 0; i < tamano; i++) {
            for (int j = 0; j < tamano; j++) {
                if (i == tamano - 1 && j == tamano - 1) continue;
                piezas.add(Bitmap.createBitmap(imagen, j * anchoPieza, i * altoPieza, anchoPieza, altoPieza));
            }
        }
        return piezas;
    }
    private void moverPieza(int x, int y) {
        if ((Math.abs(x - espacioBlancoX) == 1 && y == espacioBlancoY) || (Math.abs(y - espacioBlancoY) == 1 && x == espacioBlancoX)) {
            piezas[espacioBlancoX][espacioBlancoY].setImageBitmap(((BitmapDrawable) piezas[x][y].getDrawable()).getBitmap());
            piezas[x][y].setImageDrawable(null);
            espacioBlancoX = x;
            espacioBlancoY = y;
            verificarVictoria();
        }
    }
    private void verificarVictoria() {
        boolean ganado = true;
        int index = 0;
        for (int i = 0; i < tamanoRompecabezas; i++) {
            for (int j = 0; j < tamanoRompecabezas; j++) {
                if (i == tamanoRompecabezas - 1 && j == tamanoRompecabezas - 1) {
                    break;
                }
                if (piezas[i][j].getDrawable() == null) {
                    ganado = false;
                    break;
                }
                Bitmap bitmapActual = ((BitmapDrawable) piezas[i][j].getDrawable()).getBitmap();
                if (!bitmapActual.sameAs(piezasImagen.get(index))) {
                    ganado = false;
                    break;
                }
                index++;
            }
        }
        if (ganado) {
            mostrarMensajeVictoria();
        }
    }

    private void mostrarMensajeVictoria() {
        Toast.makeText(getContext(), "¡Felicidades! Has completado el rompecabezas", Toast.LENGTH_LONG).show();
        Button btnVolver = getView().findViewById(R.id.btn_volver);
        btnVolver.setText("¡Ganaste! Volver");
    }
}
