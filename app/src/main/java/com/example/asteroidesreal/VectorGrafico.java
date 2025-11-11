package com.example.asteroidesreal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.Random;

public class VectorGrafico {
    // Posición y movimiento
    private int cenX, cenY;
    private double incX, incY;
    private double angulo, rotacion;
    private int radioColision;
    private int xAnterior, yAnterior;
    private int radioInval;
    private View view;
    
    // Propiedades para dibujo vectorial
    private TipoVector tipo;
    private float[] puntos; // Puntos del polígono para asteroides
    private float radio; // Radio para asteroides
    private int numLados; // Número de lados para asteroides
    private Paint paint;
    
    public enum TipoVector {
        ASTEROIDE,
        NAVE,
        MISIL
    }
    
    public VectorGrafico(View view, TipoVector tipo, int cenX, int cenY) {
        this.view = view;
        this.tipo = tipo;
        this.cenX = cenX;
        this.cenY = cenY;
        this.xAnterior = cenX;
        this.yAnterior = cenY;
        
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2f);
        
        Random random = new Random();
        
        switch (tipo) {
            case ASTEROIDE:
                // Crear polígono irregular para asteroide
                radio = 30 + random.nextFloat() * 20; // Radio entre 30 y 50
                numLados = 6 + random.nextInt(4); // Entre 6 y 9 lados
                generarPuntosAsteroide(random);
                ancho = (int)(radio * 2);
                alto = (int)(radio * 2);
                radioColision = (int)(radio * 0.8f);
                paint.setColor(0xFF888888); // Gris para asteroides
                paint.setStrokeWidth(1.5f);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                break;
                
            case NAVE:
                // Triángulo para nave
                ancho = 40;
                alto = 40;
                radioColision = 15;
                paint.setColor(0xFFFFFFFF); // Blanco para nave
                paint.setStyle(Paint.Style.FILL);
                break;
                
            case MISIL:
                // Línea para misil
                ancho = 8;
                alto = 20;
                radioColision = 5;
                paint.setColor(0xFFFFFF00); // Amarillo para misil
                paint.setStrokeWidth(3f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                break;
        }
        
        radioInval = (int) Math.hypot(ancho / 2, alto / 2);
    }
    
    private void generarPuntosAsteroide(Random random) {
        puntos = new float[numLados * 2];
        float anguloStep = (float)(2 * Math.PI / numLados);
        
        for (int i = 0; i < numLados; i++) {
            float ang = i * anguloStep;
            // Variar el radio para hacer el asteroide irregular
            float r = radio * (0.7f + random.nextFloat() * 0.6f);
            puntos[i * 2] = (float)(r * Math.cos(ang));
            puntos[i * 2 + 1] = (float)(r * Math.sin(ang));
        }
    }
    
    private int ancho, alto;
    
    public void dibujarVector(Canvas canvas) {
        canvas.save();
        canvas.translate(cenX, cenY);
        canvas.rotate((float)angulo);
        
        switch (tipo) {
            case ASTEROIDE:
                dibujarAsteroide(canvas);
                break;
            case NAVE:
                dibujarNave(canvas);
                break;
            case MISIL:
                dibujarMisil(canvas);
                break;
        }
        
        canvas.restore();
        xAnterior = cenX;
        yAnterior = cenY;
    }
    
    private void dibujarAsteroide(Canvas canvas) {
        if (puntos == null || puntos.length < 6) return;
        
        Path path = new Path();
        path.moveTo(puntos[0], puntos[1]);
        
        for (int i = 1; i < numLados; i++) {
            path.lineTo(puntos[i * 2], puntos[i * 2 + 1]);
        }
        path.close();
        
        // Dibujar relleno
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF666666); // Gris oscuro
        canvas.drawPath(path, paint);
        
        // Dibujar borde
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFAAAAAA); // Gris claro
        paint.setStrokeWidth(2f);
        canvas.drawPath(path, paint);
    }
    
    private void dibujarNave(Canvas canvas) {
        // Triángulo apuntando hacia arriba (se rotará según el ángulo de la nave)
        Path path = new Path();
        
        // Punto superior (punta de la nave)
        float topX = 0;
        float topY = -alto / 2f;
        
        // Punto inferior izquierdo
        float leftX = -ancho / 2f;
        float leftY = alto / 2f;
        
        // Punto inferior derecho
        float rightX = ancho / 2f;
        float rightY = alto / 2f;
        
        path.moveTo(topX, topY);
        path.lineTo(leftX, leftY);
        path.lineTo(rightX, rightY);
        path.close();
        
        // Dibujar relleno blanco
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        canvas.drawPath(path, paint);
        
        // Dibujar borde
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFCCCCCC);
        paint.setStrokeWidth(2f);
        canvas.drawPath(path, paint);
    }
    
    private void dibujarMisil(Canvas canvas) {
        // Línea vertical para misil
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFFFFF00); // Amarillo
        paint.setStrokeWidth(4f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        
        canvas.drawLine(0, -alto / 2f, 0, alto / 2f, paint);
        
        // Punto brillante en la punta
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFAA00); // Naranja
        canvas.drawCircle(0, -alto / 2f, 3f, paint);
    }
    
    public void incrementaPos(double factor) {
        cenX += incX * factor;
        cenY += incY * factor;
        angulo += rotacion * factor;
        
        // Si salimos de la pantalla, corregimos posición
        if (cenX < 0) cenX = view.getWidth();
        if (cenX > view.getWidth()) cenX = 0;
        if (cenY < 0) cenY = view.getHeight();
        if (cenY > view.getHeight()) cenY = 0;
        
        view.postInvalidate(cenX - radioInval, cenY - radioInval,
                cenX + radioInval, cenY + radioInval);
        view.postInvalidate(xAnterior - radioInval, yAnterior - radioInval,
                xAnterior + radioInval, yAnterior + radioInval);
    }
    
    // Getters y Setters
    public void setIncX(double incX) { this.incX = incX; }
    public void setIncY(double incY) { this.incY = incY; }
    public void setAngulo(double angulo) { this.angulo = angulo; }
    public void setRotacion(double rotacion) { this.rotacion = rotacion; }
    public void setCenX(int cenX) { this.cenX = cenX; }
    public void setCenY(int cenY) { this.cenY = cenY; }
    public int getCenX() { return cenX; }
    public int getCenY() { return cenY; }
    public double getIncX() { return incX; }
    public double getIncY() { return incY; }
    public double getAngulo() { return angulo; }
    public double getRotacion() { return rotacion; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getRadioColision() { return radioColision; }
    public void setRadioColision(int radioColision) { this.radioColision = radioColision; }
    
    public double distancia(VectorGrafico g) {
        return Math.hypot(cenX - g.cenX, cenY - g.cenY);
    }
    
    public boolean verificaColision(VectorGrafico g) {
        return (distancia(g) < (radioColision + g.radioColision));
    }
    
    // Método para verificar colisión con Grafico normal (para compatibilidad)
    public boolean verificaColision(Grafico g) {
        return (Math.hypot(cenX - g.getCenX(), cenY - g.getCenY()) < (radioColision + g.getRadioColision()));
    }
}




