package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarView extends View {
    private Paint paint = new Paint();
    private Random random = new Random();
    private List<Star> stars = new ArrayList<>();
    private List<Star> bigStars = new ArrayList<>();
    private List<Bitmap> saturnFrames = new ArrayList<>();

    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMilliseconds = 120; // velocidad animación saturno

    // ... (Clases Star y ShootingStar sin cambios) ...

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
            this.curvature = (random.nextFloat() - 0.5f) * 0.3f; // trayectoria curva
        }

        void update() {
            x += speedX;
            y += speedY + curvature * (x / 50f);
            alpha -= 0.02f;
            if (alpha <= 0) active = false;
        }
    }

    private List<ShootingStar> shootingStars = new ArrayList<>();

    // Posición de saturno
    private float planetX, planetY;
    private int saturnWidth, saturnHeight;
    private final int FRAME_DURATION_MS = 16; // Aproximadamente 60 FPS

    // -------------------- Constructor para XML --------------------
    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Forzar color blanco explícitamente, ignorando modo oscuro
        paint.setColor(0xFFFFFFFF); // Blanco absoluto (ARGB)
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        
        // Deshabilitar modo oscuro para esta vista
        setForceDarkAllowed(false);

        // Cargar frames de saturno s0 - s11
        int[] saturnRes = {
                R.drawable.s0, R.drawable.s1, R.drawable.s2, R.drawable.s3,
                R.drawable.s4, R.drawable.s5, R.drawable.s6, R.drawable.s7,
                R.drawable.s8, R.drawable.s9, R.drawable.s10, R.drawable.s11
        };
        for (int res : saturnRes) {
            saturnFrames.add(BitmapFactory.decodeResource(getResources(), res));
        }

        initStars();
    }

    // ... (initStars() y onSizeChanged() sin cambios) ...

    private void initStars() {
        // Inicializar estrellas con tamaño de pantalla por defecto
        int width = getWidth() > 0 ? getWidth() : 1080;
        int height = getHeight() > 0 ? getHeight() : 1920;
        
        // Estrellas pequeñas (aumentadas para más efecto)
        for (int i = 0; i < 150; i++) {
            Star s = new Star();
            s.x = random.nextInt(width);
            s.y = random.nextInt(height);
            s.alpha = 0.3f + random.nextFloat() * 0.7f; // Alpha entre 0.3 y 1.0
            s.fading = random.nextBoolean();
            s.size = 1.5f + random.nextFloat() * 1.5f; // Tamaño variable
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
            s.size = 3f + random.nextFloat() * 2f;
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
        
        // Reinicializar estrellas con el nuevo tamaño
        if (w > 0 && h > 0 && (oldw == 0 || oldh == 0)) {
            stars.clear();
            bigStars.clear();
            initStars();
        }


        float scale = 0.45f;
        Bitmap original = saturnFrames.get(0);
        saturnWidth = (int) (original.getWidth() * scale);
        saturnHeight = (int) (original.getHeight() * scale);

        for (int i = 0; i < saturnFrames.size(); i++) {
            saturnFrames.set(i, Bitmap.createScaledBitmap(
                    saturnFrames.get(i), saturnWidth, saturnHeight, true
            ));
        }


        planetX = w - saturnWidth - 20;
        planetY = 20; // más arriba, sin tocar el título
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // BORRAR EL CANVAS para evitar el "parpadeo" de la animación anterior.
        // Si quieres que el ConstraintLayout de fondo se vea, usa TRANSPARENT.
        // Si quieres un fondo sólido en esta vista, usa Color.BLACK.
        canvas.drawColor(Color.TRANSPARENT);

        // Forzar color blanco antes de dibujar (evita problemas con modo oscuro)
        paint.setColorFilter(null); // Eliminar cualquier filtro de color
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Dibujar estrellas pequeñas
        for (Star s : stars) {
            int alpha = Math.max(100, Math.min(255, (int) (s.alpha * 255)));
            // Usar Color.argb para asegurar que el color sea siempre blanco
            paint.setColor(Color.argb(alpha, 255, 255, 255)); // Blanco con alpha explícito
            paint.setColorFilter(null); // Asegurar que no hay filtros
            canvas.drawCircle(s.x, s.y, s.size, paint);
            updateAlpha(s);
        }

        // Dibujar estrellas grandes
        for (Star s : bigStars) {
            int alpha = Math.max(150, Math.min(255, (int) (s.alpha * 255)));
            // Usar Color.argb para asegurar que el color sea siempre blanco
            paint.setColor(Color.argb(alpha, 255, 255, 255)); // Blanco con alpha explícito
            paint.setColorFilter(null); // Asegurar que no hay filtros
            canvas.drawCircle(s.x, s.y, s.size, paint);
            updateAlpha(s);
        }

        // Dibujar saturno animado
        drawSaturn(canvas);

        // Dibujar estrellas fugaces
        drawShootingStars(canvas);

        // **IMPORTANTE:** Llama a postInvalidateDelayed para redibujar a un ritmo constante.
        // 16ms equivale a ~60 FPS. Esto reemplaza el invalidate() simple.
        postInvalidateDelayed(FRAME_DURATION_MS);
    }

    // ... (updateAlpha() sin cambios) ...

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

    private void drawSaturn(Canvas canvas) {
        if (saturnFrames.size() == 0) return;

        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame = (currentFrame + 1) % saturnFrames.size();
        }

        // Asegurar que no haya filtros de color al dibujar Saturno
        paint.setColorFilter(null);
        paint.setAlpha(255);
        canvas.drawBitmap(saturnFrames.get(currentFrame), planetX, planetY, paint);
    }

    // ... (drawShootingStars() sin cambios) ...

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