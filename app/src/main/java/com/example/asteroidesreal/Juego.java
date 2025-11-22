package com.example.asteroidesreal;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Juego extends Activity {
    private VistaJuego vistaJuego;
    private View starViewGame;
    private ImageButton btnPause;
    private ImageButton btnMute;
    private ConstraintLayout mainLayout;
    private boolean pausado = false;
    private CountdownView countdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        mainLayout = findViewById(R.id.mainGameLayout);
        vistaJuego = findViewById(R.id.VistaJuego);
        starViewGame = findViewById(R.id.starViewGame);
        countdownView = findViewById(R.id.countdownView);
        
        // Verificar modo de gráficos
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        int modoGraficos = pref.getInt("graficos_mode", 0);
        
        if (modoGraficos == 2) {
            // Modo vector puro: fondo negro, ocultar StarViewGame
            mainLayout.setBackgroundColor(android.graphics.Color.BLACK);
            starViewGame.setVisibility(View.GONE);
        } else {
            // Modos normales: fondo animado y StarViewGame visible
            setupBackgroundAnimation();
            starViewGame.setVisibility(View.VISIBLE);
        }

        ImageButton btnBackGame = findViewById(R.id.btnBackGame);
        btnBackGame.setOnClickListener(v -> {
            // Guardar estado antes de salir
            if (vistaJuego != null) {
                // Detener completamente la música antes de salir
                vistaJuego.detenerJuego();
                vistaJuego.guardarEstadoJuego();
                vistaJuego.limpiarEstadoJuego();
            }
            finish();
        });
        
        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(v -> togglePausa());
        
        btnMute = findViewById(R.id.btnMute);
        btnMute.setOnClickListener(v -> toggleMute());
        // Inicializar el icono después de que vistaJuego esté listo
        if (vistaJuego != null) {
            actualizarIconoMute();
        } else {
            // Si vistaJuego aún no está listo, usar el icono por defecto (sonido activo)
            btnMute.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
        
        // Verificar si el juego ya estaba en curso (para evitar reinicio al rotar)
        SharedPreferences gameState = getSharedPreferences("game_state", MODE_PRIVATE);
        boolean juegoEnCurso = gameState.getBoolean("juego_en_curso", false);
        
        if (juegoEnCurso && vistaJuego != null) {
            // Si hay un juego en curso, no mostrar countdown, solo reanudar
            // El estado ya se restauró en el constructor de VistaJuego
            // Esperar un momento para que la vista se inicialice completamente
            vistaJuego.post(() -> {
                if (vistaJuego.isJuegoIniciado()) {
                    // El juego ya está iniciado, solo asegurar que no esté pausado
                    vistaJuego.setPausado(false);
                    countdownView.setVisibility(View.GONE);
                } else {
                    // Si por alguna razón no está iniciado, iniciarlo
                    vistaJuego.iniciarJuego();
                    vistaJuego.setPausado(false);
                    countdownView.setVisibility(View.GONE);
                }
            });
        } else {
            // Iniciar cuenta regresiva solo si es un juego nuevo
            iniciarCountdown();
        }
        
        // Configurar listener para countdown cuando se avanza de nivel
        if (vistaJuego != null) {
            vistaJuego.setOnCountdownRequestListener(() -> {
                iniciarCountdown();
            });
        }
    }
    
    private void iniciarCountdown() {
        try {
            // Verificar que la vista y el countdown existan
            if (vistaJuego == null) {
                return;
            }
            
            // Si no hay countdown, continuar sin countdown
            if (countdownView == null) {
                vistaJuego.setEstadoMisionNormal();
                vistaJuego.setPausado(false);
                if (!vistaJuego.isJuegoIniciado()) {
                    vistaJuego.iniciarJuego();
                }
                vistaJuego.guardarEstadoJuego();
                return;
            }
            
            // Guardar estado antes de pausar para el countdown
            vistaJuego.guardarEstadoJuego();
            
            // Cambiar estado a NORMAL cuando el countdown está a punto de comenzar
            // Esto asegura que cuando termine el countdown, el juego pueda continuar normalmente
            vistaJuego.setEstadoMisionNormal();
            
            // Pausar el juego para el countdown
            vistaJuego.setPausado(true);
            
            // Asegurar que el juego esté marcado como iniciado
            if (!vistaJuego.isJuegoIniciado()) {
                vistaJuego.iniciarJuego();
            }
            
            // Verificar que los asteroides existan antes de mostrar el countdown
            if (vistaJuego.getWidth() > 0 && vistaJuego.getHeight() > 0) {
                try {
                    vistaJuego.generarAsteroidesSiNecesario();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Si las dimensiones no están listas, esperar un momento y generar asteroides
                countdownView.postDelayed(() -> {
                    if (vistaJuego != null && vistaJuego.getWidth() > 0 && vistaJuego.getHeight() > 0) {
                        try {
                            vistaJuego.generarAsteroidesSiNecesario();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 50);
            }
            
            // Mostrar vista de countdown (encima de todo)
            countdownView.setVisibility(View.VISIBLE);
            countdownView.bringToFront();
            countdownView.setTextSize(140f);
            countdownView.setAlpha(1.0f);
            
            // Secuencia: "¿PREPARADO?" -> "¡ADELANTE!"
            countdownView.showText("¿PREPARADO?");
            countdownView.setTextColor(Color.rgb(233, 30, 99)); // Rosa brillante
            
            // Después de 2.5 segundos, cambiar a "¡ADELANTE!"
            countdownView.postDelayed(() -> {
                if (countdownView == null || vistaJuego == null) {
                    // Si las vistas se destruyeron, asegurar que el juego continúe
                    if (vistaJuego != null) {
                        vistaJuego.setPausado(false);
                        vistaJuego.setEstadoMisionNormal();
                        vistaJuego.guardarEstadoJuego();
                    }
                    return;
                }
                
                try {
                    countdownView.showText("¡ADELANTE!");
                    countdownView.setTextColor(Color.rgb(171, 71, 188)); // Morado brillante
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Después de 1.5 segundos, ocultar y empezar el juego
                countdownView.postDelayed(() -> {
                    if (countdownView == null || vistaJuego == null) {
                        // Si las vistas se destruyeron, asegurar que el juego continúe
                        if (vistaJuego != null) {
                            vistaJuego.setPausado(false);
                            vistaJuego.setEstadoMisionNormal();
                            vistaJuego.guardarEstadoJuego();
                        }
                        return;
                    }
                    
                    try {
                        countdownView.hide();
                        countdownView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    // Asegurar que el juego no esté pausado
                    vistaJuego.setPausado(false);
                    vistaJuego.setEstadoMisionNormal();
                    
                    // Verificar que los asteroides existan antes de continuar
                    if (vistaJuego.getWidth() > 0 && vistaJuego.getHeight() > 0) {
                        try {
                            vistaJuego.generarAsteroidesSiNecesario();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Asegurar que el juego continúe
                    if (!vistaJuego.isJuegoIniciado()) {
                        vistaJuego.iniciarJuego();
                    } else {
                        vistaJuego.setPausado(false);
                    }
                    
                    // Forzar reproducción de música después del countdown
                    vistaJuego.postDelayed(() -> {
                        if (vistaJuego != null && vistaJuego.isJuegoIniciado() && !vistaJuego.isPausado()) {
                            // Llamar al método privado a través de iniciarJuego que llama a reproducirMusicaJuego
                            vistaJuego.iniciarJuego();
                        }
                    }, 100);
                    
                    // Guardar estado después de iniciar
                    vistaJuego.guardarEstadoJuego();
                    
                    // Forzar invalidación para redibujar
                    vistaJuego.postInvalidate();
                }, 1500);
            }, 2500);
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay un error, asegurar que el juego continúe
            if (vistaJuego != null) {
                vistaJuego.setEstadoMisionNormal();
                vistaJuego.setPausado(false);
                if (!vistaJuego.isJuegoIniciado()) {
                    vistaJuego.iniciarJuego();
                }
                vistaJuego.guardarEstadoJuego();
            }
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
        mainLayout.setBackground(gradientDrawable);

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
    
    private AlertDialog dialogoPausa;
    
    private void togglePausa() {
        pausado = !pausado;
        if (vistaJuego != null) {
            vistaJuego.setPausado(pausado);
        }
        
        if (pausado) {
            btnPause.setImageResource(android.R.drawable.ic_media_play);
            mostrarDialogoPausa();
        } else {
            btnPause.setImageResource(android.R.drawable.ic_media_pause);
            if (dialogoPausa != null && dialogoPausa.isShowing()) {
                dialogoPausa.dismiss();
            }
        }
    }
    
    private void mostrarDialogoPausa() {
        if (dialogoPausa != null && dialogoPausa.isShowing()) {
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Juego Pausado");
        builder.setMessage("Presiona el botón de pausa para continuar");
        builder.setPositiveButton("Continuar", (dialog, which) -> {
            pausado = false;
            if (vistaJuego != null) {
                vistaJuego.setPausado(false);
            }
            btnPause.setImageResource(android.R.drawable.ic_media_pause);
        });
        builder.setNegativeButton("Salir", (dialog, which) -> {
            finish();
        });
        builder.setCancelable(false);
        dialogoPausa = builder.show();
    }
    
    private void toggleMute() {
        if (vistaJuego != null) {
            boolean nuevoEstado = !vistaJuego.isAudioMuteado();
            vistaJuego.setAudioMuteado(nuevoEstado);
            actualizarIconoMute();
        }
    }
    
    private void actualizarIconoMute() {
        if (btnMute != null && vistaJuego != null) {
            if (vistaJuego.isAudioMuteado()) {
                // Icono de silenciado
                btnMute.setImageResource(android.R.drawable.ic_lock_silent_mode);
            } else {
                // Icono de sonido activo
                btnMute.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            }
        }
    }
    
    
    private boolean isRotating = false; // Flag para detectar si estamos rotando
    private long lastRotationTime = 0; // Tiempo de la última rotación
    
    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar configuración de sensores cuando se resume la actividad
        // Esto es importante cuando se vuelve desde la configuración
        if (vistaJuego != null) {
            // Forzar actualización de sensores para asegurar que funcionen
            vistaJuego.actualizarConfiguracionSensores();
            // Si acabamos de rotar (menos de 2 segundos), mantener el juego activo
            long timeSinceRotation = System.currentTimeMillis() - lastRotationTime;
            if (timeSinceRotation <= 2000) {
                // Es una rotación reciente, mantener el juego activo
                if (!pausado) {
                    vistaJuego.setPausado(false);
                }
            } else {
                // No es una rotación reciente, manejar normalmente
                if (pausado) {
                    vistaJuego.setPausado(false);
                } else {
                    vistaJuego.setPausado(false); // Asegurar que esté activo
                }
            }
        }
        // Resetear flag de rotación después de un tiempo
        if (System.currentTimeMillis() - lastRotationTime > 3000) {
            isRotating = false;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Guardar estado cuando se pausa
        if (vistaJuego != null) {
            vistaJuego.guardarEstadoJuego();
        }
        
        // NUNCA pausar el juego durante la rotación o cooldown
        // La actividad no se destruye con configChanges, así que onPause solo se llama
        // cuando realmente pierde el foco (ej: otra app), no durante rotación
        long timeSinceRotation = System.currentTimeMillis() - lastRotationTime;
        // Si es una rotación reciente (menos de 5 segundos) o estamos rotando, NO pausar
        if (vistaJuego != null && (timeSinceRotation <= 5000 || isRotating)) {
            // Es una rotación reciente o estamos rotando, mantener el juego activo
            vistaJuego.setPausado(false);
            // Asegurar que el juego esté iniciado
            if (!vistaJuego.isJuegoIniciado()) {
                vistaJuego.iniciarJuego();
            }
        } else if (vistaJuego != null && timeSinceRotation > 5000 && !isRotating && !pausado) {
            // Solo pausar si no es una rotación reciente y no estamos rotando
            // Pero en la práctica, con configChanges, esto no debería pasar durante rotación
            vistaJuego.setPausado(true);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener música cuando se destruye la actividad
        if (vistaJuego != null) {
            // Detener juego (esto también detiene la música)
            vistaJuego.detenerJuego();
            // Guardar estado antes de destruir (por si acaso)
            vistaJuego.guardarEstadoJuego();
            // Solo limpiar si el juego terminó Y no estamos rotando
            // Verificar si es una rotación reciente (menos de 5 segundos)
            long timeSinceRotation = System.currentTimeMillis() - lastRotationTime;
            if (vistaJuego.isJuegoTerminado() && (timeSinceRotation > 5000 || !isRotating)) {
                vistaJuego.limpiarEstadoJuego();
            }
            vistaJuego.desactivarSensores();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar estado antes de que la actividad pueda ser destruida
        if (vistaJuego != null) {
            vistaJuego.guardarEstadoJuego();
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Guardar estado inmediatamente al rotar
        if (vistaJuego != null) {
            vistaJuego.guardarEstadoJuego();
        }
        
        // Marcar que estamos rotando para evitar pausar el juego
        isRotating = true;
        lastRotationTime = System.currentTimeMillis();
        
        // Asegurar que el juego continúe ejecutándose y activar cooldown
        if (vistaJuego != null) {
            // Asegurar que el juego esté iniciado
            if (!vistaJuego.isJuegoIniciado()) {
                vistaJuego.iniciarJuego();
            }
            // NUNCA pausar durante rotación - FORZAR que el juego continúe
            vistaJuego.setPausado(false);
            // Asegurar que el thread esté ejecutándose
            if (!vistaJuego.isThreadAlive()) {
                vistaJuego.iniciarThreadJuego();
            }
            // Iniciar cooldown de 3 segundos (esto NO pausa el juego, solo desactiva controles)
            vistaJuego.iniciarCooldownRotacion();
            // Forzar invalidación para asegurar que el juego se redibuje
            vistaJuego.postInvalidate();
        }
        
        // Forzar redibujado de StarViewGame para corregir el problema de división
        if (starViewGame != null) {
            starViewGame.invalidate();
            starViewGame.requestLayout();
        }
    }
}
