package com.example.asteroidesreal;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        layout = findViewById(R.id.main);

        // Ajustar padding por sistema
        ViewCompat.setOnApplyWindowInsetsListener(layout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ====== FONDO ANIMADO SUAVE (NEBULOSA) ======
        setupBackgroundAnimation();

        // Botón de regresar
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Spinner de tipo de gráficos
        Spinner spinnerGraficos = findViewById(R.id.spinnerGraficos);
        // Preview de la nave
        ImageView imageViewPreview = findViewById(R.id.imageViewPreview);

        // Switch de música
        Switch switchMusica = findViewById(R.id.switchMusica);
        // Switches de controles
        Switch switchTeclado = findViewById(R.id.switchTeclado);
        Switch switchTactil = findViewById(R.id.switchTactil);
        Switch switchSensores = findViewById(R.id.switchSensores);
        
        // SeekBar para número de fragmentos
        android.widget.SeekBar seekBarFragmentos = findViewById(R.id.seekBarFragmentos);
        android.widget.TextView tvFragmentos = findViewById(R.id.tvFragmentos);

        // SharedPreferences para guardar configuraciones
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        // Inicializar switches con valores guardados
        switchMusica.setChecked(prefs.getBoolean("musica_on", true));
        switchTeclado.setChecked(prefs.getBoolean("entrada_teclado", true));
        switchTactil.setChecked(prefs.getBoolean("entrada_tactil", true));
        switchSensores.setChecked(prefs.getBoolean("entrada_sensor", true));
        
        // Inicializar SeekBar de fragmentos
        int numFragmentos = prefs.getInt("num_fragmentos", 5);
        seekBarFragmentos.setProgress(numFragmentos);
        tvFragmentos.setText("Número de Fragmentos: " + numFragmentos);

        // Listener para switches
        switchMusica.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("musica_on", isChecked).apply()
        );
        switchTeclado.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("entrada_teclado", isChecked).apply()
        );
        switchTactil.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("entrada_tactil", isChecked).apply()
        );
        switchSensores.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("entrada_sensor", isChecked).apply()
        );
        
        // Listener para SeekBar de fragmentos
        seekBarFragmentos.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int numFragmentos = Math.max(2, progress); // Mínimo 2 fragmentos
                    tvFragmentos.setText("Número de Fragmentos: " + numFragmentos);
                    prefs.edit().putInt("num_fragmentos", numFragmentos).apply();
                }
            }
            
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });

        // Listener del spinner para cambiar preview y guardar selección
        spinnerGraficos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Guardar selección
                prefs.edit().putInt("graficos_mode", position).apply();

                // Cambiar preview de la nave
                if (position == 0) {
                    imageViewPreview.setImageResource(R.drawable.navevector); // vector imagen
                } else if (position == 1) {
                    imageViewPreview.setImageResource(R.drawable.nave1); // normal
                } else {
                    // posición == 2: vector puro - mostrar un triángulo simple o la nave vector
                    imageViewPreview.setImageResource(R.drawable.navevector); // vector puro
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Inicializar preview y spinner según valor guardado
        int modoGuardado = prefs.getInt("graficos_mode", 0);
        spinnerGraficos.setSelection(modoGuardado);
        if (modoGuardado == 0) {
            imageViewPreview.setImageResource(R.drawable.navevector);
        } else if (modoGuardado == 1) {
            imageViewPreview.setImageResource(R.drawable.nave1);
        } else {
            imageViewPreview.setImageResource(R.drawable.navevector);
        }
    }

    private void setupBackgroundAnimation() {
        final int[] colors = new int[]{
                Color.parseColor("#4B0082"), // morado
                Color.parseColor("#000033"), // azul oscuro
                Color.parseColor("#FF00FF")  // fucsia
        };

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{colors[0], colors[1]}
        );
        layout.setBackground(gradientDrawable);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(8000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();
            int colorStart = (Integer) new ArgbEvaluator().evaluate(fraction, colors[0], colors[1]);
            int colorEnd = (Integer) new ArgbEvaluator().evaluate(fraction, colors[1], colors[2]);
            gradientDrawable.setColors(new int[]{colorStart, colorEnd});
        });
        animator.start();
    }
}
