package com.example.asteroidesreal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlienView extends View {
    private Paint paint;
    private Random random;
    private Bitmap alienNormal;
    private Bitmap alienPaz;
    private List<Alien> aliens;
    private long lastAlienSpawn;
    private long nextSpawnTime;
    private static final long SPAWN_INTERVAL_MIN_MS = 45000; // 45 segundos mínimo
    private static final long SPAWN_INTERVAL_MAX_MS = 90000; // 90 segundos máximo
    private static final float ALIEN_SPEED_NORMAL = 3.0f; // Velocidad inicial
    private static final float ALIEN_SPEED_FAST = 6.0f; // Velocidad después del cambio
    private static final int TRAIL_LENGTH = 20; // Número de posiciones en la estela
    private static final float TRAIL_ALPHA_STEP = 0.08f; // Reducción de alpha por posición
    private static final float TRAIL_DISTANCE_THRESHOLD = 8.0f; // Distancia mínima entre puntos de estela
    private Paint trailPaint;
    private Paint glowPaint;
    private int screenWidth;
    private int screenHeight;
    
    private class Alien {
        float x;
        float y;
        boolean isNormal; // true = aliennormal, false = alienpaz
        boolean hasChanged; // Si ya cambió a alienpaz
        boolean visible;
        float speed; // Velocidad actual
        boolean movingRight; // Dirección: true = derecha, false = izquierda
        List<TrailPoint> trail; // Estela de luz
        float lastTrailX; // Última posición X donde se agregó un punto a la estela
        float lastTrailY; // Última posición Y donde se agregó un punto a la estela
        
        Alien(float x, float y, boolean movingRight) {
            this.x = x;
            this.y = y;
            this.isNormal = true;
            this.hasChanged = false;
            this.visible = true;
            this.speed = ALIEN_SPEED_NORMAL;
            this.movingRight = movingRight;
            this.trail = new ArrayList<>();
            this.lastTrailX = x;
            this.lastTrailY = y;
        }
    }
    
    private class TrailPoint {
        float x;
        float y;
        float alpha;
        boolean isFast; // Si es parte de la estela rápida (alienpaz)
        
        TrailPoint(float x, float y, float alpha, boolean isFast) {
            this.x = x;
            this.y = y;
            this.alpha = alpha;
            this.isFast = isFast;
        }
    }
    
    public AlienView(Context context) {
        super(context);
        init(context);
    }
    
    public AlienView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public AlienView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        
        // Paint para la estela normal
        trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trailPaint.setFilterBitmap(true);
        
        // Paint para el efecto de glow de la estela rápida
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setFilterBitmap(true);
        // ColorFilter para efecto de luz brillante (cyan/azul)
        ColorFilter lightFilter = new LightingColorFilter(0xFFFFFFFF, 0x0040C0FF);
        glowPaint.setColorFilter(lightFilter);
        
        random = new Random();
        aliens = new ArrayList<>();
        lastAlienSpawn = System.currentTimeMillis();
        // Calcular el próximo tiempo de spawn (entre 45-90 segundos)
        nextSpawnTime = lastAlienSpawn + SPAWN_INTERVAL_MIN_MS + random.nextInt((int)(SPAWN_INTERVAL_MAX_MS - SPAWN_INTERVAL_MIN_MS));
        
        // Cargar bitmaps de aliens
        try {
            alienNormal = BitmapFactory.decodeResource(getResources(), R.drawable.aliennormal);
            alienPaz = BitmapFactory.decodeResource(getResources(), R.drawable.alienpaz);
            
            // Escalar los bitmaps a un tamaño adecuado (por ejemplo, 100x100 dp)
            float density = getResources().getDisplayMetrics().density;
            int size = (int) (80 * density); // 80dp
            if (alienNormal != null) {
                alienNormal = Bitmap.createScaledBitmap(alienNormal, size, size, true);
            }
            if (alienPaz != null) {
                alienPaz = Bitmap.createScaledBitmap(alienPaz, size, size, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Hacer la vista transparente para que se vea el fondo
        setBackgroundColor(0x00000000);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (screenWidth == 0 || screenHeight == 0) {
            screenWidth = getWidth();
            screenHeight = getHeight();
        }
        
        long now = System.currentTimeMillis();
        
        // Crear nuevo alien ocasionalmente (cada 45-90 segundos aproximadamente)
        if (now >= nextSpawnTime && aliens.isEmpty()) { // Solo spawnear si no hay aliens activos
            spawnAlien();
            lastAlienSpawn = now;
            // Calcular el próximo tiempo de spawn (entre 45-90 segundos)
            nextSpawnTime = now + SPAWN_INTERVAL_MIN_MS + random.nextInt((int)(SPAWN_INTERVAL_MAX_MS - SPAWN_INTERVAL_MIN_MS));
        }
        
        // Actualizar y dibujar aliens
        List<Alien> aliensToRemove = new ArrayList<>();
        for (Alien alien : aliens) {
            if (!alien.visible) {
                aliensToRemove.add(alien);
                continue;
            }
            
            // Mover alien horizontalmente
            if (alien.movingRight) {
                alien.x += alien.speed;
            } else {
                alien.x -= alien.speed;
            }
            
            // Verificar si llegó a la mitad de la pantalla horizontalmente
            float mitadPantalla = screenWidth / 2f;
            if (alien.isNormal && !alien.hasChanged) {
                if ((alien.movingRight && alien.x >= mitadPantalla) || 
                    (!alien.movingRight && alien.x <= mitadPantalla)) {
                    alien.isNormal = false;
                    alien.hasChanged = true;
                    alien.speed = ALIEN_SPEED_FAST; // Aumentar velocidad después del cambio
                }
            }
            
            // Agregar punto a la estela solo si se movió lo suficiente
            float distanceFromLastTrail = (float) Math.hypot(alien.x - alien.lastTrailX, alien.y - alien.lastTrailY);
            if (distanceFromLastTrail >= TRAIL_DISTANCE_THRESHOLD) {
                alien.trail.add(new TrailPoint(alien.x, alien.y, 1.0f, !alien.isNormal));
                alien.lastTrailX = alien.x;
                alien.lastTrailY = alien.y;
                
                // Limitar longitud de la estela
                while (alien.trail.size() > TRAIL_LENGTH) {
                    alien.trail.remove(0);
                }
            }
            
            // Verificar si salió de la pantalla
            Bitmap bitmapToCheck = alien.isNormal ? alienNormal : alienPaz;
            if (bitmapToCheck != null) {
                float alienWidth = bitmapToCheck.getWidth();
                if ((alien.movingRight && alien.x > screenWidth + alienWidth) ||
                    (!alien.movingRight && alien.x < -alienWidth)) {
                    alien.visible = false;
                    aliensToRemove.add(alien);
                    continue;
                }
            }
            
            // Dibujar estela de luz (ANTES del alien para que el alien aparezca encima)
            drawTrail(canvas, alien);
            
            // Dibujar alien
            Bitmap bitmapToDraw = alien.isNormal ? alienNormal : alienPaz;
            if (bitmapToDraw != null) {
                float left = alien.x - (bitmapToDraw.getWidth() / 2f);
                float top = alien.y - (bitmapToDraw.getHeight() / 2f);
                canvas.drawBitmap(bitmapToDraw, left, top, paint);
            }
        }
        
        // Eliminar aliens que ya no son visibles
        aliens.removeAll(aliensToRemove);
        
        // Redibujar continuamente
        postInvalidateDelayed(16); // ~60 FPS
    }
    
    private void spawnAlien() {
        if (screenWidth == 0 || screenHeight == 0) return;
        
        // Spawnear alien desde un lado de la pantalla (izquierda o derecha)
        boolean movingRight = random.nextBoolean(); // Dirección aleatoria
        float x;
        if (movingRight) {
            x = -100; // Empezar fuera de la pantalla (izquierda)
        } else {
            x = screenWidth + 100; // Empezar fuera de la pantalla (derecha)
        }
        // Posición Y aleatoria en la parte superior de la pantalla (primer 30%)
        float y = random.nextFloat() * (screenHeight * 0.3f) + (screenHeight * 0.1f);
        
        Alien alien = new Alien(x, y, movingRight);
        aliens.add(alien);
    }
    
    private void drawTrail(Canvas canvas, Alien alien) {
        if (alien.trail.isEmpty()) return;
        
        // Usar el bitmap apropiado según el estado del alien
        Bitmap bitmapToDraw = alien.isNormal ? alienNormal : alienPaz;
        if (bitmapToDraw == null) return;
        
        // Dibujar estela desde la más antigua hasta la más reciente
        int trailSize = alien.trail.size();
        for (int i = 0; i < trailSize; i++) {
            TrailPoint point = alien.trail.get(i);
            
            // Calcular alpha basado en la posición en la estela (más antigua = más transparente)
            float alpha = 1.0f - (i * TRAIL_ALPHA_STEP);
            if (alpha < 0.05f) alpha = 0.05f; // Mantener un mínimo de visibilidad
            
            // Determinar qué bitmap usar para este punto de la estela
            Bitmap trailBitmap = point.isFast ? alienPaz : alienNormal;
            if (trailBitmap == null) trailBitmap = bitmapToDraw;
            
            float bitmapWidth = trailBitmap.getWidth();
            float bitmapHeight = trailBitmap.getHeight();
            float left = point.x - (bitmapWidth / 2f);
            float top = point.y - (bitmapHeight / 2f);
            
            if (point.isFast) {
                // Estela brillante para alienpaz con efecto de glow
                // Dibujar múltiples capas para efecto de resplandor
                for (int glowLayer = 2; glowLayer >= 0; glowLayer--) {
                    float glowAlpha = alpha * (0.4f - glowLayer * 0.1f);
                    if (glowAlpha > 0.05f) {
                        glowPaint.setAlpha((int)(glowAlpha * 255));
                        float scale = 1.0f + (glowLayer * 0.15f);
                        
                        canvas.save();
                        canvas.scale(scale, scale, point.x, point.y);
                        canvas.drawBitmap(trailBitmap, 
                            point.x - (bitmapWidth / 2f), 
                            point.y - (bitmapHeight / 2f), 
                            glowPaint);
                        canvas.restore();
                    }
                }
                
                // Dibujar el punto principal de la estela con color brillante
                trailPaint.setAlpha((int)(alpha * 255 * 0.9f));
                ColorFilter brightFilter = new LightingColorFilter(0xFFFFFFFF, 0x0060E0FF);
                trailPaint.setColorFilter(brightFilter);
                canvas.drawBitmap(trailBitmap, left, top, trailPaint);
                trailPaint.setColorFilter(null);
            } else {
                // Estela normal para aliennormal (más sutil)
                trailPaint.setAlpha((int)(alpha * 255 * 0.5f));
                canvas.drawBitmap(trailBitmap, left, top, trailPaint);
            }
        }
        
        // Restaurar alpha completo
        trailPaint.setAlpha(255);
        glowPaint.setAlpha(255);
    }
    
    public void reset() {
        aliens.clear();
        long now = System.currentTimeMillis();
        lastAlienSpawn = now;
        // Calcular el próximo tiempo de spawn
        nextSpawnTime = now + SPAWN_INTERVAL_MIN_MS + random.nextInt((int)(SPAWN_INTERVAL_MAX_MS - SPAWN_INTERVAL_MIN_MS));
    }
}

