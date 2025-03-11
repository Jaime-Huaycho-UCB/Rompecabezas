package com.example.rompecabezas;

import java.util.*;

public class SolverAStar {
    private final int[][] meta = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private final int[][] movimientos = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public List<NodoPuzzle> resolver(int[][] estadoInicial) {
        PriorityQueue<NodoPuzzle> abiertos = new PriorityQueue<>();
        Set<String> visitados = new HashSet<>();

        int x = -1, y = -1;
        for (int i = 0; i < estadoInicial.length; i++) {
            for (int j = 0; j < estadoInicial[i].length; j++) {
                if (estadoInicial[i][j] == 0) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }

        NodoPuzzle inicial = new NodoPuzzle(estadoInicial, x, y, 0, null);
        abiertos.add(inicial);

        while (!abiertos.isEmpty()) {
            NodoPuzzle actual = abiertos.poll();
            if (Arrays.deepEquals(actual.estado, meta)) {
                return reconstruirCamino(actual);
            }

            String clave = Arrays.deepToString(actual.estado);
            if (visitados.contains(clave)) continue;
            visitados.add(clave);

            for (int[] mov : movimientos) {
                int nx = actual.x + mov[0], ny = actual.y + mov[1];
                if (nx >= 0 && ny >= 0 && nx < estadoInicial.length && ny < estadoInicial[0].length) {
                    int[][] nuevoEstado = actual.copiarMatriz(actual.estado);
                    nuevoEstado[actual.x][actual.y] = nuevoEstado[nx][ny];
                    nuevoEstado[nx][ny] = 0;
                    abiertos.add(new NodoPuzzle(nuevoEstado, nx, ny, actual.costo + 1, actual));
                }
            }
        }
        return null;
    }

    private List<NodoPuzzle> reconstruirCamino(NodoPuzzle nodo) {
        List<NodoPuzzle> camino = new ArrayList<>();
        while (nodo != null) {
            camino.add(nodo);
            nodo = nodo.padre;
        }
        Collections.reverse(camino);
        return camino;
    }
}

