package com.example.rompecabezas;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentJuego extends Fragment {

    private Uri imagenUri;
    private int tamanoRompecabezas;
    private GridLayout gridPuzzle;
    private ImageView[][] piezas;
    private int espacioBlancoX, espacioBlancoY;
    private ArrayList<Bitmap> piezasImagen;
    private Bitmap imagenSeleccionada;
    private ImageView imagenOriginal;

    private long tiempoInicio;
    private boolean juegoEnCurso = false;

    public FragmentJuego() {
        super(R.layout.fragment_juego);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            tamanoRompecabezas = getArguments().getInt("tamano_rompecabezas", 3);
            imagenUri = getArguments().getParcelable("imagen_uri");

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

        Button btnResolver = view.findViewById(R.id.btn_resolver);
        btnResolver.setOnClickListener(v -> {
            SolverAStar solver = new SolverAStar();
            List<NodoPuzzle> solucion = solver.resolver(obtenerEstadoActual());

            if (solucion != null) {
                AnimadorRompecabezas animador = new AnimadorRompecabezas(this);
                animador.animarSolucion(solucion);
            } else {
                Toast.makeText(getContext(), "No se encontró solución", Toast.LENGTH_SHORT).show();
            }
        });


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
        tiempoInicio = System.currentTimeMillis();
        juegoEnCurso = true;
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

        gridPuzzle.post(new Runnable() {
            @Override
            public void run() {
                int puzzleWidth = gridPuzzle.getWidth();
                int puzzleHeight = gridPuzzle.getHeight();

                if (puzzleWidth == 0 || puzzleHeight == 0) {
                    Toast.makeText(getContext(), "Error: El GridLayout no tiene un tamaño válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int pieceWidth = puzzleWidth / tamanoRompecabezas;
                int pieceHeight = puzzleHeight / tamanoRompecabezas;

                int index = 0;
                for (int i = 0; i < tamanoRompecabezas; i++) {
                    for (int j = 0; j < tamanoRompecabezas; j++) {
                        ImageView pieza = new ImageView(getContext());
                        pieza.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        pieza.setAdjustViewBounds(true);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = pieceWidth;
                        params.height = pieceHeight;
                        pieza.setLayoutParams(params);

                        final int currentIndex = i * tamanoRompecabezas + j;

                        if (indices.get(currentIndex) != -1) {
                            pieza.setImageBitmap(piezasImagen.get(indices.get(currentIndex)));
                        } else {
                            pieza.setImageDrawable(null);
                            espacioBlancoX = i;
                            espacioBlancoY = j;
                        }

                        pieza.setPadding(5, 5, 5, 5);

                        final int x = i, y = j;
                        pieza.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Math.abs(x - espacioBlancoX) == 1 && y == espacioBlancoY ||
                                        Math.abs(y - espacioBlancoY) == 1 && x == espacioBlancoX) {
                                    moverPieza(x, y);
                                }
                            }
                        });

                        piezas[i][j] = pieza;
                        gridPuzzle.addView(pieza);
                    }
                }
            }
        });
    }

    private void moverPieza(int x, int y) {
        if ((Math.abs(x - espacioBlancoX) == 1 && y == espacioBlancoY) ||
                (Math.abs(y - espacioBlancoY) == 1 && x == espacioBlancoX)) {

            piezas[espacioBlancoX][espacioBlancoY].setImageBitmap(((BitmapDrawable) piezas[x][y].getDrawable()).getBitmap());
            piezas[x][y].setImageDrawable(null);

            espacioBlancoX = x;
            espacioBlancoY = y;

            verificarVictoria();
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
        long tiempoFinal = System.currentTimeMillis();
        long tiempoTotal = (tiempoFinal - tiempoInicio) / 1000;

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        DBRecords db = new DBRecords(getContext());
        db.insertarTiempo(tiempoTotal, imagenUri.toString(), fecha);

        new AlertDialog.Builder(getContext())
                .setTitle("¡Felicidades!")
                .setMessage("Has completado el rompecabezas en " + tiempoTotal + " segundos.")
                .setPositiveButton("OK", (dialog, which) -> {
                    ((MainActivity) requireActivity()).cambiarFragmento(new FragmentInicio());
                })
                .show();
    }
    public void actualizarRompecabezas(int[][] nuevoEstado) {
        for (int i = 0; i < tamanoRompecabezas; i++) {
            for (int j = 0; j < tamanoRompecabezas; j++) {
                if (nuevoEstado[i][j] == 0) {
                    piezas[i][j].setImageDrawable(null);
                } else {
                    piezas[i][j].setImageBitmap(piezasImagen.get(nuevoEstado[i][j] - 1));
                }
            }
        }
    }
    public int[][] obtenerEstadoActual() {
        int[][] estado = new int[tamanoRompecabezas][tamanoRompecabezas];

        for (int i = 0; i < tamanoRompecabezas; i++) {
            for (int j = 0; j < tamanoRompecabezas; j++) {
                if (piezas[i][j].getDrawable() == null) {
                    estado[i][j] = 0;
                } else {
                    Bitmap bitmapActual = ((BitmapDrawable) piezas[i][j].getDrawable()).getBitmap();
                    int index = piezasImagen.indexOf(bitmapActual);
                    estado[i][j] = index + 1;
                }
            }
        }
        return estado;
    }

}