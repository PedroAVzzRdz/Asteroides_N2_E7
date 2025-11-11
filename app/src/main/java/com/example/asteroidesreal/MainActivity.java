package com.example.asteroidesreal;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable; // Importación necesaria
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private ImageView logo;
    private ConstraintLayout layout;

    // **NUEVA VARIABLE:** Referencia al ImageView de la animación
    private ImageView animationLoopView;
    // **NUEVA VARIABLE:** Objeto para controlar la animación de fotogramas
    private AnimationDrawable animacionLoop;

    // Variable para manejar la música de fondo
    private MediaPlayer mediaPlayer;
    
    // Vista de aliens
    private AlienView alienView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Inicializar orientación
        lastOrientation = getResources().getConfiguration().orientation;

        layout = findViewById(R.id.main);
        logo = findViewById(R.id.logo);
        
        // Obtener referencia al contenedor de botones
        buttonsContainer = findViewById(R.id.buttonsContainer);

        // ====== INICIALIZACIÓN DE LA VISTA DE ALIENS ======
        alienView = findViewById(R.id.alienView);
        if (alienView != null) {
            // La animación de aliens se maneja automáticamente en AlienView
        }

        // ====== INICIALIZACIÓN DE LA ANIMACIÓN DE FOTOGRAMAS ======
        animationLoopView = findViewById(R.id.animationLoop);
        if (animationLoopView != null) {
            // Asume que el background ya fue establecido en el XML con @drawable/animacion_g
            // Obtenemos el AnimationDrawable del fondo del ImageView
            animacionLoop = (AnimationDrawable) animationLoopView.getBackground();
            // Fijar el tamaño para evitar que cambie al rotar
            android.view.ViewGroup.LayoutParams params = animationLoopView.getLayoutParams();
            if (params != null) {
                params.width = (int) (250 * getResources().getDisplayMetrics().density);
                params.height = (int) (252 * getResources().getDisplayMetrics().density);
                animationLoopView.setLayoutParams(params);
            }
            animationLoopView.setScaleType(ImageView.ScaleType.FIT_XY);
            animationLoopView.setAdjustViewBounds(false);
            animationLoopView.setMinimumWidth((int) (250 * getResources().getDisplayMetrics().density));
            animationLoopView.setMinimumHeight((int) (252 * getResources().getDisplayMetrics().density));
        }

        // ====== AJUSTE DE BARRAS DEL SISTEMA ======
        ViewCompat.setOnApplyWindowInsetsListener(layout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ====== ANIMACIÓN SUAVE DEL LOGO ======
        if (logo != null) {
            Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
            logo.startAnimation(animLogo);
        }

        // ====== FONDO ANIMADO SUAVE (NEBULOSA) ======
        setupBackgroundAnimation();

        // ====== INICIALIZAR MÚSICA SEGÚN PREFERENCIAS ======
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean musicaHabilitada = prefs.getBoolean("musica_on", true);
        
        if (musicaHabilitada) {
            mediaPlayer = MediaPlayer.create(this, R.raw.pixel_galaxy);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true); // Repetir la canción
            }
        }

        // ====== BOTONES ======
        Button btnJugar = findViewById(R.id.button);
        Button btnConfig = findViewById(R.id.button2);
        Button btnAcerca = findViewById(R.id.button3);
        Button btnPuntajes = findViewById(R.id.button5);
        Button btnSalir = findViewById(R.id.button4);

        // Animación de click para todos los botones (sin animaciones continuas)
        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        btnJugar.setOnClickListener(v -> {
            // Aplicar animación de click temporal (se superpone a la animación continua)
            v.startAnimation(clickAnimation);
            // Pequeño delay para ver la animación de click antes de cambiar de actividad
            v.postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, Juego.class));
            }, 200);
        });
        
        btnConfig.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }, 200);
        });
        
        btnAcerca.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                // Mostrar diálogo emergente en lugar de abrir actividad
                mostrarDialogoAcercaDe();
            }, 200);
        });
        
        btnPuntajes.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, PuntajesActivity.class));
            }, 200);
        });
        
        btnSalir.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                finish();
            }, 200);
        });
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

    // --- GESTIÓN DE RECURSOS EN EL CICLO DE VIDA ---

    @Override
    protected void onStart() {
        super.onStart();

        // Verificar preferencias de música antes de iniciar
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean musicaHabilitada = prefs.getBoolean("musica_on", true);

        // Iniciar la música solo si está habilitada
        if (musicaHabilitada) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.pixel_galaxy);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                }
            }
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } else {
            // Si la música está deshabilitada, detener y liberar el MediaPlayer
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        // **INICIAR LA ANIMACIÓN DE FOTOGRAMAS**
        if (animacionLoop != null && !animacionLoop.isRunning()) {
            animacionLoop.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Verificar preferencias de música al volver a la actividad
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean musicaHabilitada = prefs.getBoolean("musica_on", true);

        if (musicaHabilitada) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.pixel_galaxy);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                }
            }
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } else {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Pausar la música
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // **DETENER LA ANIMACIÓN DE FOTOGRAMAS** para ahorrar recursos
        if (animacionLoop != null && animacionLoop.isRunning()) {
            animacionLoop.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private int lastOrientation = -1;
    private android.widget.LinearLayout buttonsContainer;
    
    // ====== Evitar crash al rotar y forzar layout correcto ======
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        // Detectar cambio de orientación
        int currentOrientation = newConfig.orientation;
        
        // Cambiar orientación de botones dinámicamente
        if (buttonsContainer != null) {
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Landscape: botones horizontales
                buttonsContainer.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                // Ajustar layout params para distribución horizontal
                android.view.ViewGroup.LayoutParams params = buttonsContainer.getLayoutParams();
                if (params instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams cParams = 
                        (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) params;
                    cParams.width = 0; // match_constraint
                    cParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                    buttonsContainer.setLayoutParams(cParams);
                }
                // Ajustar tamaño de botones para landscape - mejor espaciado
                for (int i = 0; i < buttonsContainer.getChildCount(); i++) {
                    android.view.View child = buttonsContainer.getChildAt(i);
                    if (child instanceof Button) {
                        android.widget.LinearLayout.LayoutParams childParams = 
                            (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                        childParams.width = 0; // match_constraint
                        childParams.height = (int) (65 * getResources().getDisplayMetrics().density); // Más bajo
                        childParams.weight = 1.0f;
                        // Márgenes más grandes para mejor espaciado
                        childParams.setMargins(
                            (int) (4 * getResources().getDisplayMetrics().density), // start
                            0, // top
                            (int) (4 * getResources().getDisplayMetrics().density), // end
                            0  // bottom
                        );
                        child.setLayoutParams(childParams);
                        // Ajustar tamaño del texto según el botón
                        Button btn = (Button) child;
                        String texto = btn.getText().toString();
                        if (texto.equals("JUGAR") || texto.equals("SALIR")) {
                            btn.setTextSize(14);
                        } else {
                            btn.setTextSize(12); // Texto más pequeño para botones largos
                        }
                    }
                }
            } else {
                // Portrait: botones verticales
                buttonsContainer.setOrientation(android.widget.LinearLayout.VERTICAL);
                // Ajustar layout params para distribución vertical
                android.view.ViewGroup.LayoutParams params = buttonsContainer.getLayoutParams();
                if (params instanceof androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) {
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams cParams = 
                        (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) params;
                    cParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                    cParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                    buttonsContainer.setLayoutParams(cParams);
                }
                // Ajustar tamaño de botones para portrait
                for (int i = 0; i < buttonsContainer.getChildCount(); i++) {
                    android.view.View child = buttonsContainer.getChildAt(i);
                    if (child instanceof Button) {
                        android.widget.LinearLayout.LayoutParams childParams = 
                            (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                        childParams.width = (int) (220 * getResources().getDisplayMetrics().density);
                        childParams.height = (int) (75 * getResources().getDisplayMetrics().density);
                        childParams.weight = 0.0f;
                        childParams.setMargins(0, 0, 0, 0);
                        child.setLayoutParams(childParams);
                        // Restaurar tamaño de texto original para portrait
                        Button btn = (Button) child;
                        String texto = btn.getText().toString();
                        if (texto.equals("JUGAR")) {
                            btn.setTextSize(24);
                        } else {
                            btn.setTextSize(20);
                        }
                    }
                }
            }
        }
        
        lastOrientation = currentOrientation;
        
        // Mantener el tamaño del ImageView de animación al rotar
        if (animationLoopView != null) {
            // Forzar el tamaño original para evitar que se encoja
            android.view.ViewGroup.LayoutParams params = animationLoopView.getLayoutParams();
            if (params != null) {
                int fixedWidth = (int) (250 * getResources().getDisplayMetrics().density);
                int fixedHeight = (int) (252 * getResources().getDisplayMetrics().density);
                params.width = fixedWidth;
                params.height = fixedHeight;
                animationLoopView.setLayoutParams(params);
            }
            animationLoopView.setScaleType(ImageView.ScaleType.FIT_XY);
            animationLoopView.setAdjustViewBounds(false);
            animationLoopView.setMinimumWidth((int) (250 * getResources().getDisplayMetrics().density));
            animationLoopView.setMinimumHeight((int) (252 * getResources().getDisplayMetrics().density));
            animationLoopView.setMaxWidth((int) (250 * getResources().getDisplayMetrics().density));
            animationLoopView.setMaxHeight((int) (252 * getResources().getDisplayMetrics().density));
            // Invalidar para forzar redibujado
            animationLoopView.invalidate();
        }
        
        // Mantener el tamaño del logo al rotar
        if (logo != null) {
            // Forzar el tamaño original según la orientación
            android.view.ViewGroup.LayoutParams logoParams = logo.getLayoutParams();
            if (logoParams != null) {
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // Landscape: tamaño fijo de 200dp
                    int fixedWidth = (int) (200 * getResources().getDisplayMetrics().density);
                    logoParams.width = fixedWidth;
                    logo.setMaxWidth(fixedWidth);
                    logo.setMinimumWidth(fixedWidth);
                } else {
                    // Portrait: tamaño fijo de 300dp
                    int fixedWidth = (int) (300 * getResources().getDisplayMetrics().density);
                    logoParams.width = fixedWidth;
                    logo.setMaxWidth(fixedWidth);
                    logo.setMinimumWidth(fixedWidth);
                }
                logo.setLayoutParams(logoParams);
            }
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            logo.setAdjustViewBounds(true);
            // Invalidar para forzar redibujado
            logo.invalidate();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar la orientación actual
        outState.putInt("lastOrientation", getResources().getConfiguration().orientation);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lastOrientation = savedInstanceState.getInt("lastOrientation", -1);
        }
    }
    
    private void mostrarDialogoAcercaDe() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Acerca de");
        builder.setMessage("Este juego ha sido una práctica del curso de Android.");
        builder.setPositiveButton("Aceptar", null);
        builder.setCancelable(true);
        builder.show();
    }
}