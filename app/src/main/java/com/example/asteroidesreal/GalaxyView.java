package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GalaxyView extends View {
    private float rotationAngle = 0f;
    private Paint paintGalaxy;
    private Paint paintStars;
    private Paint paintCore;
    private long lastUpdate = 0;
    private static final float ROTATION_SPEED = 0.5f; // Grados por frame
    
    public GalaxyView(Context context) {
        super(context);
        init();
    }
    
    public GalaxyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        // Pintura para los brazos de la galaxia
        paintGalaxy = new Paint();
        paintGalaxy.setAntiAlias(true);
        paintGalaxy.setStyle(Paint.Style.STROKE);
        
        // Pintura para las estrellas en los brazos
        paintStars = new Paint();
        paintStars.setAntiAlias(true);
        paintStars.setStyle(Paint.Style.FILL);
        paintStars.setColor(Color.WHITE);
        
        // Pintura para el núcleo de la galaxia
        paintCore = new Paint();
        paintCore.setAntiAlias(true);
        paintCore.setStyle(Paint.Style.FILL);
        
        // Iniciar la animación
        lastUpdate = System.currentTimeMillis();
    }
        
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Actualizar rotación
        long now = System.currentTimeMillis();
        if (lastUpdate > 0) {
            long delta = now - lastUpdate;
            rotationAngle += ROTATION_SPEED * (delta / 16.67f); // Normalizar a 60 FPS
            if (rotationAngle >= 360f) rotationAngle -= 360f;
        }
        lastUpdate = now;
        
        // Guardar el estado del canvas
        canvas.save();
        
        // Rotar el canvas alrededor del centro
        canvas.rotate(rotationAngle, centerX, centerY);
        
        // Dibujar el núcleo brillante de la galaxia
        paintCore.setColor(Color.argb(200, 255, 200, 100)); // Amarillo/naranja brillante
        canvas.drawCircle(centerX, centerY, Math.min(width, height) * 0.08f, paintCore);
        
        paintCore.setColor(Color.argb(150, 200, 150, 255)); // Morado brillante
        canvas.drawCircle(centerX, centerY, Math.min(width, height) * 0.12f, paintCore);
        
        // Dibujar los brazos espirales de la galaxia
        float maxRadius = Math.min(width, height) * 0.4f;
        int numArms = 2; // Dos brazos espirales
        
        for (int arm = 0; arm < numArms; arm++) {
            float armAngle = (arm * 180f) + rotationAngle;
            drawSpiralArm(canvas, centerX, centerY, armAngle, maxRadius);
        }
        
        // Restaurar el estado del canvas
        canvas.restore();
        
        // Invalidar para animación continua
        postInvalidateDelayed(16);
    }
    
    private void drawSpiralArm(Canvas canvas, float centerX, float centerY, float startAngle, float maxRadius) {
        Path path = new Path();
        boolean first = true;
        
        // Crear el brazo espiral
        for (float angle = 0; angle < 360 * 2; angle += 5) {
            float currentAngle = startAngle + angle;
            float radius = (angle / 720f) * maxRadius;
            
            if (radius > maxRadius) break;
            
            float x = centerX + (float) (Math.cos(Math.toRadians(currentAngle)) * radius);
            float y = centerY + (float) (Math.sin(Math.toRadians(currentAngle)) * radius);
            
            if (first) {
                path.moveTo(x, y);
                first = false;
            } else {
                path.lineTo(x, y);
            }
        }
        
        // Dibujar el brazo con gradiente de color
        for (int i = 0; i < 3; i++) {
            float alpha = 100f - (i * 30f);
            float width = 15f - (i * 4f);
            
            paintGalaxy.setColor(Color.argb((int)alpha, 100 + i * 30, 80 + i * 20, 200 + i * 20));
            paintGalaxy.setStrokeWidth(width);
            paintGalaxy.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paintGalaxy);
        }
        
        // Dibujar estrellas en el brazo
        paintStars.setAlpha(200);
        for (float angle = 0; angle < 360 * 2; angle += 20) {
            float currentAngle = startAngle + angle;
            float radius = (angle / 720f) * maxRadius;
            
            if (radius > maxRadius) break;
            
            float x = centerX + (float) (Math.cos(Math.toRadians(currentAngle)) * radius);
            float y = centerY + (float) (Math.sin(Math.toRadians(currentAngle)) * radius);
            
            // Dibujar estrella pequeña
            float starSize = 2f + (float) (Math.random() * 3f);
            canvas.drawCircle(x, y, starSize, paintStars);
        }
    }
}

