package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class PixelArtButton extends Button {
    private Paint textPaint;
    
    public PixelArtButton(Context context) {
        super(context);
        init();
    }
    
    public PixelArtButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public PixelArtButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setFakeBoldText(true);
        textPaint.setStyle(Paint.Style.FILL);
        
        // Efecto pixel art con stroke
        textPaint.setStrokeWidth(1.5f);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        
        // Sombra sutil
        textPaint.setShadowLayer(5f, 2f, 2f, Color.argb(100, 0, 0, 0));
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // Dibujar el fondo del botón primero
        super.onDraw(canvas);
        
        // El texto se dibuja automáticamente por el Button
        // pero podemos aplicar el estilo aquí si es necesario
    }
}




