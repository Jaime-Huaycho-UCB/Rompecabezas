package com.example.rompecabezas;

import android.os.Handler;
import java.util.List;

public class AnimadorRompecabezas {
    private FragmentJuego fragmentJuego;
    private Handler handler = new Handler();

    public AnimadorRompecabezas(FragmentJuego fragmentJuego) {
        this.fragmentJuego = fragmentJuego;
    }

    public void animarSolucion(List<NodoPuzzle> solucion) {
        new Thread(() -> {
            for (NodoPuzzle paso : solucion) {
                handler.post(() -> fragmentJuego.actualizarRompecabezas(paso.estado));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

