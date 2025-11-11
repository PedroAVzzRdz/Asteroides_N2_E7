package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarViewGame extends View {
    private Paint paint = new Paint();
    private Random random = new Random();
    private List<Star> stars = new ArrayList<>();
    private List<Star> bigStars = new ArrayList<>();

    // ---- Estrella normal ----
    private class Star {
        float x, y;
        float alpha;
        boolean fading;
        float size;
        float blinkSpeed; // Velocidad individual de parpadeo
    }

    // ---- Estrella fugaz ----
    private class ShootingStar {
        float x, y;
        float speedX, speedY;
        float curvature;
        float alpha = 1f;
        boolean active = true;

        ShootingStar(float startX, float startY) {
            this.x = startX;
            this.y = startY;
            this.speedX = -15 - random.nextInt(10);
            this.speedY = 8 + random.nextInt(5);
            this.curvature = (random.nextFloat() - 0.5f) * 0.3f;
        }

        void update() {
            x += speedX;
            y += speedY + curvature * (x / 50f);
            alpha -= 0.02f;
            if (alpha <= 0) active = false;
        }
    }

    private List<ShootingStar> shootingStars = new ArrayList<>();
    private final int FRAME_DURATION_MS = 16; // Aproximadamente 60 FPS

    public StarViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Forzar color blanco explícitamente, ignorando modo oscuro
        paint.setColor(0xFFFFFFFF); // Blanco absoluto (ARGB)
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        
        // Deshabilitar modo oscuro para esta vista
        setForceDarkAllowed(false);
        
        initStars();
    }

    private void initStars() {
        int width = getWidth() > 0 ? getWidth() : 1080;
        int height = getHeight() > 0 ? getHeight() : 1920;
        
        // Estrellas pequeñas (aumentadas para más efecto)
        for (int i = 0; i < 150; i++) {
            Star s = new Star();
            s.x = random.nextInt(width);
            s.y = random.nextInt(height);
            s.alpha = 0.3f + random.nextFloat() * 0.7f; // Alpha entre 0.3 y 1.0
            s.fading = random.nextBoolean();
            s.size = 1.5f + random.nextFloat() * 1.5f; // Tamaño variable entre 1.5 y 3
            s.blinkSpeed = 0.008f + (random.nextFloat() * 0.015f);
            stars.add(s);
        }

        // Estrellas medianas (nuevas)
        for (int i = 0; i < 50; i++) {
            Star s = new Star();
            s.x = random.nextInt(width);
            s.y = random.nextInt(height);
            s.alpha = 0.4f + random.nextFloat() * 0.6f;
            s.fading = random.nextBoolean();
            s.size = 3f + random.nextFloat() * 2f; // Tamaño entre 3 y 5
            s.blinkSpeed = 0.008f + (random.nextFloat() * 0.015f);
            stars.add(s);
        }

        // Estrellas grandes
        for (int i = 0; i < 40; i++) {
            Star s = new Star();
            s.x = random.nextInt(width);
            s.y = random.nextInt(height);
            s.alpha = 0.5f + random.nextFloat() * 0.5f;
            s.fading = random.nextBoolean();
            s.size = 5f + random.nextFloat() * 3f; // Tamaño entre 5 y 8
            s.blinkSpeed = 0.008f + (random.nextFloat() * 0.015f);
            bigStars.add(s);
        }
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Reinicializar estrellas cuando cambia el tamaño (incluyendo rotación)
        // Verificar si el tamaño cambió significativamente (rotación)
        if (w > 0 && h > 0) {
            // Si es la primera vez (oldw == 0 || oldh == 0) o si cambió la orientación
            boolean cambioOrientacion = (oldw > 0 && oldh > 0) && 
                                       ((oldw < oldh && w > h) || (oldw > oldh && w < h));
            
            if (oldw == 0 || oldh == 0 || cambioOrientacion) {
                stars.clear();
                bigStars.clear();
                shootingStars.clear();
                initStars();
            } else {
                // Si solo cambió el tamaño pero no la orientación, ajustar posiciones
                float scaleX = (float) w / oldw;
                float scaleY = (float) h / oldh;
                
                for (Star s : stars) {
                    s.x *= scaleX;
                    s.y *= scaleY;
                    // Asegurar que estén dentro de los límites
                    if (s.x < 0) s.x = 0;
                    if (s.x >= w) s.x = w - 1;
                    if (s.y < 0) s.y = 0;
                    if (s.y >= h) s.y = h - 1;
                }
                
                for (Star s : bigStars) {
                    s.x *= scaleX;
                    s.y *= scaleY;
                    // Asegurar que estén dentro de los límites
                    if (s.x < 0) s.x = 0;
                    if (s.x >= w) s.x = w - 1;
                    if (s.y < 0) s.y = 0;
                    if (s.y >= h) s.y = h - 1;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        // Forzar color blanco antes de dibujar (evita problemas con modo oscuro)
        paint.setStyle(Paint.Style.FILL);
        paint.setColorFilter(null); // Eliminar cualquier filtro de color
        paint.setAntiAlias(true);

        // Dibujar estrellas pequeñas (blancas)
        for (Star s : stars) {
            int alpha = Math.max(100, Math.min(255, (int) (s.alpha * 255)));
            // Usar Color.argb para asegurar que el color sea siempre blanco
            paint.setColor(Color.argb(alpha, 255, 255, 255)); // Blanco con alpha explícito
            paint.setColorFilter(null); // Asegurar que no hay filtros
            canvas.drawCircle(s.x, s.y, s.size, paint);
            updateAlpha(s);
        }

        // Dibujar estrellas grandes (blancas)
        for (Star s : bigStars) {
            int alpha = Math.max(150, Math.min(255, (int) (s.alpha * 255)));
            // Usar Color.argb para asegurar que el color sea siempre blanco
            paint.setColor(Color.argb(alpha, 255, 255, 255)); // Blanco con alpha explícito
            paint.setColorFilter(null); // Asegurar que no hay filtros
            canvas.drawCircle(s.x, s.y, s.size, paint);
            updateAlpha(s);
        }

        // Dibujar estrellas fugaces (SIN SATURNO)
        drawShootingStars(canvas);

        postInvalidateDelayed(FRAME_DURATION_MS);
    }

    private void updateAlpha(Star s) {
        // Usar velocidad individual de parpadeo para cada estrella
        float speed = s.blinkSpeed;
        
        if (s.fading) {
            s.alpha -= speed;
            if (s.alpha <= 0.2f) { // Mantener mínimo de visibilidad
                s.alpha = 0.2f;
                s.fading = false;
            }
        } else {
            s.alpha += speed;
            if (s.alpha >= 1.0f) {
                s.alpha = 1.0f;
                s.fading = true;
            }
        }
    }

    private void drawShootingStars(Canvas canvas) {
        // Crear estrella fugaz con baja probabilidad
        if (random.nextInt(250) == 0) {
            shootingStars.add(new ShootingStar(getWidth(), random.nextInt(getHeight() / 2)));
        }

        List<ShootingStar> toRemove = new ArrayList<>();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColorFilter(null); // Eliminar cualquier filtro de color
        for (ShootingStar ss : shootingStars) {
            if (!ss.active) {
                toRemove.add(ss);
                continue;
            }
            int alpha = Math.max(0, Math.min(255, (int) (ss.alpha * 255)));
            paint.setColor(Color.argb(alpha, 255, 255, 255)); // Blanco con alpha explícito
            paint.setColorFilter(null); // Asegurar que no hay filtros
            canvas.drawLine(ss.x, ss.y, ss.x + 40, ss.y - 10, paint);
            ss.update();
        }
        shootingStars.removeAll(toRemove);
    }
}



