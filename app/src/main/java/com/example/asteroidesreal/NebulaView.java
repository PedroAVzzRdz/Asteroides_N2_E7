package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

import java.util.Random;

public class NebulaView extends View {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paint = new Paint();
    private int width, height;

    private float offsetX = 0f;
    private float offsetY = 0f;
    private Random random = new Random();

    private Handler handler = new Handler();
    private final int FRAME_RATE = 33; // ~30 FPS

    public NebulaView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void startAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateNebula();
                invalidate();
                offsetX += 0.01f;
                offsetY += 0.01f;
                handler.postDelayed(this, FRAME_RATE);
            }
        }, FRAME_RATE);
    }

    private void generateNebula() {
        float scale = 0.02f; // escala del ruido
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float value = (float) (PerlinNoise.noise(x * scale + offsetX, y * scale + offsetY, 0));
                int hue = (int) ((value + 1) * 180); // -1..1 → 0..360
                float saturation = 0.7f + 0.3f * random.nextFloat();
                float brightness = 0.4f + 0.6f * random.nextFloat();
                int color = Color.HSVToColor(new float[]{hue, saturation, brightness});
                bitmap.setPixel(x, y, color);
            }
        }
    }
}
