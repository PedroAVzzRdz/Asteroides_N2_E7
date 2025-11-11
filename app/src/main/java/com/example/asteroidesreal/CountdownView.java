package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class CountdownView extends View {
    private Paint paint;
    private String currentText = "";
    private float textSize = 80f;
    private boolean visible = false;
    private int textColor = Color.WHITE;
    private Typeface pixelFont;
    
    public CountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public CountdownView(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        
        // Cargar fuente pixel art
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pixelFont = context.getResources().getFont(R.font.upheavtt);
            } else {
                // Para versiones anteriores a Android O, cargar desde assets
                pixelFont = Typeface.createFromAsset(context.getAssets(), "upheavtt.ttf");
            }
        } catch (Exception e) {
            // Si falla, intentar cargar desde recursos de otra manera
            try {
                pixelFont = Typeface.createFromAsset(context.getAssets(), "fonts/upheavtt.ttf");
            } catch (Exception e2) {
                // Si todo falla, usar fuente por defecto
                pixelFont = Typeface.MONOSPACE;
            }
        }
        
        // Aplicar fuente pixel art
        paint.setTypeface(pixelFont);
        paint.setStrokeWidth(3f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        
        // Sombra para efecto pixel art con colores galácticos
        paint.setShadowLayer(15f, 8f, 8f, Color.argb(200, 233, 30, 99)); // Rosa
    }
    
    public void showText(String text) {
        this.currentText = text;
        this.visible = true;
        invalidate();
    }
    
    public void hide() {
        this.visible = false;
        this.currentText = "";
        invalidate();
    }
    
    public void setTextColor(int color) {
        this.textColor = color;
        invalidate();
    }
    
    public void setTextSize(float size) {
        this.textSize = size;
        paint.setTextSize(size);
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (visible && !currentText.isEmpty()) {
            float x = getWidth() / 2f;
            float y = getHeight() / 2f + (paint.descent() + paint.ascent()) / 2;
            
            // Guardar el color original
            int originalColor = textColor;
            
            // Dibujar borde negro primero (más grueso para efecto pixel art)
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5f);
            paint.setColor(Color.argb(255, 0, 0, 0)); // Borde negro sólido
            canvas.drawText(currentText, x, y, paint);
            
            // Dibujar relleno con color galáctico
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(originalColor);
            canvas.drawText(currentText, x, y, paint);
        }
    }
}

