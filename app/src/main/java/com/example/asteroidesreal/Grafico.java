package com.example.asteroidesreal;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

class Grafico {
    private Drawable drawable;     // Imagen que dibujaremos
    private int cenX, cenY;        // Posición del centro del gráfico
    private int ancho, alto;       // Dimensiones de la imagen
    private double incX, incY;     // Velocidad de desplazamiento
    private double angulo, rotacion; // Ángulo y velocidad de rotación
    private int radioColision;     // Para determinar colisión
    private int xAnterior, yAnterior; // Posición anterior
    private int radioInval;        // Radio usado en invalidate()
    private View view;             // Usada en view.invalidate()

    public Grafico(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioColision = (alto + ancho) / 4;
        radioInval = (int) Math.hypot(ancho / 2, alto / 2);
    }

    public void dibujaGrafico(Canvas canvas) {
        int x = cenX - ancho / 2;
        int y = cenY - alto / 2;
        drawable.setBounds(x, y, x + ancho, y + alto);
        canvas.save();
        canvas.rotate((float) angulo, cenX, cenY);
        drawable.draw(canvas);
        canvas.restore();
        xAnterior = cenX;
        yAnterior = cenY;
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

    // ==== SETTERS Y GETTERS EXISTENTES ====
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
    public int getxAnterior() { return xAnterior; }
    public int getyAnterior() { return yAnterior; }

    // ==== GETTERS y SETTERS AÑADIDOS ====
    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public int getRadioColision() {
        return radioColision;
    }

    public void setRadioColision(int radioColision) {
        this.radioColision = radioColision;
    }

    // ==== MÉTODOS DE COLISIÓN ====
    public double distancia(Grafico g) {
        return Math.hypot(cenX - g.cenX, cenY - g.cenY);
    }

    public boolean verificaColision(Grafico g) {
        return (distancia(g) < (radioColision + g.radioColision));
    }
}