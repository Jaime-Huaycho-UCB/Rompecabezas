package com.example.rompecabezas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.IOException;

public class FragmentInicio extends Fragment {
    private int tamanoRompecabezas = 3;
    private Uri imagenUri;
    private EditText inputTamano;

    public FragmentInicio() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnGaleria = view.findViewById(R.id.btn_galeria);
        Button btnCamara = view.findViewById(R.id.btn_camara);
        Button btnIniciar = view.findViewById(R.id.btn_iniciar);
        inputTamano = view.findViewById(R.id.input_tamano);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTexto = inputTamano.getText().toString();
                if (!inputTexto.isEmpty()) {
                    int tamanoIngresado = Integer.parseInt(inputTexto);
                    if (tamanoIngresado >= 2 && tamanoIngresado <= 10) {
                        tamanoRompecabezas = tamanoIngresado;
                    } else {
                        tamanoRompecabezas = 3;
                    }
                }

                if (imagenUri == null) {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("tamano_rompecabezas", tamanoRompecabezas);
                bundle.putParcelable("imagen_uri", imagenUri);
                FragmentJuego fragmentJuego = new FragmentJuego();
                fragmentJuego.setArguments(bundle);
                ((MainActivity) requireActivity()).cambiarFragmento(fragmentJuego);
            }
        });

        Button btnRecords = view.findViewById(R.id.btn_records);
        btnRecords.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).cambiarFragmento(new FragmentRecords());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && data != null) {
            if (requestCode == 1) { // Galería
                imagenUri = data.getData();
            } else if (requestCode == 2) { // Cámara
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                imagenUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        requireActivity().getContentResolver(), foto, "Nueva Foto", null));
            }
        }
    }
}
