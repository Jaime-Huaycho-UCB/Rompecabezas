package com.example.rompecabezas;

import java.util.Arrays;

public class NodoPuzzle implements Comparable<NodoPuzzle>{
    int[][] estado;
    int x, y;
    int costo, heuristica;
    NodoPuzzle padre;

    public NodoPuzzle(int[][] estado, int x, int y, int costo, NodoPuzzle padre) {
        this.estado = copiarMatriz(estado);
        this.x = x;
        this.y = y;
        this.costo = costo;
        this.heuristica = calcularHeuristica();
        this.padre = padre;
    }

    private int calcularHeuristica() {
        int h = 0;
        int n = estado.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (estado[i][j] != 0) {
                    int valor = estado[i][j] - 1;
                    int filaObjetivo = valor / n;
                    int columnaObjetivo = valor % n;
                    h += Math.abs(i - filaObjetivo) + Math.abs(j - columnaObjetivo);
                }
            }
        }
        return h;
    }

    public int[][] copiarMatriz(int[][] matriz) {
        int[][] copia = new int[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++)
            copia[i] = Arrays.copyOf(matriz[i], matriz[i].length);
        return copia;
    }

    @Override
    public int compareTo(NodoPuzzle otro) {
        return (this.costo + this.heuristica) - (otro.costo + otro.heuristica);
    }
}
