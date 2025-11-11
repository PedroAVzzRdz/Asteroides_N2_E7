package com.example.asteroidesreal;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class AnimatedGalaxyButton extends Button {
    private ValueAnimator colorAnimator;
    private LayerDrawable layerDrawable;
    private GradientDrawable gradientDrawable;
    private ArgbEvaluator argbEvaluator;
    
    // Colores galácticos para animación
    private int[] colorSet1 = {0xFF4A148C, 0xFF880E4F, 0xFF1A237E}; // Morado, rosa, azul
    private int[] colorSet2 = {0xFF9C27B0, 0xFFE91E63, 0xFF3F51B5}; // Morado claro, rosa brillante, azul brillante
    private int[] colorSet3 = {0xFF7B1FA2, 0xFFFF6B9D, 0xFF5C6BC0}; // Morado medio, rosa claro, azul medio
    
    public AnimatedGalaxyButton(Context context) {
        super(context);
        init();
    }
    
    public AnimatedGalaxyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public AnimatedGalaxyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        argbEvaluator = new ArgbEvaluator();
        
        // Obtener el drawable de fondo
        if (getBackground() instanceof LayerDrawable) {
            layerDrawable = (LayerDrawable) getBackground();
            // Buscar el GradientDrawable en las capas (generalmente la última)
            for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                if (layerDrawable.getDrawable(i) instanceof GradientDrawable) {
                    gradientDrawable = (GradientDrawable) layerDrawable.getDrawable(i);
                    break;
                }
            }
        } else if (getBackground() instanceof GradientDrawable) {
            gradientDrawable = (GradientDrawable) getBackground();
        }
        
        // Si no encontramos un GradientDrawable, crear uno nuevo
        if (gradientDrawable == null) {
            createAnimatedBackground();
        }
        
        // Iniciar animación de colores después de que la vista esté lista
        post(() -> startColorAnimation());
    }
    
    private void createAnimatedBackground() {
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(8 * getResources().getDisplayMetrics().density);
        gradientDrawable.setColors(colorSet1);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TL_BR);
        setBackground(gradientDrawable);
    }
    
    private void startColorAnimation() {
        if (gradientDrawable == null) return;
        
        // Animación que cambia entre los tres conjuntos de colores
        colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration(4000); // 4 segundos por ciclo completo
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.RESTART);
        
        colorAnimator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            
            // Interpolar entre los tres conjuntos de colores
            int[] currentColors = new int[3];
            
            if (fraction < 0.33f) {
                // Primera tercera parte: colorSet1 -> colorSet2
                float localFraction = fraction / 0.33f;
                currentColors[0] = (Integer) argbEvaluator.evaluate(localFraction, colorSet1[0], colorSet2[0]);
                currentColors[1] = (Integer) argbEvaluator.evaluate(localFraction, colorSet1[1], colorSet2[1]);
                currentColors[2] = (Integer) argbEvaluator.evaluate(localFraction, colorSet1[2], colorSet2[2]);
            } else if (fraction < 0.66f) {
                // Segunda tercera parte: colorSet2 -> colorSet3
                float localFraction = (fraction - 0.33f) / 0.33f;
                currentColors[0] = (Integer) argbEvaluator.evaluate(localFraction, colorSet2[0], colorSet3[0]);
                currentColors[1] = (Integer) argbEvaluator.evaluate(localFraction, colorSet2[1], colorSet3[1]);
                currentColors[2] = (Integer) argbEvaluator.evaluate(localFraction, colorSet2[2], colorSet3[2]);
            } else {
                // Tercera parte: colorSet3 -> colorSet1
                float localFraction = (fraction - 0.66f) / 0.34f;
                currentColors[0] = (Integer) argbEvaluator.evaluate(localFraction, colorSet3[0], colorSet1[0]);
                currentColors[1] = (Integer) argbEvaluator.evaluate(localFraction, colorSet3[1], colorSet1[1]);
                currentColors[2] = (Integer) argbEvaluator.evaluate(localFraction, colorSet3[2], colorSet1[2]);
            }
            
            if (gradientDrawable != null) {
                gradientDrawable.setColors(currentColors);
                invalidate();
            }
        });
        
        colorAnimator.start();
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (colorAnimator != null && !colorAnimator.isStarted()) {
            colorAnimator.start();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
    }
    
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (colorAnimator != null) {
            if (visibility == View.VISIBLE) {
                if (!colorAnimator.isStarted()) {
                    colorAnimator.start();
                }
            } else {
                colorAnimator.pause();
            }
        }
    }
}




