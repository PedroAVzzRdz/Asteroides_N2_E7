package com.example.asteroidesreal;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class AcercaDeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acercade);

        // Ajuste de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_acercade), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.textViewAcerca);
        textView.setText("Asteroides - Versión 1.0\n\nJuego desarrollado en Android.\n\n© 2025 Tu Nombre");
        
        // Mostrar diálogo emergente al iniciar
        mostrarDialogoAcercaDe();
    }
    
    private void mostrarDialogoAcercaDe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acerca de");
        builder.setMessage("Este programa ha sido desarrollado como ejemplo en el curso de Android...");
        builder.setPositiveButton("Aceptar", null);
        builder.setCancelable(true);
        builder.show();
    }
}
