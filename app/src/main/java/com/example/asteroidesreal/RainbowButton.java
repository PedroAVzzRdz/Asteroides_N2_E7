package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class RainbowButton extends ImageButton {
    private Paint paint = new Paint();
    private float hue = 0f;
    private long lastUpdate = 0;

    public RainbowButton(Context context) {
        super(context);
        init();
    }

    public RainbowButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RainbowButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        setBackgroundColor(android.graphics.Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = System.currentTimeMillis();
        if (now - lastUpdate > 16) { // ~60 FPS
            hue += 2f;
            if (hue >= 360f) hue = 0f;
            lastUpdate = now;
        }

        // Crear gradiente arcoíris
        int[] colors = new int[7];
        float[] positions = new float[7];
        for (int i = 0; i < 7; i++) {
            float h = (hue + i * 51.4f) % 360f;
            colors[i] = android.graphics.Color.HSVToColor(new float[]{h, 1f, 1f});
            positions[i] = i / 6f;
        }

        LinearGradient gradient = new LinearGradient(
                0, 0, getWidth(), getHeight(),
                colors, positions,
                Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);

        // Dibujar fondo con gradiente arcoíris
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 16, 16, paint);

        // Dibujar el contenido del botón
        super.onDraw(canvas);

        // Invalidar para animación continua
        postInvalidateDelayed(16);
    }
}

