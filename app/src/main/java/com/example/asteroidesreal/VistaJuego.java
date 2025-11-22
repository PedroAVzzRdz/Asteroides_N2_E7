package com.example.asteroidesreal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VistaJuego extends View implements SensorEventListener {

    // SENSORES
    private SensorManager mSensorManager;
    private Sensor acelerometro;
    private Sensor giroscopio;
    private boolean sensoresActivados = true;
    private float[] valoresGiroscopio = new float[3];
    private float[] valoresAcelerometro = new float[3];

    // ASTEROIDES
    private List<Grafico> asteroides;
    private int numAsteroides = 5;
    
    // NAVE
    private Grafico nave;
    
    // MISILES
    private ArrayList<Grafico> misiles;
    private ArrayList<Integer> tiempoMisiles;
    private static int PASO_VELOCIDAD_MISIL = 20;
    
    // VECTORES (para modo vector puro)
    private List<VectorGrafico> asteroidesVector;
    private VectorGrafico naveVector;
    private ArrayList<VectorGrafico> misilesVector;
    private int modoGraficos = 0; // 0=vector imagen, 1=normal, 2=vector puro

    // POSICIONES INICIALES
    private boolean posicionesInicializadas = false;
    private List<float[]> posicionesNormalizadas = new ArrayList<>();

    // THREAD
    ThreadJuego thread; // Cambiado a package-private para acceso desde Juego
    private static final int PERIODO_PROCESO = 16; // ~60 fps (1000ms / 60 ≈ 16.67ms)
    private long ultimoProceso = 0;

    // CONTROL NAVE
    private double giroNave = 0;
    private double aceleracionNave = 0;
    private double direccionJoystick = 0; // Dirección deseada del joystick en grados
    private double velocidadJoystick = 0; // Intensidad del joystick (0-1)
    private static final double MAX_VELOCIDAD_NAVE = 20;
    private static final double PASO_ACELERACION_NAVE = 1.2; // Aumentado para mayor responsividad
    private static final double PASO_GIRO_NAVE = 3.0; // Velocidad de giro hacia la dirección del joystick
    private static final double FRICCION = 0.98; // Fricción para desaceleración gradual
    
    // Estados de teclas presionadas
    private boolean teclaArriba = false;
    private boolean teclaIzquierda = false;
    private boolean teclaDerecha = false;

    // ENTRADA
    private boolean tecladoActivado = true;
    private boolean tactilActivado = true;
    
    // JOYSTICK VISIBLE
    private float joystickCenterX = 0;
    private float joystickCenterY = 0;
    private float joystickRadius = 160f; // Radio del joystick base (duplicado: 80 * 2)
    private float joystickThumbRadius = 60f; // Radio del thumbstick (duplicado: 30 * 2)
    private float joystickThumbX = 0;
    private float joystickThumbY = 0;
    private boolean joystickActive = false;
    private int joystickPointerId = -1;

    // PUNTAJE
    private int puntaje = 0;
    private boolean juegoTerminado = false;
    private ScoreDatabaseHelper dbHelper;
    private boolean nombreIngresado = false;
    
    // FRAGMENTACIÓN DE ASTEROIDES
    private Map<Grafico, Integer> fragmentacionAsteroides = new HashMap<>();
    private int numFragmentosConfigurado = 5; // Número de fragmentos configurado por el usuario
    
    // SISTEMA DE DISPARO
    private static final int DISPAROS_POR_SECUENCIA = 3;
    private static final long DELAY_ENTRE_DISPAROS_MS = 200; // 0.2 segundos entre disparos
    private static final long COOLDOWN_DISPARO_MS = 600; // 0.6 segundos después de la secuencia completa
    private long ultimoDisparo = 0;
    private int disparosEnSecuencia = 0;
    private long ultimoDisparoEnSecuencia = 0;
    
    // SISTEMA DE VIDAS
    private int vidas = 3;
    
    // SISTEMA DE NIVELES
    private int nivel = 1;
    private long tiempoInicio = 0;
    private long tiempoInicioNivel = 0; // Tiempo de inicio del nivel actual
    private long tiempoMostrarNivel = 0;
    private static final long TIEMPO_MOSTRAR_NIVEL_MS = 2000; // 2 segundos mostrando "NIVEL X"
    private boolean mostrandoNivel = false;
    
    // SISTEMA DE FINALIZACIÓN DE MISIÓN
    private enum EstadoMision { NORMAL, MISION_COMPLETADA, PANTALLA_BONUS, TRANSICION }
    private EstadoMision estadoMision = EstadoMision.NORMAL;
    private MediaPlayer sonidoMisionCompletada;
    private MediaPlayer sonidoNivel1;
    private long tiempoMisionCompletada = 0;
    private boolean audioMisionCompletado = false;
    private long tiempoInicioBonus = 0;
    
    // BONUS Y PUNTUACIÓN
    private int nivelCompletado = 0; // Nivel que se acaba de completar
    private int bonusClear = 0; // Bonus por completar nivel
    private int bonusTiempo = 0; // Bonus por tiempo
    private int bonusVidas = 0; // Bonus por vidas restantes
    private int bonusEnemigos = 0; // Bonus por enemigos derrotados
    private int vidasInicialesNivel = 3; // Vidas al inicio del nivel
    private int enemigosDerrotados = 0; // Contador de enemigos derrotados en el nivel
    
    // ANIMACIONES DE BONUS
    private int bonusClearActual = 0; // Valor animado actual
    private int bonusTiempoActual = 0;
    private int bonusVidasActual = 0;
    private int bonusEnemigosActual = 0;
    private int puntajeTotalAnimado = 0;
    private long ultimaActualizacionBonus = 0;
    private static final long INTERVALO_ANIMACION_BONUS_MS = 20; // Actualizar cada 20ms para animación suave
    private int indiceBonusMostrando = 0; // 0=clear, 1=tiempo, 2=vidas, 3=enemigos, 4=total
    private boolean bonusCompletado = false;
    private long ultimoGuardadoEstado = 0; // Última vez que se guardó el estado del juego
    
    // ANIMACIÓN DE CORAZONES
    private long ultimoCambioCorazon = 0;
    private static final long INTERVALO_CORAZON_MS = 500; // 0.5 segundos
    private boolean mostrarCorazon1 = true; // true = ce1.png, false = ce2.png
    
    // POWER-UPS
    private enum PowerUpType { ESCUDO, DISPARO_MEJORADO, VELOCIDAD }
    private PowerUpType powerUpActivo = null;
    private long tiempoPowerUp = 0;
    private static final long DURACION_POWERUP_MS = 10000; // 10 segundos
    
    // ESTADOS DE ANIMACIÓN DEL ESCUDO
    private enum EstadoEscudo { NONE, APARICIENDO, ACTIVO, DESTRUYENDO }
    private EstadoEscudo estadoEscudo = EstadoEscudo.NONE;
    private long tiempoEstadoEscudo = 0;
    private static final long DURACION_APARICION_MS = 400; // 0.4 segundos para es1 -> es1.5
    private static final long DURACION_DESTRUCCION_MS = 500; // 0.5 segundos para es3 -> es4
    private Grafico escudoGrafico = null;
    
    // ORBES DE POWER-UP
    private List<PowerUpOrb> orbesPowerUp = new ArrayList<>();
    private static final float PROBABILIDAD_ORBE_DE_ASTEROIDE = 0.15f; // 15% de probabilidad al destruir un asteroide completamente
    
    private class PowerUpOrb {
        float x, y;
        float vx, vy;
        PowerUpType tipo;
        Grafico grafico;
        float floatOffset = 0;
        float floatSpeed = 0.02f;
        float pulseAlpha = 1.0f;
        boolean pulseDirection = false;
        boolean activa = true;
        
        PowerUpOrb(float x, float y, PowerUpType tipo, Context context) {
            this.x = x;
            this.y = y;
            this.tipo = tipo;
            this.vx = (float) (Math.random() * 2 - 1);
            this.vy = (float) (Math.random() * 2 - 1);
            
            // Cargar el drawable según el tipo
            int drawableId;
            switch (tipo) {
                case ESCUDO:
                    drawableId = R.drawable.pe;
                    break;
                case VELOCIDAD:
                    drawableId = R.drawable.pv;
                    break;
                case DISPARO_MEJORADO:
                    drawableId = R.drawable.pd;
                    break;
                default:
                    drawableId = R.drawable.pe;
            }
            
            Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                // Reducir 30% más del tamaño actual (0.3 * 0.7 = 0.21)
                int nuevoAncho = (int) (bmp.getWidth() * 0.21);
                int nuevoAlto = (int) (bmp.getHeight() * 0.21);
                Bitmap reducido = Bitmap.createScaledBitmap(bmp, nuevoAncho, nuevoAlto, true);
                this.grafico = new Grafico(VistaJuego.this, new BitmapDrawable(getResources(), reducido));
            } else {
                this.grafico = new Grafico(VistaJuego.this, drawable);
            }
            
            this.grafico.setCenX((int) x);
            this.grafico.setCenY((int) y);
            this.grafico.setRadioColision(this.grafico.getAncho() / 2);
        }
        
        void update() {
            int width = VistaJuego.this.getWidth();
            int height = VistaJuego.this.getHeight();
            
            if (width == 0 || height == 0) return;
            
            // Movimiento flotante
            x += vx;
            y += vy;
            
            // Rebote suave en los bordes
            if (x < 0 || x > width) vx = -vx;
            if (y < 0 || y > height) vy = -vy;
            
            // Mantener dentro de la pantalla
            x = Math.max(0, Math.min(width, x));
            y = Math.max(0, Math.min(height, y));
            
            // Efecto de flotación vertical
            floatOffset += floatSpeed;
            y += Math.sin(floatOffset) * 0.5f;
            
            // Asegurar que siga dentro después de la flotación
            y = Math.max(0, Math.min(height, y));
            
            // Actualizar posición del gráfico
            grafico.setCenX((int) x);
            grafico.setCenY((int) y);
            
            // Actualizar pulso del haz de luz
            if (pulseDirection) {
                pulseAlpha += 0.05f;
                if (pulseAlpha >= 1.0f) {
                    pulseAlpha = 1.0f;
                    pulseDirection = false;
                }
            } else {
                pulseAlpha -= 0.05f;
                if (pulseAlpha <= 0.3f) {
                    pulseAlpha = 0.3f;
                    pulseDirection = true;
                }
            }
        }
        
        int getColor() {
            switch (tipo) {
                case ESCUDO:
                    return Color.GREEN;
                case VELOCIDAD:
                    return Color.BLUE;
                case DISPARO_MEJORADO:
                    return Color.RED;
                default:
                    return Color.WHITE;
            }
        }
    }
    
    // EFECTO DE DESTELLO
    private float flashAlpha = 0f;
    private int flashColor = Color.WHITE;
    private long flashStartTime = 0;
    private static final long FLASH_DURATION_MS = 300; // 0.3 segundos
    
    // ANIMACIONES DE EXPLOSIÓN
    private List<Explosion> explosiones = new ArrayList<>();
    
    private class Explosion {
        float x, y;
        float radio;
        float alpha = 1.0f;
        boolean activa = true;
        
        Explosion(float x, float y) {
            this.x = x;
            this.y = y;
            this.radio = 0;
        }
        
        void update() {
            radio += 5;
            alpha -= 0.05f;
            if (alpha <= 0 || radio > 100) activa = false;
        }
    }
    
    // INMUNIDAD TEMPORAL
    private long tiempoInmunidad = 0;
    private static final long DURACION_INMUNIDAD_MS = 2000; // 2 segundos
    private boolean inmune = false;
    private float alphaInmunidad = 1.0f;
    
    // PANTALLA SHAKE
    private float shakeOffsetX = 0;
    private float shakeOffsetY = 0;
    private float shakeIntensity = 0;
    private static final float SHAKE_DECAY = 0.9f;
    private static final float MAX_SHAKE = 20f;
    
    // PARTÍCULAS MEJORADAS
    private List<Particle> particulas = new ArrayList<>();
    
    private class Particle {
        float x, y;
        float vx, vy;
        float life;
        float maxLife;
        int color;
        float size;
        boolean activa = true;
        
        Particle(float x, float y, float vx, float vy, int color, float size, float life) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            this.life = life;
            this.maxLife = life;
        }
        
        void update() {
            x += vx;
            y += vy;
            life -= 0.02f;
            vx *= 0.98f;
            vy *= 0.98f;
            if (life <= 0) activa = false;
        }
    }
    
    // ESTADÍSTICAS
    private int asteroidesDestruidos = 0;
    private long tiempoJuego = 0;
    private int disparosRealizados = 0;
    private int disparosAcertados = 0;
    
    // ESTELAS DE NAVE
    private List<TrailPoint> estelaNave = new ArrayList<>();
    private static final int MAX_TRAIL_POINTS = 15;
    
    private class TrailPoint {
        float x, y;
        float alpha;
        float size;
        
        TrailPoint(float x, float y) {
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
            this.size = 8f;
        }
        
        void update() {
            alpha -= 0.1f;
            size -= 0.3f;
            if (alpha <= 0 || size <= 0) alpha = 0;
        }
    }
    
    // ESTADO DE PAUSA
    private boolean pausado = false;
    
    // COOLDOWN DESPUÉS DE ROTACIÓN
    private boolean enCooldown = false;
    private long tiempoInicioCooldown = 0;
    private static final long DURACION_COOLDOWN_MS = 3000; // 3 segundos
    
    // VIBRADOR Y SONIDOS
    private Vibrator vibrator;
    private MediaPlayer sonidoDisparo;
    private SoundPool soundPool;
    private int soundIdExplosion = 0; // ID del sonido de explosión en el SoundPool (0 = no cargado)
    private MediaPlayer sonidoColision;
    private MediaPlayer sonidoEscudoAparece;
    private MediaPlayer sonidoEscudoExplota;
    private boolean audioMuteado = false; // Estado de silencio del audio
    
    // REFERENCIA AL StarViewGame
    private View starViewGame;
    
    // ESTADO DE INICIO
    private boolean juegoIniciado = false;
    
    // FUENTE PIXEL ART
    private Typeface pixelFont;

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        modoGraficos = pref.getInt("graficos_mode", 0);
        
        // Inicializar vibrator
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        
        // Inicializar tiempo de inicio y nivel
        tiempoInicio = System.currentTimeMillis();
        tiempoInicioNivel = System.currentTimeMillis();

        // Restaurar estado del juego si existe (para evitar reinicio al rotar)
        SharedPreferences gameState = context.getSharedPreferences("game_state", Context.MODE_PRIVATE);
        boolean juegoEnCurso = gameState.getBoolean("juego_en_curso", false);

        if (juegoEnCurso) {
            // Restaurar estado del juego
            nivel = gameState.getInt("nivel", 1);
            puntaje = gameState.getInt("puntaje", 0);
            vidas = gameState.getInt("vidas", 3);
            tiempoInicioNivel = gameState.getLong("tiempo_inicio_nivel", System.currentTimeMillis());
            vidasInicialesNivel = gameState.getInt("vidas_iniciales_nivel", 3);
            enemigosDerrotados = gameState.getInt("enemigos_derrotados", 0);
            // Restaurar estado de misión si existe
            String estadoMisionStr = gameState.getString("estado_mision", EstadoMision.NORMAL.name());
            try {
                estadoMision = EstadoMision.valueOf(estadoMisionStr);
            } catch (Exception e) {
                estadoMision = EstadoMision.NORMAL;
            }
            nivelCompletado = gameState.getInt("nivel_completado", 0);
            // Si el juego estaba en curso, marcar como iniciado
            juegoIniciado = true;
            mostrandoNivel = false;
        } else {
            // Juego nuevo: siempre empezar con 3 vidas
            nivel = 1;
            puntaje = 0;
            vidas = 3; // Asegurar que siempre empiece con 3 vidas
            vidasInicialesNivel = 3; // Asegurar vidas iniciales del nivel
            enemigosDerrotados = 0;
            mostrandoNivel = false;
            estadoMision = EstadoMision.NORMAL;
            juegoIniciado = false;
            // Limpiar cualquier estado previo para asegurar un inicio limpio
            // (solo limpiar el flag, no todos los valores ya que pueden no estar inicializados)
            SharedPreferences.Editor editor = gameState.edit();
            editor.putBoolean("juego_en_curso", false);
            editor.apply();
        }
        
        // Cargar número de fragmentos configurado
        numFragmentosConfigurado = pref.getInt("num_fragmentos", 5);

        sensoresActivados = pref.getBoolean("entrada_sensor", true);
        tecladoActivado = pref.getBoolean("entrada_teclado", true);
        tactilActivado = pref.getBoolean("entrada_tactil", true);

        // Inicializar base de datos de puntajes
        dbHelper = new ScoreDatabaseHelper(context);

        // Inicializar SensorManager siempre, pero solo registrar si están activados
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensoresActivados) {
            activarSensores();
        }

        // Configurar fondo según modo de gráficos
        if (modoGraficos == 2) {
            // Modo vector puro: fondo negro
            setBackgroundColor(Color.BLACK);
        } else {
            // Modos normales: fondo transparente para ver las estrellas
            setBackgroundColor(Color.TRANSPARENT);
        }

        misiles = new ArrayList<>();
        tiempoMisiles = new ArrayList<>();
        misilesVector = new ArrayList<>();

        // Inicializar según el modo de gráficos
        if (modoGraficos == 2) {
            // MODO VECTOR PURO: Crear objetos vectoriales
            inicializarVectores(context);
        } else {
            // MODO NORMAL O VECTOR IMAGEN: Usar drawables
            inicializarDrawables(context);
        }

        // Inicializar thread CORREGIDO (NO STATIC)
        thread = new ThreadJuego();
        
        // Cargar fuente pixel art
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pixelFont = getResources().getFont(R.font.upheavtt);
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
    }
    
    private void inicializarDrawables(Context context) {
        Drawable drawableAsteroide = AppCompatResources.getDrawable(context,
                modoGraficos == 0 ? R.drawable.asteroidevector : R.drawable.asteroide1);

        Drawable drawableAsteroideReducido;
        if (drawableAsteroide instanceof BitmapDrawable) {
            Bitmap bmpAst = ((BitmapDrawable) drawableAsteroide).getBitmap();
            // Reducir 20% más (0.8 = 80% del tamaño original, que ya estaba reducido a la mitad)
            int nuevoAncho = (int) (bmpAst.getWidth() * 0.4); // 0.5 * 0.8 = 0.4
            int nuevoAlto = (int) (bmpAst.getHeight() * 0.4);
            Bitmap bmpAstReducido = Bitmap.createScaledBitmap(bmpAst, nuevoAncho, nuevoAlto, true);
            drawableAsteroideReducido = new BitmapDrawable(getResources(), bmpAstReducido);
        } else drawableAsteroideReducido = drawableAsteroide;

        asteroides = new ArrayList<>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroideReducido);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setAngulo(Math.random() * 360);
            asteroide.setRotacion(Math.random() * 8 - 4);
            asteroides.add(asteroide);
            fragmentacionAsteroides.put(asteroide, 0); // 0 = asteroide completo
        }
        for (Grafico a : asteroides) a.setRadioColision(a.getAncho() / 3);

        Drawable drawableNave = AppCompatResources.getDrawable(context,
                modoGraficos == 0 ? R.drawable.navevector : R.drawable.nave1);
        if (drawableNave instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawableNave).getBitmap();
            // Reducir 20% más (0.8 = 80% del tamaño original, que ya estaba reducido a 1/8)
            int nuevoAncho = (int) (bmp.getWidth() * 0.1); // 1/8 * 0.8 = 0.1
            int nuevoAlto = (int) (bmp.getHeight() * 0.1);
            Bitmap reducido = Bitmap.createScaledBitmap(bmp, nuevoAncho, nuevoAlto, true);
            nave = new Grafico(this, new BitmapDrawable(getResources(), reducido));
        } else nave = new Grafico(this, drawableNave);
        
        // Establecer radio de colisión para la nave
        if (nave != null) {
            nave.setRadioColision(nave.getAncho() / 3);
        }
    }
    
    private void inicializarVectores(Context context) {
        // Inicializar asteroides vectoriales (se crearán en onSizeChanged cuando tengamos las dimensiones)
        asteroidesVector = new ArrayList<>();
        
        // La nave y asteroides se crearán cuando se conozca el tamaño de la pantalla
        // Por ahora solo inicializamos las listas
    }
    
    // Método helper para aplicar fuente pixel art a un Paint
    private void aplicarFuentePixelArt(Paint paint) {
        if (pixelFont != null) {
            paint.setTypeface(pixelFont);
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Inicializar sonidos cuando la vista esté completamente lista
        if (getContext() != null) {
            inicializarSonidos();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        liberarSonidos();
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        
        // Asegurar que el juego no se pause durante el cambio de tamaño (rotación)
        // Esto es importante para mantener el juego activo
        if (juegoIniciado && ancho > 0 && alto > 0) {
            pausado = false;
        }
        
        // Inicializar posición del joystick (esquina inferior izquierda)
        if (tactilActivado) {
            joystickCenterX = ancho * 0.15f;
            joystickCenterY = alto * 0.85f;
            joystickThumbX = joystickCenterX;
            joystickThumbY = joystickCenterY;
            
            // Botón de disparo (esquina inferior derecha)
            botonDisparoX = ancho * 0.85f;
            botonDisparoY = alto * 0.85f;
        }

        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            if (naveVector == null) {
                // Crear nave vectorial
                naveVector = new VectorGrafico(this, VectorGrafico.TipoVector.NAVE, ancho / 2, alto / 2);
                naveVector.setIncX(0);
                naveVector.setIncY(0);
                naveVector.setAngulo(0);
            } else {
                naveVector.setCenX(ancho / 2);
                naveVector.setCenY(alto / 2);
            }
            
            if (asteroidesVector == null || asteroidesVector.isEmpty()) {
                asteroidesVector = new ArrayList<>();
                int distanciaMinima = Math.max(ancho / 4, alto / 4);
                int distanciaMinimaJugador = Math.max(ancho / 3, alto / 3);
                int[] posicionJugador = new int[]{ancho / 2, alto / 2};
                
                for (int i = 0; i < numAsteroides; i++) {
                    int x, y;
                    boolean valido;
                    int intentos = 0;
                    do {
                        x = (int) (Math.random() * ancho);
                        y = (int) (Math.random() * alto);
                        valido = true;
                        
                        double distanciaJugador = Math.hypot(x - posicionJugador[0], y - posicionJugador[1]);
                        if (distanciaJugador < distanciaMinimaJugador) {
                            valido = false;
                        }
                        
                        if (valido) {
                            for (VectorGrafico a : asteroidesVector) {
                                if (Math.hypot(x - a.getCenX(), y - a.getCenY()) < distanciaMinima) {
                                    valido = false;
                                    break;
                                }
                            }
                        }
                        
                        if (!valido) intentos++;
                        if (intentos > 500) {
                            distanciaMinima = Math.max(ancho / 6, Math.max(alto / 6, distanciaMinima - 20));
                            distanciaMinimaJugador = Math.max(ancho / 4, Math.max(alto / 4, distanciaMinimaJugador - 20));
                            intentos = 0;
                        }
                    } while (!valido);
                    
                    VectorGrafico asteroide = new VectorGrafico(this, VectorGrafico.TipoVector.ASTEROIDE, x, y);
                    asteroide.setIncX(Math.random() * 4 - 2);
                    asteroide.setIncY(Math.random() * 4 - 2);
                    asteroide.setAngulo(Math.random() * 360);
                    asteroide.setRotacion(Math.random() * 8 - 4);
                    asteroidesVector.add(asteroide);
                }
                posicionesInicializadas = true;
            }
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            // Asegurar que la nave existe antes de reposicionarla
            if (nave == null && getContext() != null) {
                // Crear la nave si no existe
                try {
                    SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                    int modoGraficos = pref.getInt("graficos_mode", 0);
                    Drawable drawableNave = AppCompatResources.getDrawable(getContext(),
                            modoGraficos == 0 ? R.drawable.navevector : R.drawable.nave1);
                    if (drawableNave != null) {
                        if (drawableNave instanceof BitmapDrawable) {
                            Bitmap bmp = ((BitmapDrawable) drawableNave).getBitmap();
                            int nuevoAncho = (int) (bmp.getWidth() * 0.3);
                            int nuevoAlto = (int) (bmp.getHeight() * 0.3);
                            Bitmap reducido = Bitmap.createScaledBitmap(bmp, nuevoAncho, nuevoAlto, true);
                            nave = new Grafico(this, new BitmapDrawable(getResources(), reducido));
                        } else {
                            nave = new Grafico(this, drawableNave);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (nave != null) {
                nave.setCenX(ancho / 2);
                nave.setCenY(alto / 2);
            }

            if (!posicionesInicializadas) {
                // Distancia mínima aumentada para evitar asteroides cerca del jugador
                int distanciaMinima = Math.max(ancho / 4, alto / 4); // Al menos 1/4 de la pantalla
                int distanciaMinimaJugador = Math.max(ancho / 3, alto / 3); // Zona segura alrededor del jugador
                List<int[]> posiciones = new ArrayList<>();
                int[] posicionJugador = new int[]{ancho / 2, alto / 2};
                posiciones.add(posicionJugador);
                posicionesNormalizadas.clear();

                for (Grafico asteroide : asteroides) {
                    int x, y;
                    boolean valido;
                    int intentos = 0;
                    do {
                        x = (int) (Math.random() * ancho);
                        y = (int) (Math.random() * alto);
                        valido = true;
                        
                        // Verificar distancia mínima del jugador (zona segura)
                        double distanciaJugador = Math.hypot(x - posicionJugador[0], y - posicionJugador[1]);
                        if (distanciaJugador < distanciaMinimaJugador) {
                            valido = false;
                        }
                        
                        // Verificar distancia mínima de otros asteroides
                        if (valido) {
                            for (int[] pos : posiciones) {
                                if (Math.hypot(x - pos[0], y - pos[1]) < distanciaMinima) {
                                    valido = false;
                                    break;
                                }
                            }
                        }
                        
                        if (!valido) intentos++;
                        if (intentos > 500) {
                            // Si no se encuentra posición válida, reducir distancia mínima gradualmente
                            distanciaMinima = Math.max(ancho / 6, Math.max(alto / 6, distanciaMinima - 20));
                            distanciaMinimaJugador = Math.max(ancho / 4, Math.max(alto / 4, distanciaMinimaJugador - 20));
                            intentos = 0;
                        }
                    } while (!valido);

                    asteroide.setCenX(x);
                    asteroide.setCenY(y);
                    posiciones.add(new int[]{x, y});
                    posicionesNormalizadas.add(new float[]{x / (float) ancho, y / (float) alto});
                }
                posicionesInicializadas = true;
            } else {
                for (int i = 0; i < asteroides.size(); i++) {
                    float[] rel = posicionesNormalizadas.get(i);
                    int x = (int) (rel[0] * ancho);
                    int y = (int) (rel[1] * alto);
                    asteroides.get(i).setCenX(x);
                    asteroides.get(i).setCenY(y);
                }
            }
        }

        ultimoProceso = System.currentTimeMillis();
        
        // El juego se iniciará después de la cuenta regresiva
        // Solo establecer juegoIniciado = false si es la primera vez (nivel 1)
        // Si el juego ya estaba iniciado (nivel 2+), mantener el estado
        if (nivel == 1 && !juegoIniciado) {
            juegoIniciado = false;
        }

        if (!isThreadAlive()) {
            iniciarThreadJuego();
        }
        
        // Si el juego ya estaba iniciado y necesitamos generar asteroides para un nuevo nivel,
        // verificar si los asteroides están vacíos y generarlos
        if (juegoIniciado && ancho > 0 && alto > 0) {
            boolean necesitaAsteroides = false;
            if (modoGraficos == 2) {
                necesitaAsteroides = (asteroidesVector == null || asteroidesVector.isEmpty());
            } else {
                synchronized (asteroides) {
                    necesitaAsteroides = (asteroides == null || asteroides.isEmpty());
                }
            }
            
            if (necesitaAsteroides && getContext() != null) {
                // Generar asteroides si no existen y el juego está iniciado
                generarAsteroidesNivel();
            }
        }
    }
    
    public void iniciarJuego() {
        // NO resetear el nivel ni el puntaje si el juego ya está iniciado
        // Solo marcar como iniciado si no lo está ya
        if (!juegoIniciado) {
            juegoIniciado = true;
            // Asegurar que siempre empiece con 3 vidas cuando se inicia un juego nuevo
            vidas = 3;
            vidasInicialesNivel = 3;
            // Activar inmunidad inicial para evitar colisiones inmediatas al inicio
            inmune = true;
            tiempoInmunidad = System.currentTimeMillis();
        }
        // Asegurar que el juego no esté pausado
        pausado = false;
        // Asegurar que no estemos en cooldown (para niveles después del primero)
        // Solo resetear cooldown si no estamos en el nivel 1 (el nivel 1 puede tener cooldown por rotación)
        if (nivel > 1) {
            enCooldown = false;
        }
        // Asegurar que el thread esté ejecutándose
        if (!isThreadAlive()) {
            iniciarThreadJuego();
        }
        // Reproducir música del juego (sonidoNivel1) para todos los niveles
        reproducirMusicaJuego();
        // Guardar estado después de iniciar
        guardarEstadoJuego();
    }
    
    public void iniciarThreadJuego() {
        if (thread == null) {
            thread = new ThreadJuego();
        }
        if (!isThreadAlive()) {
            thread.start();
        }
    }
    
    public boolean isThreadAlive() {
        return thread != null && thread.isAlive();
    }

    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        
        // SIEMPRE actualizar estado de misión, incluso si está pausado
        // Esto asegura que la pantalla de bonus se actualice correctamente
        if (estadoMision != EstadoMision.NORMAL) {
            actualizarEstadoMision();
        }
        
        // Verificar que la música solo suene cuando el juego está activo
        // Esto asegura que se detenga inmediatamente si el juego se pausa o termina
        if (sonidoNivel1 != null && sonidoNivel1.isPlaying()) {
            if (pausado || juegoTerminado || audioMuteado || !juegoIniciado) {
                detenerMusicaInmediatamente();
            }
        }
        
        // Actualizar estado del cooldown (SIEMPRE, incluso si está pausado)
        // Esto asegura que el cooldown se desactive correctamente
        if (enCooldown) {
            long tiempoTranscurrido = ahora - tiempoInicioCooldown;
            if (tiempoTranscurrido >= DURACION_COOLDOWN_MS) {
                enCooldown = false;
                // Resetear controles al finalizar cooldown
                teclaArriba = false;
                teclaIzquierda = false;
                teclaDerecha = false;
                velocidadJoystick = 0;
                direccionJoystick = 0;
                aceleracionNave = 0;
                giroNave = 0;
                // También resetear joystick visual
                joystickThumbX = joystickCenterX;
                joystickThumbY = joystickCenterY;
                joystickActive = false;
                // DESACTIVAR INMUNIDAD al finalizar cooldown
                long tiempoInmunidadTranscurrido = ahora - tiempoInmunidad;
                if (tiempoInmunidadTranscurrido >= DURACION_COOLDOWN_MS) {
                    inmune = false;
                    alphaInmunidad = 1.0f;
                }
                // CRÍTICO: FORZAR que el juego continúe después del cooldown
                // Asegurar que el juego esté iniciado y NO pausado
                juegoIniciado = true;
                pausado = false;
                // Asegurar que el thread esté ejecutándose
                if (!isThreadAlive()) {
                    iniciarThreadJuego();
                }
                // Forzar invalidación para redibujar
                postInvalidate();
            }
        }
        
        // IMPORTANTE: Durante el cooldown o en pantallas de misión, el juego DEBE continuar ejecutándose para actualizar
        // Solo retornar si realmente está pausado, NO estamos en cooldown, y NO estamos en pantallas de misión
        // Si estamos en cooldown o en pantallas de misión, el juego debe continuar actualizando
        if ((pausado || !juegoIniciado) && !enCooldown && estadoMision == EstadoMision.NORMAL) return;
        
        if (ultimoProceso + PERIODO_PROCESO > ahora) return;
        // Normalizar factorMov para mantener la misma velocidad que antes (50ms)
        // Aunque actualizamos más frecuentemente (16ms), la velocidad debe ser la misma
        double factorMov = (ahora - ultimoProceso) / 50.0; // Usar 50ms como referencia para mantener velocidad
        ultimoProceso = ahora;
        
        // Guardar estado del juego periódicamente (cada 2 segundos)
        if (ahora - ultimoGuardadoEstado > 2000) {
            guardarEstadoJuego();
            ultimoGuardadoEstado = ahora;
        }

        // Actualizar controles según teclas presionadas (solo si no está en cooldown)
        if (tecladoActivado && !enCooldown) {
            if (teclaArriba) {
                double aceleracion = PASO_ACELERACION_NAVE;
                // Power-up de velocidad: aceleración aumentada
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    aceleracion *= 1.5;
                }
                aceleracionNave = aceleracion;
            }
            if (teclaIzquierda) {
                giroNave = -PASO_GIRO_NAVE;
            }
            if (teclaDerecha) {
                giroNave = +PASO_GIRO_NAVE;
            }
        } else if (enCooldown) {
            // Durante cooldown, resetear controles
            aceleracionNave = 0;
            giroNave = 0;
        }
        
        // Manejar controles según modo de gráficos (solo si no está en cooldown)
        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            if (tactilActivado && velocidadJoystick > 0 && !enCooldown) {
                double aceleracion = PASO_ACELERACION_NAVE * velocidadJoystick * 1.5;
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    aceleracion *= 1.5;
                }
                double aceleracionX = aceleracion * Math.cos(Math.toRadians(direccionJoystick)) * factorMov;
                double aceleracionY = aceleracion * Math.sin(Math.toRadians(direccionJoystick)) * factorMov;
                double nIncX = naveVector.getIncX() + aceleracionX;
                double nIncY = naveVector.getIncY() + aceleracionY;
                double maxVelocidad = MAX_VELOCIDAD_NAVE;
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    maxVelocidad *= 1.3;
                }
                double velocidadActual = Math.hypot(nIncX, nIncY);
                if (velocidadActual > maxVelocidad) {
                    nIncX = (nIncX / velocidadActual) * maxVelocidad;
                    nIncY = (nIncY / velocidadActual) * maxVelocidad;
                }
                naveVector.setIncX(nIncX);
                naveVector.setIncY(nIncY);
                if (velocidadActual > 0.3) {
                    double anguloMovimiento = Math.toDegrees(Math.atan2(nIncY, nIncX));
                    naveVector.setAngulo(anguloMovimiento);
                }
            } else if (tecladoActivado && !enCooldown) {
                if (teclaIzquierda || teclaDerecha) {
                    naveVector.setAngulo(naveVector.getAngulo() + giroNave * factorMov);
                }
                if (teclaArriba) {
                    double aceleracion = PASO_ACELERACION_NAVE;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        aceleracion *= 1.5;
                    }
                    double anguloMovimiento = naveVector.getAngulo();
                    double nIncX = naveVector.getIncX() + aceleracion * Math.cos(Math.toRadians(anguloMovimiento)) * factorMov;
                    double nIncY = naveVector.getIncY() + aceleracion * Math.sin(Math.toRadians(anguloMovimiento)) * factorMov;
                    double maxVelocidad = MAX_VELOCIDAD_NAVE;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        maxVelocidad *= 1.3;
                    }
                    double velocidadActual = Math.hypot(nIncX, nIncY);
                    if (velocidadActual > maxVelocidad) {
                        nIncX = (nIncX / velocidadActual) * maxVelocidad;
                        nIncY = (nIncY / velocidadActual) * maxVelocidad;
                    }
                    naveVector.setIncX(nIncX);
                    naveVector.setIncY(nIncY);
                }
            }
            if ((!tecladoActivado || (!teclaArriba && !teclaIzquierda && !teclaDerecha)) && 
                (!tactilActivado || velocidadJoystick == 0) &&
                (!sensoresActivados)) {
                naveVector.setIncX(naveVector.getIncX() * FRICCION);
                naveVector.setIncY(naveVector.getIncY() * FRICCION);
            }
            naveVector.incrementaPos(factorMov);
            
            // Actualizar asteroides vectoriales
            for (VectorGrafico a : asteroidesVector) {
                a.incrementaPos(factorMov);
            }
            
            // Actualizar misiles vectoriales
            for (int i = 0; i < misilesVector.size(); i++) {
                VectorGrafico m = misilesVector.get(i);
                m.incrementaPos(factorMov);
                tiempoMisiles.set(i, tiempoMisiles.get(i) - (int)(PERIODO_PROCESO * factorMov));
                if (tiempoMisiles.get(i) <= 0) {
                    misilesVector.remove(i);
                    tiempoMisiles.remove(i);
                    i--;
                }
            }
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            // Control táctil: movimiento direccional directo
            if (tactilActivado && velocidadJoystick > 0 && !enCooldown) {
                // Acelerar directamente en la dirección del joystick (sin girar la nave)
                // Aumentar aceleración base para mayor responsividad
                double aceleracion = PASO_ACELERACION_NAVE * velocidadJoystick * 1.5; // Multiplicador adicional
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    aceleracion *= 1.5;
                }
                
                // Aplicar aceleración directamente en la dirección del joystick
                double aceleracionX = aceleracion * Math.cos(Math.toRadians(direccionJoystick)) * factorMov;
                double aceleracionY = aceleracion * Math.sin(Math.toRadians(direccionJoystick)) * factorMov;
                
                double nIncX = nave.getIncX() + aceleracionX;
                double nIncY = nave.getIncY() + aceleracionY;
                
                // Limitar velocidad máxima
                double maxVelocidad = MAX_VELOCIDAD_NAVE;
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    maxVelocidad *= 1.3;
                }
                
                double velocidadActual = Math.hypot(nIncX, nIncY);
                if (velocidadActual > maxVelocidad) {
                    nIncX = (nIncX / velocidadActual) * maxVelocidad;
                    nIncY = (nIncY / velocidadActual) * maxVelocidad;
                }
                
                nave.setIncX(nIncX);
                nave.setIncY(nIncY);
                
                // Hacer que la nave apunte hacia la dirección del movimiento para mejor feedback visual
                if (velocidadActual > 0.3) {
                    double anguloMovimiento = Math.toDegrees(Math.atan2(nIncY, nIncX));
                    nave.setAngulo(anguloMovimiento);
                }
            } else if (tecladoActivado) {
                // Control de teclado: girar y acelerar hacia adelante
                if (teclaIzquierda || teclaDerecha) {
                    nave.setAngulo(nave.getAngulo() + giroNave * factorMov);
                }
                
                if (teclaArriba) {
                    double aceleracion = PASO_ACELERACION_NAVE;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        aceleracion *= 1.5;
                    }
                    
                    double anguloMovimiento = nave.getAngulo();
                    double nIncX = nave.getIncX() + aceleracion * Math.cos(Math.toRadians(anguloMovimiento)) * factorMov;
                    double nIncY = nave.getIncY() + aceleracion * Math.sin(Math.toRadians(anguloMovimiento)) * factorMov;
                    
                    double maxVelocidad = MAX_VELOCIDAD_NAVE;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        maxVelocidad *= 1.3;
                    }
                    
                    double velocidadActual = Math.hypot(nIncX, nIncY);
                    if (velocidadActual > maxVelocidad) {
                        nIncX = (nIncX / velocidadActual) * maxVelocidad;
                        nIncY = (nIncY / velocidadActual) * maxVelocidad;
                    }
                    
                    nave.setIncX(nIncX);
                    nave.setIncY(nIncY);
                }
            } else if (sensoresActivados && !enCooldown) {
                // Control de sensores: girar y acelerar basado en sensores
                // Aplicar giro de la nave
                if (Math.abs(giroNave) > 0.01) {
                    nave.setAngulo(nave.getAngulo() + giroNave * factorMov);
                }
                
                // Aplicar aceleración basada en sensores
                if (aceleracionNave > 0.01) {
                    double aceleracion = aceleracionNave;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        aceleracion *= 1.5;
                    }
                    
                    double anguloMovimiento = nave.getAngulo();
                    double nIncX = nave.getIncX() + aceleracion * Math.cos(Math.toRadians(anguloMovimiento)) * factorMov;
                    double nIncY = nave.getIncY() + aceleracion * Math.sin(Math.toRadians(anguloMovimiento)) * factorMov;
                    
                    double maxVelocidad = MAX_VELOCIDAD_NAVE;
                    if (powerUpActivo == PowerUpType.VELOCIDAD) {
                        maxVelocidad *= 1.3;
                    }
                    
                    double velocidadActual = Math.hypot(nIncX, nIncY);
                    if (velocidadActual > maxVelocidad) {
                        nIncX = (nIncX / velocidadActual) * maxVelocidad;
                        nIncY = (nIncY / velocidadActual) * maxVelocidad;
                    }
                    
                    nave.setIncX(nIncX);
                    nave.setIncY(nIncY);
                }
            }
            
            // Aplicar fricción cuando no hay input
            if ((!tecladoActivado || (!teclaArriba && !teclaIzquierda && !teclaDerecha)) && 
                (!tactilActivado || velocidadJoystick == 0) &&
                (!sensoresActivados || (aceleracionNave <= 0.01 && Math.abs(giroNave) <= 0.01))) {
                nave.setIncX(nave.getIncX() * FRICCION);
                nave.setIncY(nave.getIncY() * FRICCION);
            }

            nave.incrementaPos(factorMov);
        }

        // Verificar colisión de la nave con asteroides
        if (!juegoTerminado && !pausado && !inmune) {
            if (modoGraficos == 2) {
                // MODO VECTOR PURO
                if (naveVector != null) {
                    for (int j = 0; j < asteroidesVector.size(); j++) {
                        if (naveVector.verificaColision(asteroidesVector.get(j))) {
                            // Verificar si tiene escudo activo ANTES de procesar la colisión
                            // El escudo protege tanto cuando está ACTIVO como cuando está APARICIENDO
                            if (powerUpActivo == PowerUpType.ESCUDO && 
                                (estadoEscudo == EstadoEscudo.ACTIVO || estadoEscudo == EstadoEscudo.APARICIENDO)) {
                                // El escudo protege: destruir el asteroide sin dañar la nave
                                VectorGrafico asteroide = asteroidesVector.get(j);
                                vibrar(50);
                                reproducirSonido(sonidoColision);
                                shakeIntensity = MAX_SHAKE * 0.3f;
                                crearParticulasEscudo(naveVector.getCenX(), naveVector.getCenY());
                                iniciarDestruccionEscudo();
                                
                                // Destruir el asteroide que chocó
                                asteroidesVector.remove(j);
                                
                                // Activar inmunidad temporal para evitar colisiones múltiples
                                inmune = true;
                                tiempoInmunidad = System.currentTimeMillis();
                                
                                // Desactivar escudo
                                powerUpActivo = null;
                                tiempoPowerUp = 0;
                            } else {
                                // No hay escudo o el escudo no está activo: daño normal
                                vidas--;
                                vibrar(200);
                                reproducirSonido(sonidoColision);
                                shakeIntensity = MAX_SHAKE;
                                explosiones.add(new Explosion(naveVector.getCenX(), naveVector.getCenY()));
                                crearParticulasExplosion(naveVector.getCenX(), naveVector.getCenY());
                                if (vidas <= 0) {
                                    juegoTerminado = true;
                                    limpiarEstadoJuego();
                                    detenerJuego();
                                    guardarProgreso();
                                    mostrarDialogoNombre();
                                } else {
                                    naveVector.setCenX(getWidth() / 2);
                                    naveVector.setCenY(getHeight() / 2);
                                    naveVector.setIncX(0);
                                    naveVector.setIncY(0);
                                    inmune = true;
                                    tiempoInmunidad = System.currentTimeMillis();
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                // MODO NORMAL O VECTOR IMAGEN
                if (nave != null) {
                    synchronized (asteroides) {
                        for (int j = 0; j < asteroides.size(); j++) {
                            if (nave.verificaColision(asteroides.get(j))) {
                                // Verificar si tiene escudo activo ANTES de procesar la colisión
                                // El escudo protege tanto cuando está ACTIVO como cuando está APARICIENDO
                                if (powerUpActivo == PowerUpType.ESCUDO && 
                                    (estadoEscudo == EstadoEscudo.ACTIVO || estadoEscudo == EstadoEscudo.APARICIENDO)) {
                                    // El escudo protege: destruir el asteroide sin dañar la nave
                                    Grafico asteroide = asteroides.get(j);
                                    vibrar(50);
                                    reproducirSonido(sonidoColision);
                                    // Efecto shake suave
                                    shakeIntensity = MAX_SHAKE * 0.3f;
                                    // Crear partículas de escudo
                                    crearParticulasEscudo(nave.getCenX(), nave.getCenY());
                                    // Iniciar animación de destrucción del escudo
                                    iniciarDestruccionEscudo();
                                    
                                    // Destruir el asteroide que chocó
                                    asteroides.remove(j);
                                    
                                    // Activar inmunidad temporal para evitar colisiones múltiples
                                    inmune = true;
                                    tiempoInmunidad = System.currentTimeMillis();
                                    
                                    // Desactivar escudo
                                    powerUpActivo = null;
                                    tiempoPowerUp = 0;
                                } else {
                                    // No hay escudo o el escudo no está activo: daño normal
                                    // Perder una vida
                                    vidas--;
                                    vibrar(200);
                                    reproducirSonido(sonidoColision);
                                    
                                    // Efecto shake fuerte
                                    shakeIntensity = MAX_SHAKE;
                                    
                                    // Crear explosión
                                    explosiones.add(new Explosion(nave.getCenX(), nave.getCenY()));
                                    
                                    // Crear partículas de explosión
                                    crearParticulasExplosion(nave.getCenX(), nave.getCenY());
                                    
                                    if (vidas <= 0) {
                                        // El jugador ha muerto
                                        juegoTerminado = true;
                                    limpiarEstadoJuego();
                                        detenerJuego();
                                        guardarProgreso();
                                        mostrarDialogoNombre();
                                    } else {
                                        // Reposicionar nave en el centro
                                        nave.setCenX(getWidth() / 2);
                                        nave.setCenY(getHeight() / 2);
                                        nave.setIncX(0);
                                        nave.setIncY(0);
                                        // Activar inmunidad temporal
                                        inmune = true;
                                        tiempoInmunidad = System.currentTimeMillis();
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // Actualizar inmunidad temporal
        if (inmune) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInmunidad;
            // Si estamos en cooldown, la inmunidad debe durar lo mismo que el cooldown (3 segundos)
            // Si no estamos en cooldown, usar la duración normal de inmunidad (2 segundos)
            long duracionInmunidad = enCooldown ? DURACION_COOLDOWN_MS : DURACION_INMUNIDAD_MS;
            
            if (tiempoTranscurrido >= duracionInmunidad) {
                // Solo desactivar inmunidad si no estamos en cooldown
                // Si estamos en cooldown, la inmunidad se desactivará cuando termine el cooldown
                if (!enCooldown) {
                    inmune = false;
                    alphaInmunidad = 1.0f;
                } else {
                    // Durante cooldown, mantener inmunidad activa
                    alphaInmunidad = (float) (0.3f + 0.7f * Math.sin(tiempoTranscurrido / 50.0));
                }
            } else {
                // Parpadeo para indicar inmunidad
                alphaInmunidad = (float) (0.3f + 0.7f * Math.sin(tiempoTranscurrido / 50.0));
            }
        }
        
        // Actualizar shake
        if (shakeIntensity > 0) {
            shakeOffsetX = (float) ((Math.random() - 0.5) * shakeIntensity);
            shakeOffsetY = (float) ((Math.random() - 0.5) * shakeIntensity);
            shakeIntensity *= SHAKE_DECAY;
            if (shakeIntensity < 0.1f) {
                shakeIntensity = 0;
                shakeOffsetX = 0;
                shakeOffsetY = 0;
            }
        }
        
        // Verificar si se acabaron todos los asteroides para avanzar de nivel
        verificarAvanceNivel();
        
        // Actualizar dificultad progresiva
        actualizarDificultad();
        
        // Actualizar power-ups
        actualizarPowerUps();
        
        // Actualizar explosiones
        actualizarExplosiones();
        
        // Actualizar partículas
        actualizarParticulas();
        
        // Actualizar estela de nave
        actualizarEstelaNave();
        
        // Actualizar orbes de power-up
        actualizarOrbesPowerUp();
        
        // Verificar colisión con orbes de power-up
        verificarColisionOrbes();
        
        // Actualizar efecto de destello
        actualizarDestello();
        
        // Actualizar estadísticas
        if (!juegoTerminado && !pausado) {
            tiempoJuego = System.currentTimeMillis() - tiempoInicio;
        }

        // Verificar colisiones de misiles con asteroides
        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            for (int i = 0; i < misilesVector.size(); i++) {
                for (int j = 0; j < asteroidesVector.size(); j++) {
                    if (misilesVector.get(i).verificaColision(asteroidesVector.get(j))) {
                        VectorGrafico asteroide = asteroidesVector.get(j);
                        explosiones.add(new Explosion(asteroide.getCenX(), asteroide.getCenY()));
                        fragmentaAsteroideVector(j);
                        if (!juegoTerminado) {
                            puntaje += 10;
                            asteroidesDestruidos++;
                            disparosAcertados++;
                            vibrar(30);
                            reproducirSonidoExplosion();
                            crearParticulasExplosion(asteroide.getCenX(), asteroide.getCenY());
                        }
                        misilesVector.remove(i);
                        tiempoMisiles.remove(i);
                        i--;
                        break;
                    }
                }
            }
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            for (int i = 0; i < misiles.size(); i++) {
                misiles.get(i).incrementaPos(factorMov);
                tiempoMisiles.set(i, tiempoMisiles.get(i) - (int) factorMov);
                if (tiempoMisiles.get(i) < 0) {
                    misiles.remove(i);
                    tiempoMisiles.remove(i);
                    i--;
                    continue;
                }
                for (int j = 0; j < asteroides.size(); j++) {
                    if (misiles.get(i).verificaColision(asteroides.get(j))) {
                        Grafico asteroide = asteroides.get(j);
                        // Crear explosión en la posición del asteroide
                        explosiones.add(new Explosion(asteroide.getCenX(), asteroide.getCenY()));
                        
                        fragmentaAsteroide(j);
                        
                        // Incrementar puntaje al destruir/fragmentar un asteroide
                        if (!juegoTerminado) {
                            puntaje += 10;
                            asteroidesDestruidos++;
                            disparosAcertados++;
                            vibrar(30);
                            reproducirSonidoExplosion();
                            
                            // Crear partículas de explosión
                            crearParticulasExplosion(asteroide.getCenX(), asteroide.getCenY());
                        }
                        misiles.remove(i);
                        tiempoMisiles.remove(i);
                        i--;
                        break;
                    }
                }
            }

            for (Grafico a : asteroides) a.incrementaPos(factorMov);
        }

        // Resetear controles solo si no están siendo presionados por ningún método activo
        if (tecladoActivado) {
            if (!teclaArriba) {
                if (!tactilActivado && !sensoresActivados) {
                    aceleracionNave = 0;
                }
            }
            if (!teclaIzquierda && !teclaDerecha) {
                if (!tactilActivado && !sensoresActivados) {
                    giroNave = 0;
                }
            }
        } else {
            if (!tactilActivado && !sensoresActivados) {
                aceleracionNave = 0;
                giroNave = 0;
            }
        }
    }

    // ---------------- THREAD CORREGIDO ----------------
    private class ThreadJuego extends Thread {
        private boolean running = true;

        public void detener() { running = false; }

        @Override
        public void run() {
            while (running) {
                synchronized (VistaJuego.this) {
                    actualizaFisica();
                }
                postInvalidate();
                try { Thread.sleep(PERIODO_PROCESO); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    public void detenerJuego() {
        if (thread != null) thread.detener();
        // Detener inmediatamente toda la música y sonidos cuando se detiene el juego
        detenerMusicaInmediatamente();
        
        // También detener sonido de misión completada si está sonando
        if (sonidoMisionCompletada != null) {
            try {
                if (sonidoMisionCompletada.isPlaying()) {
                    sonidoMisionCompletada.stop();
                }
                sonidoMisionCompletada.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Verificar y actualizar cooldown antes de dibujar (por si acaso)
        // Esto asegura que el cooldown se desactive incluso si el juego está pausado
        if (enCooldown) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicioCooldown;
            if (tiempoTranscurrido >= DURACION_COOLDOWN_MS) {
                enCooldown = false;
                // Resetear controles
                teclaArriba = false;
                teclaIzquierda = false;
                teclaDerecha = false;
                velocidadJoystick = 0;
                direccionJoystick = 0;
                aceleracionNave = 0;
                giroNave = 0;
                joystickThumbX = joystickCenterX;
                joystickThumbY = joystickCenterY;
                joystickActive = false;
                // DESACTIVAR INMUNIDAD al finalizar cooldown
                long tiempoInmunidadTranscurrido = System.currentTimeMillis() - tiempoInmunidad;
                if (tiempoInmunidadTranscurrido >= DURACION_COOLDOWN_MS) {
                    inmune = false;
                    alphaInmunidad = 1.0f;
                }
                // CRÍTICO: FORZAR que el juego continúe después del cooldown
                // Asegurar que el juego esté iniciado y NO pausado
                juegoIniciado = true;
                pausado = false;
                // Asegurar que el thread esté ejecutándose
                if (!isThreadAlive()) {
                    iniciarThreadJuego();
                }
                // Forzar invalidación para redibujar
                postInvalidate();
            }
        }
        
        // Si estamos en pantalla de misión completada, bonus o transición, dibujar esas pantallas
        if (estadoMision == EstadoMision.MISION_COMPLETADA || estadoMision == EstadoMision.PANTALLA_BONUS || estadoMision == EstadoMision.TRANSICION) {
            if (estadoMision == EstadoMision.MISION_COMPLETADA) {
                dibujarPantallaMisionCompletada(canvas);
            } else if (estadoMision == EstadoMision.PANTALLA_BONUS) {
                dibujarPantallaBonus(canvas);
            } else if (estadoMision == EstadoMision.TRANSICION) {
                // Durante la transición, mostrar pantalla negra mientras se prepara el siguiente nivel
                Paint fondoPaint = new Paint();
                fondoPaint.setColor(Color.BLACK);
                canvas.drawRect(0, 0, getWidth(), getHeight(), fondoPaint);
            }
            // Dibujar cooldown si está activo incluso en estas pantallas
            if (enCooldown) {
                dibujarCooldown(canvas);
            }
            return;
        }
        
        // Aplicar shake a todo el canvas
        canvas.save();
        canvas.translate(shakeOffsetX, shakeOffsetY);
        
        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            // Dibujar asteroides vectoriales
            for (VectorGrafico a : asteroidesVector) {
                a.dibujarVector(canvas);
            }
            
            // Dibujar misiles vectoriales
            for (VectorGrafico m : misilesVector) {
                m.dibujarVector(canvas);
            }
            
            // Dibujar nave vectorial con efecto de inmunidad
            if (naveVector != null) {
                if (inmune) {
                    // Aplicar efecto de parpadeo para inmunidad
                    Paint paintNave = new Paint();
                    paintNave.setAlpha((int)(alphaInmunidad * 255));
                    canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int)(alphaInmunidad * 255), Canvas.ALL_SAVE_FLAG);
                    naveVector.dibujarVector(canvas);
                    canvas.restore();
                    
                    // Dibujar aura de inmunidad
                    dibujarAuraInmunidad(canvas, naveVector.getCenX(), naveVector.getCenY());
                } else {
                    naveVector.dibujarVector(canvas);
                }
                
                // Dibujar escudo si está activo (después de la nave para que se vea encima)
                dibujarEscudo(canvas);
            }
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            synchronized (asteroides) {
                for (Grafico a : asteroides) a.dibujaGrafico(canvas);
            }
            
            // Dibujar estelas de misiles
            dibujarEstelasMisiles(canvas);
            
            // Dibujar misiles
            for (Grafico m : misiles) m.dibujaGrafico(canvas);
            
            // Dibujar estela de nave
            dibujarEstelaNave(canvas);
            
            // Dibujar nave con efecto de inmunidad
            if (nave != null) {
                if (inmune) {
                    // Aplicar efecto de parpadeo para inmunidad
                    Paint paintNave = new Paint();
                    paintNave.setAlpha((int)(alphaInmunidad * 255));
                    canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int)(alphaInmunidad * 255), Canvas.ALL_SAVE_FLAG);
                    nave.dibujaGrafico(canvas);
                    canvas.restore();
                    
                    // Dibujar aura de inmunidad
                    dibujarAuraInmunidad(canvas, nave.getCenX(), nave.getCenY());
                } else {
                    nave.dibujaGrafico(canvas);
                }
                
                // Dibujar escudo si está activo (después de la nave para que se vea encima)
                dibujarEscudo(canvas);
            }
        }
        
        // Dibujar partículas
        dibujarParticulas(canvas);
        
        // Dibujar orbes de power-up
        dibujarOrbesPowerUp(canvas);
        
        // Dibujar joystick y botón de disparo si el control táctil está activado
        if (tactilActivado) {
            dibujarJoystick(canvas);
            dibujarBotonDisparo(canvas);
        }
        
        // Restaurar canvas (quitar shake)
        canvas.restore();
        
        // Dibujar efecto de destello (fuera del shake)
        dibujarDestello(canvas);
        
        // Dibujar cooldown si está activo (sobre todo lo demás)
        if (enCooldown) {
            dibujarCooldown(canvas);
        }

        // Dibujar puntaje (más grande, en la parte superior)
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(70); // Aumentado de 40 a 70
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setFakeBoldText(true);
        // Aplicar fuente pixel art
        aplicarFuentePixelArt(paint);
        // Sombra para mejor visibilidad
        paint.setShadowLayer(5, 3, 3, Color.BLACK);
        canvas.drawText("Puntaje: " + puntaje, 20, 100, paint); // Movido más arriba
        
        // Dibujar vidas con corazones (en la esquina superior derecha)
        dibujarVidas(canvas);
        
        // Dibujar nivel
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(5, 3, 3, Color.BLACK);
        aplicarFuentePixelArt(paint);
        canvas.drawText("Nivel: " + nivel, 20, 150, paint); // Ajustado para no tapar el puntaje
        
        // Dibujar power-up activo
        if (powerUpActivo != null) {
            long tiempoRestante = (DURACION_POWERUP_MS - (System.currentTimeMillis() - tiempoPowerUp)) / 1000;
            paint.setTextSize(35);
            paint.setColor(Color.YELLOW);
            aplicarFuentePixelArt(paint);
            String powerUpText = "Power-up: " + powerUpActivo.name() + " (" + tiempoRestante + "s)";
            canvas.drawText(powerUpText, 20, 320, paint); // Movido de 240 a 320
        }
        
        // Dibujar explosiones
        dibujarExplosiones(canvas);
        
        // Dibujar estadísticas
        dibujarEstadisticas(canvas);
        
        // Dibujar mensaje de nivel avanzado
        if (mostrandoNivel) {
            paint.setTextSize(120);
            paint.setColor(Color.YELLOW);
            paint.setFakeBoldText(true);
            paint.setShadowLayer(10, 5, 5, Color.BLACK);
            aplicarFuentePixelArt(paint);
            String mensajeNivel = "NIVEL " + nivel;
            float textWidth = paint.measureText(mensajeNivel);
            canvas.drawText(mensajeNivel, (getWidth() - textWidth) / 2, getHeight() / 2, paint);
        }

        // Dibujar mensaje de juego terminado (solo si no se ha ingresado el nombre aún)
        if (juegoTerminado && !nombreIngresado) {
            paint.setTextSize(60);
            paint.setColor(Color.RED);
            aplicarFuentePixelArt(paint);
            String mensaje = "¡Juego Terminado!";
            float textWidth = paint.measureText(mensaje);
            canvas.drawText(mensaje, (getWidth() - textWidth) / 2, getHeight() / 2, paint);
            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            aplicarFuentePixelArt(paint);
            String puntajeFinal = "Puntaje Final: " + puntaje;
            textWidth = paint.measureText(puntajeFinal);
            canvas.drawText(puntajeFinal, (getWidth() - textWidth) / 2, getHeight() / 2 + 60, paint);
        }
    }
    
    private void dibujarPantallaMisionCompletada(Canvas canvas) {
        // Fondo semi-transparente oscuro
        Paint fondoPaint = new Paint();
        fondoPaint.setColor(Color.argb(200, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), fondoPaint);
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        aplicarFuentePixelArt(paint);
        
        // Título "MISIÓN X"
        paint.setTextSize(100);
        paint.setColor(Color.rgb(255, 200, 0)); // Amarillo-naranja
        paint.setFakeBoldText(true);
        paint.setShadowLayer(15, 5, 5, Color.rgb(200, 0, 0)); // Sombra roja
        
        String tituloMision = "MISIÓN " + nivelCompletado;
        float textWidth = paint.measureText(tituloMision);
        float x = (getWidth() - textWidth) / 2;
        float y = getHeight() / 2 - 80;
        canvas.drawText(tituloMision, x, y, paint);
        
        // Texto "COMPLETADA" con animación de parpadeo
        paint.setTextSize(80);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(10, 3, 3, Color.rgb(150, 0, 150)); // Sombra púrpura
        
        // Animación de parpadeo (cada 500ms)
        long tiempoActual = System.currentTimeMillis();
        boolean mostrar = (tiempoActual / 500) % 2 == 0;
        
        if (mostrar) {
            String completada = "COMPLETADA";
            textWidth = paint.measureText(completada);
            x = (getWidth() - textWidth) / 2;
            y = getHeight() / 2 + 20;
            canvas.drawText(completada, x, y, paint);
        }
    }
    
    private void dibujarPantallaBonus(Canvas canvas) {
        // Fondo oscuro
        Paint fondoPaint = new Paint();
        fondoPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), fondoPaint);
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        aplicarFuentePixelArt(paint);
        
        float centerX = getWidth() / 2;
        float startY = getHeight() / 4;
        float lineHeight = 80;
        float currentY = startY;
        
        // Título
        paint.setTextSize(70);
        paint.setColor(Color.rgb(255, 200, 0));
        paint.setFakeBoldText(true);
        paint.setShadowLayer(10, 3, 3, Color.BLACK);
        String titulo = "MISIÓN " + nivelCompletado;
        float textWidth = paint.measureText(titulo);
        canvas.drawText(titulo, (getWidth() - textWidth) / 2, currentY, paint);
        currentY += lineHeight * 1.5f;
        
        // Texto de bonus más pequeño
        paint.setTextSize(50);
        paint.setFakeBoldText(false);
        paint.setShadowLayer(5, 2, 2, Color.BLACK);
        
        // Clear Bonus
        paint.setColor(Color.WHITE);
        String clearText = "CLEAR BONUS";
        float clearTextWidth = paint.measureText(clearText);
        canvas.drawText(clearText, centerX - 300, currentY, paint);
        
        paint.setColor(Color.YELLOW);
        String clearPoints = String.format("%06d PTS", bonusClearActual);
        canvas.drawText(clearPoints, centerX + 150, currentY, paint);
        currentY += lineHeight;
        
        // Time Bonus (solo mostrar si estamos en este bonus o más avanzado)
        if (indiceBonusMostrando >= 1) {
            paint.setColor(Color.WHITE);
            String timeText = "TIME BONUS";
            canvas.drawText(timeText, centerX - 300, currentY, paint);
            
            paint.setColor(Color.YELLOW);
            String timePoints = String.format("%06d PTS", bonusTiempoActual);
            canvas.drawText(timePoints, centerX + 150, currentY, paint);
            currentY += lineHeight;
        } else {
            currentY += lineHeight;
        }
        
        // Lives Bonus (solo mostrar si estamos en este bonus o más avanzado)
        if (indiceBonusMostrando >= 2) {
            paint.setColor(Color.WHITE);
            String livesText = "LIVES BONUS";
            canvas.drawText(livesText, centerX - 300, currentY, paint);
            
            paint.setColor(Color.YELLOW);
            String livesPoints = String.format("%06d PTS", bonusVidasActual);
            canvas.drawText(livesPoints, centerX + 150, currentY, paint);
            currentY += lineHeight;
        } else {
            currentY += lineHeight;
        }
        
        // Enemies Bonus (solo mostrar si estamos en este bonus o más avanzado)
        if (indiceBonusMostrando >= 3) {
            paint.setColor(Color.WHITE);
            String enemiesText = "ENEMIES BONUS";
            canvas.drawText(enemiesText, centerX - 300, currentY, paint);
            
            paint.setColor(Color.YELLOW);
            String enemiesPoints = String.format("%06d PTS", bonusEnemigosActual);
            canvas.drawText(enemiesPoints, centerX + 150, currentY, paint);
            currentY += lineHeight;
        } else {
            currentY += lineHeight;
        }
        
        // Total Score (solo mostrar si estamos en este bonus o más avanzado)
        if (indiceBonusMostrando >= 4) {
            currentY += lineHeight * 0.5f;
            paint.setTextSize(60);
            paint.setFakeBoldText(true);
            paint.setColor(Color.rgb(0, 255, 255)); // Cyan
            paint.setShadowLayer(8, 3, 3, Color.BLACK);
            String totalText = "TOTAL SCORE";
            float totalTextWidth = paint.measureText(totalText);
            canvas.drawText(totalText, (getWidth() - totalTextWidth) / 2, currentY, paint);
            currentY += lineHeight;
            
            paint.setTextSize(70);
            paint.setColor(Color.YELLOW);
            String totalPoints = String.format("%08d", puntajeTotalAnimado);
            float totalPointsWidth = paint.measureText(totalPoints);
            canvas.drawText(totalPoints, (getWidth() - totalPointsWidth) / 2, currentY, paint);
        }
    }
    
    private void dibujarVidas(Canvas canvas) {
        // Actualizar animación de corazones
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoCambioCorazon > INTERVALO_CORAZON_MS) {
            mostrarCorazon1 = !mostrarCorazon1;
            ultimoCambioCorazon = ahora;
        }
        
        Drawable corazonLleno1 = AppCompatResources.getDrawable(getContext(), R.drawable.ce1);
        Drawable corazonLleno2 = AppCompatResources.getDrawable(getContext(), R.drawable.ce2);
        Drawable corazonVacio = AppCompatResources.getDrawable(getContext(), R.drawable.cv1);
        
        if (corazonLleno1 == null || corazonLleno2 == null || corazonVacio == null) {
            // Si no hay drawables, usar texto como fallback
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setFakeBoldText(true);
            paint.setShadowLayer(5, 3, 3, Color.BLACK);
            canvas.drawText("Vidas: " + vidas, 20, 220, paint);
            return;
        }
        
        // Obtener tamaños reales de los corazones para adaptar el haz de luz
        int tamanoCorazon1 = 0, tamanoCorazon2 = 0;
        if (corazonLleno1 instanceof BitmapDrawable) {
            Bitmap bmp1 = ((BitmapDrawable) corazonLleno1).getBitmap();
            tamanoCorazon1 = Math.max(bmp1.getWidth(), bmp1.getHeight());
        }
        if (corazonLleno2 instanceof BitmapDrawable) {
            Bitmap bmp2 = ((BitmapDrawable) corazonLleno2).getBitmap();
            tamanoCorazon2 = Math.max(bmp2.getWidth(), bmp2.getHeight());
        }
        
        // Convertir dp a píxeles para el tamaño del corazón (más pequeño)
        float density = getContext().getResources().getDisplayMetrics().density;
        int tamanoCorazon = (int) (30 * density); // Reducido de 50dp a 30dp
        int espacioEntreCorazones = (int) (8 * density);
        
        // Posicionar corazones en su posición original (parte superior izquierda)
        int xInicial = (int) (20 * density);
        int y = 220; // Posición original
        
        // Pintura para el haz de luz azul
        Paint paintHaz = new Paint();
        paintHaz.setAntiAlias(true);
        paintHaz.setStyle(Paint.Style.FILL);
        
        // Dibujar corazones llenos con animación y haz de luz
        Drawable corazonLleno = mostrarCorazon1 ? corazonLleno1 : corazonLleno2;
        int tamanoCorazonActual = mostrarCorazon1 ? tamanoCorazon1 : tamanoCorazon2;
        
        for (int i = 0; i < vidas; i++) {
            int x = xInicial + i * (tamanoCorazon + espacioEntreCorazones);
            int centroX = x + tamanoCorazon / 2;
            int centroY = y - tamanoCorazon / 2;
            
            // Dibujar haz de luz azul alrededor del corazón
            // El radio del haz se adapta al tamaño del corazón (ce1 o ce2)
            float radioHaz = tamanoCorazon * 0.75f; // Radio base del haz
            
            // Ajustar el radio según el tamaño real del corazón actual
            if (tamanoCorazonActual > 0 && tamanoCorazon1 > 0 && tamanoCorazon2 > 0) {
                // Calcular factor de escala: si ce1 es más grande, el haz será más grande
                // Si ce2 es más pequeño, el haz será más pequeño
                float factorTamano = (float) tamanoCorazonActual / Math.max(tamanoCorazon1, tamanoCorazon2);
                radioHaz = tamanoCorazon * 0.75f * (0.8f + factorTamano * 0.4f); // Entre 0.8 y 1.2 del tamaño base
            }
            
            // Crear gradiente radial azul para el haz de luz
            RadialGradient gradientHaz = new RadialGradient(
                    centroX, centroY, radioHaz,
                    new int[]{
                            Color.argb(180, 100, 150, 255), // Azul brillante en el centro
                            Color.argb(120, 50, 100, 200),  // Azul medio
                            Color.argb(60, 0, 50, 150),     // Azul oscuro
                            Color.argb(0, 0, 0, 0)           // Transparente en el borde
                    },
                    new float[]{0.0f, 0.3f, 0.6f, 1.0f},
                    Shader.TileMode.CLAMP
            );
            paintHaz.setShader(gradientHaz);
            
            // Dibujar el haz de luz
            canvas.drawCircle(centroX, centroY, radioHaz, paintHaz);
            
            // Dibujar el corazón encima del haz
            corazonLleno.setBounds(x, y - tamanoCorazon, x + tamanoCorazon, y);
            corazonLleno.draw(canvas);
        }
        
        // Dibujar corazones vacíos (sin haz de luz)
        for (int i = vidas; i < 3; i++) {
            int x = xInicial + i * (tamanoCorazon + espacioEntreCorazones);
            corazonVacio.setBounds(x, y - tamanoCorazon, x + tamanoCorazon, y);
            corazonVacio.draw(canvas);
        }
    }
    
    private void dibujarCooldown(Canvas canvas) {
        if (!enCooldown) return;
        
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicioCooldown;
        long tiempoRestante = Math.max(0, DURACION_COOLDOWN_MS - tiempoTranscurrido);
        int segundosRestantes = (int) Math.ceil(tiempoRestante / 1000.0);
        
        // Verificar si el cooldown ya terminó (por si acaso)
        if (tiempoTranscurrido >= DURACION_COOLDOWN_MS) {
            enCooldown = false;
            return;
        }
        
        // Dibujar overlay semi-transparente
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        
        // Dibujar texto de cooldown con fuente pixel art
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        aplicarFuentePixelArt(textPaint); // Aplicar fuente pixel art
        
        // Título
        textPaint.setTextSize(80);
        textPaint.setColor(Color.rgb(255, 200, 0)); // Amarillo
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(15, 5, 5, Color.BLACK);
        String titulo = "ROTACIÓN DETECTADA";
        canvas.drawText(titulo, getWidth() / 2, getHeight() / 2 - 80, textPaint);
        
        // Contador
        textPaint.setTextSize(120);
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(20, 5, 5, Color.rgb(255, 0, 0));
        String contador = String.valueOf(segundosRestantes);
        canvas.drawText(contador, getWidth() / 2, getHeight() / 2 + 40, textPaint);
        
        // Mensaje
        textPaint.setTextSize(50);
        textPaint.setColor(Color.rgb(200, 200, 200));
        textPaint.setFakeBoldText(false);
        textPaint.setShadowLayer(10, 3, 3, Color.BLACK);
        String mensaje = "Espera antes de continuar...";
        canvas.drawText(mensaje, getWidth() / 2, getHeight() / 2 + 120, textPaint);
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        if (!tecladoActivado || juegoTerminado || enCooldown) return false;
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                teclaArriba = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                teclaIzquierda = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                teclaDerecha = true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_SPACE:
                activaMisil();
                break;
            default: procesada = false; break;
        }
        return procesada;
    }
    
    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        if (!tecladoActivado || enCooldown) return false;
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                teclaArriba = false;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                teclaIzquierda = false;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                teclaDerecha = false;
                break;
            default: procesada = false; break;
        }
        return procesada;
    }

    private float mX = 0, mY = 0;
    private boolean disparo = false;
    private float botonDisparoX = 0;
    private float botonDisparoY = 0;
    private float botonDisparoRadius = 100f; // Duplicado: 50 * 2
    private boolean botonDisparoPresionado = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!tactilActivado || juegoTerminado || pausado || enCooldown) return false;
        super.onTouchEvent(event);
        
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        
        // Verificar si toca el botón de disparo
        float distDisparo = (float) Math.hypot(x - botonDisparoX, y - botonDisparoY);
        boolean tocaBotonDisparo = distDisparo <= botonDisparoRadius * 1.5f;
        
        // Verificar si toca el joystick
        float distJoystick = (float) Math.hypot(x - joystickCenterX, y - joystickCenterY);
        boolean tocaJoystick = distJoystick <= joystickRadius * 1.5f;
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (tocaBotonDisparo && !botonDisparoPresionado) {
                    botonDisparoPresionado = true;
                    activaMisil();
                } else if (tocaJoystick && !joystickActive) {
                    joystickActive = true;
                    joystickPointerId = pointerId;
                    joystickThumbX = x;
                    joystickThumbY = y;
                    actualizarJoystick(x, y);
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (joystickActive && pointerId == joystickPointerId) {
                    joystickThumbX = x;
                    joystickThumbY = y;
                    actualizarJoystick(x, y);
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (pointerId == joystickPointerId) {
                    joystickActive = false;
                    joystickThumbX = joystickCenterX;
                    joystickThumbY = joystickCenterY;
                    velocidadJoystick = 0;
                    aceleracionNave = 0;
                    joystickPointerId = -1;
                }
                if (pointerId == joystickPointerId || tocaBotonDisparo) {
                    botonDisparoPresionado = false;
                }
                break;
                
            case MotionEvent.ACTION_CANCEL:
                joystickActive = false;
                joystickThumbX = joystickCenterX;
                joystickThumbY = joystickCenterY;
                velocidadJoystick = 0;
                aceleracionNave = 0;
                botonDisparoPresionado = false;
                joystickPointerId = -1;
                break;
        }
        
        invalidate(); // Redibujar para actualizar el joystick
        return true;
    }
    
    private void actualizarJoystick(float x, float y) {
        float dx = x - joystickCenterX;
        float dy = y - joystickCenterY;
        float distance = (float) Math.hypot(dx, dy);
        
        // Limitar el thumbstick al radio del joystick
        if (distance > joystickRadius) {
            dx = (dx / distance) * joystickRadius;
            dy = (dy / distance) * joystickRadius;
            joystickThumbX = joystickCenterX + dx;
            joystickThumbY = joystickCenterY + dy;
            distance = joystickRadius;
        }
        
        // Calcular dirección del joystick en grados
        // En Android, y aumenta hacia abajo
        // Cuando joystick está arriba: dy < 0 → ángulo negativo → nave debe ir arriba (Y negativo)
        // Cuando joystick está abajo: dy > 0 → ángulo positivo → nave debe ir abajo (Y positivo)
        if (distance > 5) { // Zona muerta reducida para mayor responsividad
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            direccionJoystick = angle;
            velocidadJoystick = Math.min(1.0, distance / joystickRadius);
        } else {
            // Zona muerta: no hay input
            velocidadJoystick = 0;
        }
    }

    private void fragmentaAsteroide(int i) {
        synchronized (asteroides) {
            if (i >= asteroides.size()) return;
            Grafico asteroide = asteroides.get(i);
            Integer nivelFragmentacion = fragmentacionAsteroides.get(asteroide);
            if (nivelFragmentacion == null) nivelFragmentacion = 0;
            
            if (nivelFragmentacion == 0) {
                // Primera fragmentación: dividir en 2
                fragmentacionAsteroides.remove(asteroide);
                asteroides.remove(i);
                
                SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                int modoGraficos = pref.getInt("graficos_mode", 0);
                Drawable drawableAsteroide = AppCompatResources.getDrawable(getContext(),
                        modoGraficos == 0 ? R.drawable.asteroidevector : R.drawable.asteroide1);
                
                Drawable drawableAsteroideReducido;
                if (drawableAsteroide instanceof BitmapDrawable) {
                    Bitmap bmpAst = ((BitmapDrawable) drawableAsteroide).getBitmap();
                    int nuevoAncho = (int) (bmpAst.getWidth() * 0.2); // Más pequeño
                    int nuevoAlto = (int) (bmpAst.getHeight() * 0.2);
                    Bitmap bmpAstReducido = Bitmap.createScaledBitmap(bmpAst, nuevoAncho, nuevoAlto, true);
                    drawableAsteroideReducido = new BitmapDrawable(getResources(), bmpAstReducido);
                } else {
                    drawableAsteroideReducido = drawableAsteroide;
                }
                
                // Factor de velocidad según el nivel (aumenta 20% por nivel)
                double factorVelocidad = 1.0 + (nivel - 1) * 0.2;
                
                // Obtener número de fragmentos configurado
                SharedPreferences prefFragmentos = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                numFragmentosConfigurado = prefFragmentos.getInt("num_fragmentos", 5);
                int numFragmentosPrimera = Math.max(2, numFragmentosConfigurado / 2); // Primera fragmentación: mitad del configurado, mínimo 2
                
                // Crear fragmentos según configuración
                for (int f = 0; f < numFragmentosPrimera; f++) {
                    Grafico fragmento = new Grafico(this, drawableAsteroideReducido);
                    fragmento.setCenX(asteroide.getCenX());
                    fragmento.setCenY(asteroide.getCenY());
                    // Velocidad de fragmentos aumentada según el nivel
                    fragmento.setIncX((asteroide.getIncX() + (Math.random() * 4 - 2)) * factorVelocidad);
                    fragmento.setIncY((asteroide.getIncY() + (Math.random() * 4 - 2)) * factorVelocidad);
                    fragmento.setAngulo(Math.random() * 360);
                    fragmento.setRotacion(Math.random() * 8 - 4);
                    fragmento.setRadioColision(fragmento.getAncho() / 3);
                    asteroides.add(fragmento);
                    fragmentacionAsteroides.put(fragmento, 1); // Nivel 1
                }
            } else if (nivelFragmentacion == 1) {
                // Segunda fragmentación: dividir en 4
                fragmentacionAsteroides.remove(asteroide);
                asteroides.remove(i);
                
                SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                int modoGraficos = pref.getInt("graficos_mode", 0);
                Drawable drawableAsteroide = AppCompatResources.getDrawable(getContext(),
                        modoGraficos == 0 ? R.drawable.asteroidevector : R.drawable.asteroide1);
                
                Drawable drawableAsteroideReducido;
                if (drawableAsteroide instanceof BitmapDrawable) {
                    Bitmap bmpAst = ((BitmapDrawable) drawableAsteroide).getBitmap();
                    int nuevoAncho = (int) (bmpAst.getWidth() * 0.1); // Aún más pequeño
                    int nuevoAlto = (int) (bmpAst.getHeight() * 0.1);
                    Bitmap bmpAstReducido = Bitmap.createScaledBitmap(bmpAst, nuevoAncho, nuevoAlto, true);
                    drawableAsteroideReducido = new BitmapDrawable(getResources(), bmpAstReducido);
                } else {
                    drawableAsteroideReducido = drawableAsteroide;
                }
                
                // Factor de velocidad según el nivel (aumenta 20% por nivel)
                double factorVelocidad = 1.0 + (nivel - 1) * 0.2;
                
                // Obtener número de fragmentos configurado
                SharedPreferences prefFragmentos = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                numFragmentosConfigurado = prefFragmentos.getInt("num_fragmentos", 5);
                int numFragmentosSegunda = Math.max(4, numFragmentosConfigurado); // Segunda fragmentación: número configurado, mínimo 4
                
                // Crear fragmentos según configuración
                for (int f = 0; f < numFragmentosSegunda; f++) {
                    Grafico fragmento = new Grafico(this, drawableAsteroideReducido);
                    fragmento.setCenX(asteroide.getCenX());
                    fragmento.setCenY(asteroide.getCenY());
                    // Velocidad de fragmentos aumentada según el nivel
                    fragmento.setIncX((asteroide.getIncX() + (Math.random() * 4 - 2)) * factorVelocidad);
                    fragmento.setIncY((asteroide.getIncY() + (Math.random() * 4 - 2)) * factorVelocidad);
                    fragmento.setAngulo(Math.random() * 360);
                    fragmento.setRotacion(Math.random() * 8 - 4);
                    fragmento.setRadioColision(fragmento.getAncho() / 3);
                    asteroides.add(fragmento);
                    fragmentacionAsteroides.put(fragmento, 2); // Nivel 2
                }
            } else {
                // Nivel 2: desaparecer completamente
                float xAsteroide = asteroide.getCenX();
                float yAsteroide = asteroide.getCenY();
                fragmentacionAsteroides.remove(asteroide);
                asteroides.remove(i);
                
                // Incrementar contador de enemigos derrotados
                if (estadoMision == EstadoMision.NORMAL) {
                    enemigosDerrotados++;
                }
                
                // Probabilidad de generar orbe de power-up al destruir completamente
                if (Math.random() < PROBABILIDAD_ORBE_DE_ASTEROIDE && orbesPowerUp.size() < 3) {
                    PowerUpType[] tipos = PowerUpType.values();
                    PowerUpType tipo = tipos[(int) (Math.random() * tipos.length)];
                    orbesPowerUp.add(new PowerUpOrb(xAsteroide, yAsteroide, tipo, getContext()));
                }
            }
        }
    }

    private void activaMisil() {
        long ahora = System.currentTimeMillis();
        
        // Verificar si podemos disparar (cooldown después de la secuencia completa)
        if (disparosEnSecuencia == 0) {
            if (ahora - ultimoDisparo < COOLDOWN_DISPARO_MS) {
                return; // Todavía en cooldown
            }
            // Iniciar nueva secuencia
            disparosEnSecuencia = DISPAROS_POR_SECUENCIA;
            if (powerUpActivo == PowerUpType.DISPARO_MEJORADO) {
                disparosEnSecuencia = 5;
            }
            ultimoDisparo = ahora;
            ultimoDisparoEnSecuencia = ahora;
            
            // Disparar el primer misil inmediatamente
            dispararUnMisil();
            disparosEnSecuencia--;
            
            // Programar los otros disparos con delay de 0.2s entre cada uno
            if (disparosEnSecuencia > 0) {
                for (int i = 1; i <= disparosEnSecuencia; i++) {
                    final int index = i;
                    final int delay = i * (int)DELAY_ENTRE_DISPAROS_MS;
                    postDelayed(() -> {
                        if (disparosEnSecuencia > 0) {
                            dispararUnMisil();
                            disparosEnSecuencia--;
                            if (disparosEnSecuencia == 0) {
                                ultimoDisparo = System.currentTimeMillis();
                            }
                        }
                    }, delay);
                }
            } else {
                ultimoDisparo = ahora;
            }
        }
    }
    
    private void dispararUnMisil() {
        if (juegoTerminado || pausado) return;
        
        // Contar disparo realizado
        disparosRealizados++;
        
        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            if (naveVector == null) return;
            
            double angulo = naveVector.getAngulo();
            VectorGrafico misil = new VectorGrafico(this, VectorGrafico.TipoVector.MISIL, 
                naveVector.getCenX(), naveVector.getCenY());
            misil.setAngulo(angulo);
            misil.setIncX(Math.cos(Math.toRadians(angulo)) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(angulo)) * PASO_VELOCIDAD_MISIL);
            
            int tiempo = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), 
                this.getHeight() / Math.abs(misil.getIncY())) - 2;
            misilesVector.add(misil);
            tiempoMisiles.add(tiempo);
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            if (nave == null) return;
            
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(powerUpActivo == PowerUpType.DISPARO_MEJORADO ? Color.YELLOW : Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.getPaint().setStrokeWidth(2);
            dMisil.setIntrinsicWidth(30);
            dMisil.setIntrinsicHeight(6);
            Grafico misil = new Grafico(this, dMisil);
            misil.setRadioColision((misil.getAncho() + misil.getAlto()) / 4);

            double angulo = nave.getAngulo();
            
            misil.setCenX(nave.getCenX());
            misil.setCenY(nave.getCenY());
            misil.setAngulo(angulo);
            misil.setIncX(Math.cos(Math.toRadians(angulo)) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(angulo)) * PASO_VELOCIDAD_MISIL);

            int tiempo = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), 
                this.getHeight() / Math.abs(misil.getIncY())) - 2;
            misiles.add(misil);
            tiempoMisiles.add(tiempo);
        }
        
        // Vibración y sonido para cada disparo
        vibrar(20);
        reproducirSonido(sonidoDisparo);
    }
    
    private void fragmentaAsteroideVector(int i) {
        if (i >= asteroidesVector.size()) return;
        VectorGrafico asteroide = asteroidesVector.get(i);
        
        // Simplemente remover el asteroide (sin fragmentación compleja por ahora)
        // Para simplificar, solo removemos el asteroide y generamos un orbe si corresponde
        float xAsteroide = asteroide.getCenX();
        float yAsteroide = asteroide.getCenY();
        asteroidesVector.remove(i);
        
        // Incrementar contador de enemigos derrotados
        if (estadoMision == EstadoMision.NORMAL) {
            enemigosDerrotados++;
        }
        
        // Probabilidad de generar orbe de power-up
        if (Math.random() < PROBABILIDAD_ORBE_DE_ASTEROIDE && orbesPowerUp.size() < 3) {
            PowerUpType[] tipos = PowerUpType.values();
            PowerUpType tipo = tipos[(int) (Math.random() * tipos.length)];
            orbesPowerUp.add(new PowerUpOrb(xAsteroide, yAsteroide, tipo, getContext()));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!sensoresActivados || juegoTerminado || enCooldown || pausado) return;
        
        int tipoSensor = event.sensor.getType();
        
        if (tipoSensor == Sensor.TYPE_ACCELEROMETER) {
            // Guardar valores del acelerómetro
            valoresAcelerometro[0] = event.values[0];
            valoresAcelerometro[1] = event.values[1];
            valoresAcelerometro[2] = event.values[2];
        } else if (tipoSensor == Sensor.TYPE_GYROSCOPE) {
            // Guardar valores del giroscopio
            valoresGiroscopio[0] = event.values[0];
            valoresGiroscopio[1] = event.values[1];
            valoresGiroscopio[2] = event.values[2];
        }
        
        // Usar giroscopio para rotación (más preciso y responsivo)
        if (giroscopio != null && tipoSensor == Sensor.TYPE_GYROSCOPE) {
            float rotacionZ = event.values[2]; // Rotación alrededor del eje Z (yaw) en radianes/segundo
            // Aumentar sensibilidad del giroscopio para que sea más responsivo
            if (Math.abs(rotacionZ) > 0.05f) {
                // Convertir radianes/segundo a grados y aplicar factor de sensibilidad mayor
                // Multiplicar por 15 en lugar de 10 para mayor sensibilidad
                giroNave = Math.max(-PASO_GIRO_NAVE, Math.min(PASO_GIRO_NAVE, -rotacionZ * 15.0));
            } else {
                // Solo suavizar si no hay otros controles activos
                if (!tecladoActivado && !tactilActivado) {
                    giroNave *= 0.85; // Suavizar el giro cuando no hay rotación
                }
            }
        } else if (tipoSensor == Sensor.TYPE_ACCELEROMETER) {
            // Si no hay giroscopio, usar acelerómetro para rotación
            float x = event.values[0];
            if (Math.abs(x) > 0.3f) {
                // Aumentar sensibilidad del acelerómetro
                giroNave = Math.max(-PASO_GIRO_NAVE, Math.min(PASO_GIRO_NAVE, -x * 0.8));
            } else {
                if (!tecladoActivado && !tactilActivado) {
                    giroNave *= 0.85; // Suavizar el giro
                }
            }
            
            // Usar acelerómetro para aceleración (inclinación hacia adelante)
            float y = event.values[1];
            // Aceleración basada en inclinación vertical (hacia adelante)
            // Reducir el umbral para que sea más sensible
            if (y < -1.5f) {
                aceleracionNave = Math.min(PASO_ACELERACION_NAVE, Math.abs(y) * 0.3);
            } else {
                if (!tecladoActivado && !tactilActivado) {
                    aceleracionNave *= 0.9; // Suavizar la desaceleración
                }
            }
        }
    }

    private void mostrarDialogoNombre() {
        if (nombreIngresado) return;
        nombreIngresado = true;
        
        Context context = getContext();
        if (!(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("¡Juego Terminado!");
            builder.setMessage("Ingresa tu nombre para guardar tu puntaje: " + puntaje);
            
            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Tu nombre");
            input.setMaxLines(1);
            builder.setView(input);
            
            builder.setPositiveButton("Guardar", (dialog, which) -> {
                String nombre = input.getText().toString().trim();
                if (nombre.isEmpty()) {
                    nombre = "Jugador";
                }
                // Limitar longitud del nombre
                if (nombre.length() > 20) {
                    nombre = nombre.substring(0, 20);
                }
                guardarPuntaje(nombre);
                // Cerrar la actividad después de guardar
                activity.finish();
            });
            
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
                activity.finish();
            });
            
            builder.setCancelable(false);
            builder.show();
        });
    }
    
    private void guardarPuntaje(String nombre) {
        if (dbHelper != null && puntaje > 0) {
            dbHelper.insertScore(puntaje, nombre);
        }
    }

    public int getPuntaje() {
        return puntaje;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    
    public boolean isJuegoIniciado() {
        return juegoIniciado;
    }
    
    public void setPausado(boolean pausado) {
        this.pausado = pausado;
        // Si se pausa, detener inmediatamente la música
        if (pausado) {
            detenerMusicaInmediatamente();
        } else if (!pausado && juegoIniciado && !juegoTerminado && !audioMuteado) {
            // Si se despausa el juego, intentar reproducir la música
            reproducirMusicaJuego();
        }
    }
    
    // Método para detener la música inmediatamente
    private void detenerMusicaInmediatamente() {
        if (sonidoNivel1 != null) {
            try {
                if (sonidoNivel1.isPlaying()) {
                    sonidoNivel1.stop(); // Detener inmediatamente, no pausar
                }
                sonidoNivel1.reset(); // Resetear para asegurar que se detenga completamente
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isPausado() {
        return pausado;
    }
    
    // Método para iniciar cooldown después de rotación
    public void iniciarCooldownRotacion() {
        enCooldown = true;
        tiempoInicioCooldown = System.currentTimeMillis();
        // Resetear controles inmediatamente
        teclaArriba = false;
        teclaIzquierda = false;
        teclaDerecha = false;
        velocidadJoystick = 0;
        direccionJoystick = 0;
        aceleracionNave = 0;
        giroNave = 0;
        // IMPORTANTE: El cooldown NO pausa el juego, solo desactiva los controles
        // Asegurar que el juego NO esté pausado durante el cooldown
        if (juegoIniciado) {
            pausado = false;
        }
        // ACTIVAR INMUNIDAD durante el cooldown para proteger al jugador
        inmune = true;
        tiempoInmunidad = System.currentTimeMillis();
        // También resetear joystick visual
        joystickThumbX = joystickCenterX;
        joystickThumbY = joystickCenterY;
        joystickActive = false;
    }
    
    // Métodos para activar/desactivar sensores dinámicamente
    public void activarSensores() {
        // Primero desactivar sensores para evitar registros duplicados
        desactivarSensores();
        
        if (mSensorManager == null) {
            Context context = getContext();
            if (context != null) {
                mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            }
        }
        if (mSensorManager != null) {
            // Obtener acelerómetro
            if (acelerometro == null) {
                List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                if (!listSensors.isEmpty()) {
                    acelerometro = listSensors.get(0);
                }
            }
            
            // Obtener giroscopio
            if (giroscopio == null) {
                List<Sensor> listGiroscopio = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
                if (!listGiroscopio.isEmpty()) {
                    giroscopio = listGiroscopio.get(0);
                }
            }
            
            // Registrar acelerómetro
            if (acelerometro != null) {
                try {
                    mSensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Registrar giroscopio si está disponible
            if (giroscopio != null) {
                try {
                    mSensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_GAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            sensoresActivados = true;
        }
    }
    
    public void desactivarSensores() {
        if (mSensorManager != null) {
            try {
                mSensorManager.unregisterListener(this);
                sensoresActivados = false;
            } catch (Exception e) {
                // Si hay algún error al desregistrar, simplemente marcar como desactivado
                sensoresActivados = false;
            }
        }
    }
    
    public void actualizarConfiguracionSensores() {
        if (getContext() == null) return;
        
        SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean sensoresActivadosNuevo = pref.getBoolean("entrada_sensor", true);
        
        // Actualizar otras configuraciones primero
        tecladoActivado = pref.getBoolean("entrada_teclado", true);
        tactilActivado = pref.getBoolean("entrada_tactil", true);
        
        // Si cambió el estado de los sensores, actualizar
        if (sensoresActivadosNuevo != sensoresActivados) {
            if (sensoresActivadosNuevo) {
                // Activar sensores (esto también los desregistra primero para evitar duplicados)
                activarSensores();
            } else {
                // Desactivar sensores
                desactivarSensores();
            }
        } else if (sensoresActivadosNuevo && sensoresActivados) {
            // Si ya están activados pero puede que no estén registrados correctamente, reactivarlos
            // Esto asegura que funcionen incluso si hubo algún problema
            activarSensores();
        }
    }
    
    // Método público para generar asteroides si es necesario (llamado desde Juego.java)
    public void generarAsteroidesSiNecesario() {
        if (getContext() == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        
        boolean necesitaAsteroides = false;
        if (modoGraficos == 2) {
            necesitaAsteroides = (asteroidesVector == null || asteroidesVector.isEmpty());
        } else {
            synchronized (asteroides) {
                necesitaAsteroides = (asteroides == null || asteroides.isEmpty());
            }
        }
        
        if (necesitaAsteroides) {
            try {
                generarAsteroidesNivel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void verificarAvanceNivel() {
        if (pausado || juegoTerminado) return;
        
        // Actualizar estado de misión
        if (estadoMision != EstadoMision.NORMAL) {
            actualizarEstadoMision();
            return;
        }
        
        // Ocultar mensaje de nivel después del tiempo establecido
        if (mostrandoNivel) {
            if (System.currentTimeMillis() - tiempoMostrarNivel > TIEMPO_MOSTRAR_NIVEL_MS) {
                mostrandoNivel = false;
                // Inicializar tiempo de inicio del nivel cuando el mensaje desaparece
                tiempoInicioNivel = System.currentTimeMillis();
                vidasInicialesNivel = vidas;
                enemigosDerrotados = 0;
                // Asegurar que el juego NO esté pausado cuando termina el mensaje
                pausado = false;
                juegoIniciado = true;
                // Asegurar que el thread esté ejecutándose
                if (!isThreadAlive()) {
                    iniciarThreadJuego();
                }
            }
            return; // No verificar avance mientras se muestra el mensaje
        }
        
        // Verificar si no hay asteroides
        boolean asteroidesVacios = false;
        if (modoGraficos == 2) {
            asteroidesVacios = asteroidesVector.isEmpty();
        } else {
            synchronized (asteroides) {
                asteroidesVacios = asteroides.isEmpty();
            }
        }
        
        if (asteroidesVacios && estadoMision == EstadoMision.NORMAL) {
            // Iniciar secuencia de finalización de misión
            iniciarMisionCompletada();
        }
    }
    
    private void generarAsteroidesNivel() {
        // Verificar que el contexto y las dimensiones estén disponibles
        if (getContext() == null) {
            return; // No generar asteroides si no hay contexto
        }
        
        int ancho = getWidth();
        int alto = getHeight();
        if (ancho <= 0 || alto <= 0) {
            // Si las dimensiones no están listas, intentar más tarde
            post(new Runnable() {
                @Override
                public void run() {
                    if (getWidth() > 0 && getHeight() > 0) {
                        generarAsteroidesNivel();
                    }
                }
            });
            return;
        }
        
        // Reproducir música del juego para todos los niveles
        reproducirMusicaJuego();
        
        // Número de asteroides aumenta con el nivel (5 + nivel)
        numAsteroides = 5 + nivel;
        if (numAsteroides > 15) numAsteroides = 15; // Máximo 15 asteroides
        
        if (modoGraficos == 2) {
            // MODO VECTOR PURO
            if (asteroidesVector == null) {
                asteroidesVector = new ArrayList<>();
            }
            asteroidesVector.clear();
            if (ancho > 0 && alto > 0) {
                int distanciaMinima = Math.max(ancho / 4, alto / 4);
                int distanciaMinimaJugador = Math.max(ancho / 3, alto / 3);
                int[] posicionJugador = new int[]{ancho / 2, alto / 2};
                
                for (int i = 0; i < numAsteroides; i++) {
                    int x, y;
                    boolean valido;
                    int intentos = 0;
                    do {
                        x = (int) (Math.random() * ancho);
                        y = (int) (Math.random() * alto);
                        valido = true;
                        
                        double distanciaJugador = Math.hypot(x - posicionJugador[0], y - posicionJugador[1]);
                        if (distanciaJugador < distanciaMinimaJugador) {
                            valido = false;
                        }
                        
                        if (valido) {
                            for (VectorGrafico a : asteroidesVector) {
                                if (Math.hypot(x - a.getCenX(), y - a.getCenY()) < distanciaMinima) {
                                    valido = false;
                                    break;
                                }
                            }
                        }
                        
                        if (!valido) intentos++;
                        if (intentos > 500) {
                            distanciaMinima = Math.max(ancho / 6, Math.max(alto / 6, distanciaMinima - 20));
                            distanciaMinimaJugador = Math.max(ancho / 4, Math.max(alto / 4, distanciaMinimaJugador - 20));
                            intentos = 0;
                        }
                    } while (!valido);
                    
                    double factorVelocidad = 1.0 + (nivel - 1) * 0.2;
                    VectorGrafico asteroide = new VectorGrafico(this, VectorGrafico.TipoVector.ASTEROIDE, x, y);
                    asteroide.setIncX((Math.random() * 4 - 2) * factorVelocidad);
                    asteroide.setIncY((Math.random() * 4 - 2) * factorVelocidad);
                    asteroide.setAngulo(Math.random() * 360);
                    asteroide.setRotacion(Math.random() * 8 - 4);
                    asteroidesVector.add(asteroide);
                }
            }
        } else {
            // MODO NORMAL O VECTOR IMAGEN
            try {
                SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                int modoGraficos = pref.getInt("graficos_mode", 0);
                
                Drawable drawableAsteroide = AppCompatResources.getDrawable(getContext(),
                        modoGraficos == 0 ? R.drawable.asteroidevector : R.drawable.asteroide1);
                
                if (drawableAsteroide == null) {
                    // Si no se puede cargar el drawable, usar un fallback
                    return;
                }
                
                // Factor de velocidad según el nivel (aumenta 20% por nivel)
                double factorVelocidad = 1.0 + (nivel - 1) * 0.2;
                
                if (asteroides == null) {
                    asteroides = new ArrayList<>();
                }
                if (fragmentacionAsteroides == null) {
                    fragmentacionAsteroides = new HashMap<>();
                }
                
                synchronized (asteroides) {
                    asteroides.clear();
                    fragmentacionAsteroides.clear();
                    
                    for (int i = 0; i < numAsteroides; i++) {
                        Drawable drawableAsteroideReducido;
                        if (drawableAsteroide instanceof BitmapDrawable) {
                            Bitmap bmpAst = ((BitmapDrawable) drawableAsteroide).getBitmap();
                            if (bmpAst != null) {
                                int nuevoAncho = (int) (bmpAst.getWidth() * 0.4); // 40% del tamaño original
                                int nuevoAlto = (int) (bmpAst.getHeight() * 0.4);
                                Bitmap bmpAstReducido = Bitmap.createScaledBitmap(bmpAst, nuevoAncho, nuevoAlto, true);
                                drawableAsteroideReducido = new BitmapDrawable(getResources(), bmpAstReducido);
                            } else {
                                drawableAsteroideReducido = drawableAsteroide;
                            }
                        } else {
                            drawableAsteroideReducido = drawableAsteroide;
                        }
                        
                        Grafico asteroide = new Grafico(this, drawableAsteroideReducido);
                        // Velocidad base aumentada según el nivel
                        double velocidadBase = 2.0 * factorVelocidad;
                        asteroide.setIncX((Math.random() * 4 - 2) * factorVelocidad);
                        asteroide.setIncY((Math.random() * 4 - 2) * factorVelocidad);
                        asteroide.setAngulo(Math.random() * 360);
                        asteroide.setRotacion(Math.random() * 8 - 4);
                        asteroide.setRadioColision(asteroide.getAncho() / 3);
                        asteroides.add(asteroide);
                        fragmentacionAsteroides.put(asteroide, 0);
                    }
                }
                
                // Reposicionar asteroides
                posicionesInicializadas = false;
                if (getWidth() > 0 && getHeight() > 0) {
                    onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Si hay un error, intentar continuar sin asteroides o con un conjunto básico
            }
        }
    }
    
    private void actualizarDificultad() {
        if (pausado || juegoTerminado) return;
        
        // La dificultad ahora se maneja por niveles, no por tiempo
        // Este método se mantiene por compatibilidad pero ya no se usa
    }
    
    private void agregarAsteroide() {
        SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int modoGraficos = pref.getInt("graficos_mode", 0);
        Drawable drawableAsteroide = AppCompatResources.getDrawable(getContext(),
                modoGraficos == 0 ? R.drawable.asteroidevector : R.drawable.asteroide1);
        
        Drawable drawableAsteroideReducido;
        if (drawableAsteroide instanceof BitmapDrawable) {
            Bitmap bmpAst = ((BitmapDrawable) drawableAsteroide).getBitmap();
            int nuevoAncho = (int) (bmpAst.getWidth() * 0.4);
            int nuevoAlto = (int) (bmpAst.getHeight() * 0.4);
            Bitmap bmpAstReducido = Bitmap.createScaledBitmap(bmpAst, nuevoAncho, nuevoAlto, true);
            drawableAsteroideReducido = new BitmapDrawable(getResources(), bmpAstReducido);
        } else {
            drawableAsteroideReducido = drawableAsteroide;
        }
        
        Grafico nuevoAsteroide = new Grafico(this, drawableAsteroideReducido);
        nuevoAsteroide.setCenX((int) (Math.random() * getWidth()));
        nuevoAsteroide.setCenY((int) (Math.random() * getHeight()));
        nuevoAsteroide.setIncX(Math.random() * 4 - 2);
        nuevoAsteroide.setIncY(Math.random() * 4 - 2);
        nuevoAsteroide.setAngulo(Math.random() * 360);
        nuevoAsteroide.setRotacion(Math.random() * 8 - 4);
        nuevoAsteroide.setRadioColision(nuevoAsteroide.getAncho() / 3);
        
        synchronized (asteroides) {
            asteroides.add(nuevoAsteroide);
            fragmentacionAsteroides.put(nuevoAsteroide, 0);
        }
    }
    
    private void actualizarPowerUps() {
        if (powerUpActivo != null) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoPowerUp;
            if (tiempoTranscurrido >= DURACION_POWERUP_MS) {
                // Desactivar power-up
                if (powerUpActivo == PowerUpType.ESCUDO) {
                    // Iniciar animación de destrucción cuando el tiempo se agota
                    iniciarDestruccionEscudo();
                }
                powerUpActivo = null;
                tiempoPowerUp = 0;
            } else {
                // Aplicar efectos del power-up
                if (powerUpActivo == PowerUpType.VELOCIDAD) {
                    // Velocidad aumentada ya está en el factor de movimiento
                } else if (powerUpActivo == PowerUpType.DISPARO_MEJORADO) {
                    // Disparo mejorado se maneja en activaMisil
                } else if (powerUpActivo == PowerUpType.ESCUDO) {
                    // Actualizar estado del escudo
                    actualizarEstadoEscudo();
                }
            }
        } else {
            // Actualizar animación de destrucción incluso si no hay power-up activo
            actualizarEstadoEscudo();
        }
    }
    
    private void actualizarEstadoEscudo() {
        if (estadoEscudo == EstadoEscudo.NONE) return;
        
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoEstadoEscudo;
        
        switch (estadoEscudo) {
            case APARICIENDO:
                if (tiempoTranscurrido >= DURACION_APARICION_MS) {
                    // Cambiar a estado activo
                    estadoEscudo = EstadoEscudo.ACTIVO;
                    tiempoEstadoEscudo = System.currentTimeMillis();
                    cargarEscudoGrafico(R.drawable.es2); // Escudo completo
                } else {
                    // Durante la aparición, cambiar entre es1 y es1.5
                    if (tiempoTranscurrido < DURACION_APARICION_MS / 2) {
                        cargarEscudoGrafico(R.drawable.es1);
                    } else {
                        cargarEscudoGrafico(R.drawable.es1_5);
                    }
                }
                break;
                
            case ACTIVO:
                // Mantener es2 mientras el escudo está activo
                if (escudoGrafico == null) {
                    cargarEscudoGrafico(R.drawable.es2);
                }
                break;
                
            case DESTRUYENDO:
                if (tiempoTranscurrido >= DURACION_DESTRUCCION_MS) {
                    // Terminar animación de destrucción
                    estadoEscudo = EstadoEscudo.NONE;
                    escudoGrafico = null;
                } else {
                    // Durante la destrucción, cambiar entre es3 y es4
                    if (tiempoTranscurrido < DURACION_DESTRUCCION_MS / 2) {
                        cargarEscudoGrafico(R.drawable.es3);
                    } else {
                        cargarEscudoGrafico(R.drawable.es4);
                    }
                }
                break;
        }
        
        // Actualizar posición del escudo para que siga a la nave
        if (escudoGrafico != null && nave != null) {
            escudoGrafico.setCenX(nave.getCenX());
            escudoGrafico.setCenY(nave.getCenY());
        }
    }
    
    private int drawableEscudoActual = -1;
    
    private void cargarEscudoGrafico(int drawableId) {
        // Solo cargar si cambió el drawable
        if (drawableEscudoActual != drawableId) {
            Drawable drawable = AppCompatResources.getDrawable(getContext(), drawableId);
            if (drawable != null) {
                // Reducir el tamaño del escudo para que solo cubra ligeramente al jugador
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                    // Reducir a 35% del tamaño original para que solo tape ligeramente al jugador
                    int nuevoAncho = (int) (bmp.getWidth() * 0.35);
                    int nuevoAlto = (int) (bmp.getHeight() * 0.35);
                    Bitmap reducido = Bitmap.createScaledBitmap(bmp, nuevoAncho, nuevoAlto, true);
                    escudoGrafico = new Grafico(this, new BitmapDrawable(getResources(), reducido));
                } else {
                    escudoGrafico = new Grafico(this, drawable);
                }
                drawableEscudoActual = drawableId;
                if (nave != null) {
                    escudoGrafico.setCenX(nave.getCenX());
                    escudoGrafico.setCenY(nave.getCenY());
                }
            }
        }
    }
    
    private void iniciarAparicionEscudo() {
        estadoEscudo = EstadoEscudo.APARICIENDO;
        tiempoEstadoEscudo = System.currentTimeMillis();
        drawableEscudoActual = -1; // Forzar recarga
        cargarEscudoGrafico(R.drawable.es1);
        reproducirSonido(sonidoEscudoAparece);
    }
    
    private void iniciarDestruccionEscudo() {
        if (estadoEscudo == EstadoEscudo.ACTIVO || estadoEscudo == EstadoEscudo.APARICIENDO) {
            estadoEscudo = EstadoEscudo.DESTRUYENDO;
            tiempoEstadoEscudo = System.currentTimeMillis();
            cargarEscudoGrafico(R.drawable.es3);
            reproducirSonido(sonidoEscudoExplota);
        }
    }
    
    private void dibujarEscudo(Canvas canvas) {
        if (estadoEscudo == EstadoEscudo.NONE || escudoGrafico == null) {
            return;
        }
        
        // Verificar que la nave exista según el modo de gráficos
        if (modoGraficos == 2) {
            if (naveVector == null) return;
            // Dibujar el escudo centrado en la nave vectorial
            escudoGrafico.setCenX(naveVector.getCenX());
            escudoGrafico.setCenY(naveVector.getCenY());
        } else {
            if (nave == null) return;
            // Dibujar el escudo centrado en la nave
            escudoGrafico.setCenX(nave.getCenX());
            escudoGrafico.setCenY(nave.getCenY());
        }
        
        // Aplicar efecto de parpadeo suave durante la aparición
        if (estadoEscudo == EstadoEscudo.APARICIENDO) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoEstadoEscudo;
            float alpha = Math.min(1.0f, tiempoTranscurrido / (float)DURACION_APARICION_MS);
            canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int)(alpha * 255), Canvas.ALL_SAVE_FLAG);
            escudoGrafico.dibujaGrafico(canvas);
            canvas.restore();
        } else if (estadoEscudo == EstadoEscudo.DESTRUYENDO) {
            // Efecto de desvanecimiento durante la destrucción
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoEstadoEscudo;
            float alpha = 1.0f - (tiempoTranscurrido / (float)DURACION_DESTRUCCION_MS);
            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int)(alpha * 255), Canvas.ALL_SAVE_FLAG);
            escudoGrafico.dibujaGrafico(canvas);
            canvas.restore();
        } else {
            // Estado activo: dibujar normalmente
            escudoGrafico.dibujaGrafico(canvas);
        }
    }
    
    // ====== MÉTODOS DE ORBES DE POWER-UP ======
    private void actualizarOrbesPowerUp() {
        List<PowerUpOrb> toRemove = new ArrayList<>();
        for (PowerUpOrb orb : orbesPowerUp) {
            if (orb.activa) {
                orb.update();
            } else {
                toRemove.add(orb);
            }
        }
        orbesPowerUp.removeAll(toRemove);
    }
    
    private void verificarColisionOrbes() {
        if (juegoTerminado || pausado) return;
        if (modoGraficos == 2 && naveVector == null) return;
        if (modoGraficos != 2 && nave == null) return;
        
        List<PowerUpOrb> toRemove = new ArrayList<>();
        for (PowerUpOrb orb : orbesPowerUp) {
            boolean colision = false;
            if (modoGraficos == 2) {
                // MODO VECTOR PURO: verificar colisión con naveVector
                double distancia = Math.hypot(naveVector.getCenX() - orb.x, naveVector.getCenY() - orb.y);
                colision = distancia < (naveVector.getRadioColision() + orb.grafico.getRadioColision());
            } else {
                // MODO NORMAL: verificar colisión con nave
                colision = nave.verificaColision(orb.grafico);
            }
            
            if (colision) {
                // Activar power-up
                powerUpActivo = orb.tipo;
                tiempoPowerUp = System.currentTimeMillis();
                
                // Efecto de destello
                activarDestello(orb.getColor());
                
                // Efecto visual/sonoro
                vibrar(100);
                crearParticulasEscudo(orb.x, orb.y);
                
                // Si es escudo, iniciar animación de aparición
                if (orb.tipo == PowerUpType.ESCUDO) {
                    iniciarAparicionEscudo();
                }
                
                toRemove.add(orb);
            }
        }
        orbesPowerUp.removeAll(toRemove);
    }
    
    private void dibujarOrbesPowerUp(Canvas canvas) {
        for (PowerUpOrb orb : orbesPowerUp) {
            // Dibujar haz de luz parpadeante
            dibujarHazOrbe(canvas, orb);
            
            // Dibujar el orbe
            orb.grafico.dibujaGrafico(canvas);
        }
    }
    
    private void dibujarHazOrbe(Canvas canvas, PowerUpOrb orb) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        
        int color = orb.getColor();
        float alpha = orb.pulseAlpha;
        // Haz más pequeño, solo alrededor del orbe (1.2x el tamaño del orbe)
        float radius = orb.grafico.getAncho() * 1.2f;
        
        // Crear gradiente radial para el haz (más compacto)
        RadialGradient gradient = new RadialGradient(
                orb.x, orb.y, radius,
                Color.argb((int)(alpha * 120), Color.red(color), Color.green(color), Color.blue(color)),
                Color.argb(0, Color.red(color), Color.green(color), Color.blue(color)),
                Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        
        canvas.drawCircle(orb.x, orb.y, radius, paint);
    }
    
    // ====== MÉTODOS DE DESTELLO ======
    private void activarDestello(int color) {
        flashColor = color;
        flashAlpha = 1.0f;
        flashStartTime = System.currentTimeMillis();
    }
    
    private void actualizarDestello() {
        if (flashAlpha > 0) {
            long tiempoTranscurrido = System.currentTimeMillis() - flashStartTime;
            if (tiempoTranscurrido > FLASH_DURATION_MS) {
                flashAlpha = 0;
            } else {
                // Fade out rápido
                flashAlpha = 1.0f - (tiempoTranscurrido / (float) FLASH_DURATION_MS);
            }
        }
    }
    
    private void dibujarDestello(Canvas canvas) {
        if (flashAlpha > 0) {
            Paint paint = new Paint();
            paint.setColor(Color.argb((int)(flashAlpha * 200), Color.red(flashColor), Color.green(flashColor), Color.blue(flashColor)));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }
    
    private void actualizarExplosiones() {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion exp : explosiones) {
            exp.update();
            if (!exp.activa) {
                toRemove.add(exp);
            }
        }
        explosiones.removeAll(toRemove);
    }
    
    private void dibujarExplosiones(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        
        for (Explosion exp : explosiones) {
            paint.setAlpha((int) (exp.alpha * 255));
            paint.setColor(Color.argb((int) (exp.alpha * 255), 255, 200, 0));
            canvas.drawCircle(exp.x, exp.y, exp.radio, paint);
            
            // Círculos concéntricos para efecto de explosión
            paint.setColor(Color.argb((int) (exp.alpha * 200), 255, 100, 0));
            canvas.drawCircle(exp.x, exp.y, exp.radio * 0.7f, paint);
            paint.setColor(Color.argb((int) (exp.alpha * 150), 255, 50, 0));
            canvas.drawCircle(exp.x, exp.y, exp.radio * 0.4f, paint);
        }
    }
    
    private void vibrar(long duracion) {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duracion);
            }
        }
    }
    
    private void reproducirSonido(MediaPlayer player) {
        if (audioMuteado) return; // No reproducir si está silenciado
        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.seekTo(0);
                } else {
                    player.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para reproducir sonido de explosión usando SoundPool (permite múltiples reproducciones simultáneas)
    private void reproducirSonidoExplosion() {
        if (audioMuteado) return; // No reproducir si está silenciado
        try {
            if (soundPool != null && soundIdExplosion != 0) {
                // Reproducir el sonido con volumen 0.7 (ambos canales) y prioridad 1 (normal)
                // PRIORITY: 1 (normal), LOOP: 0 (no loop), RATE: 1.0f (velocidad normal)
                soundPool.play(soundIdExplosion, 0.7f, 0.7f, 1, 0, 1.0f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para controlar el mute del audio
    public void setAudioMuteado(boolean muteado) {
        audioMuteado = muteado;
        // Si se silencia, detener completamente todos los sonidos
        if (muteado) {
            if (sonidoDisparo != null) {
                try {
                    if (sonidoDisparo.isPlaying()) {
                        sonidoDisparo.stop();
                    }
                    sonidoDisparo.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (sonidoEscudoAparece != null) {
                try {
                    if (sonidoEscudoAparece.isPlaying()) {
                        sonidoEscudoAparece.stop();
                    }
                    sonidoEscudoAparece.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (sonidoEscudoExplota != null) {
                try {
                    if (sonidoEscudoExplota.isPlaying()) {
                        sonidoEscudoExplota.stop();
                    }
                    sonidoEscudoExplota.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (sonidoMisionCompletada != null) {
                try {
                    if (sonidoMisionCompletada.isPlaying()) {
                        sonidoMisionCompletada.stop();
                    }
                    sonidoMisionCompletada.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (sonidoNivel1 != null) {
                try {
                    if (sonidoNivel1.isPlaying()) {
                        sonidoNivel1.stop();
                    }
                    sonidoNivel1.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Si se activa el audio, intentar reproducir la música del juego si está iniciado
            if (juegoIniciado && !pausado) {
                reproducirMusicaJuego();
            }
        }
    }
    
    public boolean isAudioMuteado() {
        return audioMuteado;
    }
    
    // Método para reproducir la música del juego
    // SOLO se reproduce cuando el juego está activo y en el nivel
    private void reproducirMusicaJuego() {
        // Verificar condiciones: no silenciado, juego iniciado, no pausado, no terminado
        if (audioMuteado || !juegoIniciado || pausado || juegoTerminado) {
            // Si no se cumplen las condiciones, detener la música
            detenerMusicaInmediatamente();
            return;
        }
        
        // Asegurar que los sonidos estén inicializados
        if (sonidoNivel1 == null && getContext() != null) {
            inicializarSonidos();
        }
        
        if (sonidoNivel1 != null) {
            try {
                // Solo reproducir si no está reproduciéndose y todas las condiciones se cumplen
                if (!sonidoNivel1.isPlaying() && juegoIniciado && !pausado && !juegoTerminado && !audioMuteado) {
                    sonidoNivel1.seekTo(0);
                    sonidoNivel1.start();
                } else if (sonidoNivel1.isPlaying() && (pausado || juegoTerminado || audioMuteado)) {
                    // Si está reproduciéndose pero no debería, detenerla
                    detenerMusicaInmediatamente();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Si hay error, intentar reinicializar
                try {
                    if (getContext() != null) {
                        sonidoNivel1 = MediaPlayer.create(getContext(), R.raw.l1);
                        if (sonidoNivel1 != null) {
                            sonidoNivel1.setVolume(1.0f, 1.0f);
                            sonidoNivel1.setLooping(true);
                            // Solo iniciar si todas las condiciones se cumplen
                            if (juegoIniciado && !pausado && !juegoTerminado && !audioMuteado) {
                                sonidoNivel1.start();
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    private void inicializarSonidos() {
        try {
            Context context = getContext();
            if (context != null) {
                // Inicializar sonido de disparo (laser3)
                sonidoDisparo = MediaPlayer.create(context, R.raw.laser3);
                if (sonidoDisparo != null) {
                    sonidoDisparo.setVolume(0.8f, 0.8f);
                }
                
                // Inicializar SoundPool para sonidos de explosión (permite múltiples reproducciones simultáneas)
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(10) // Permitir hasta 10 explosiones simultáneas
                        .build();
                
                // Cargar el sonido de explosión (la carga es asíncrona, pero SoundPool lo maneja automáticamente)
                soundIdExplosion = soundPool.load(context, R.raw.explosion, 1);
                
                // Listener para verificar cuando el sonido está cargado (opcional, pero útil para debugging)
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (status == 0 && sampleId == soundIdExplosion) {
                            // Sonido cargado correctamente
                            // No necesitamos hacer nada aquí, el SoundPool ya está listo
                        }
                    }
                });
                
                sonidoEscudoAparece = MediaPlayer.create(context, R.raw.burbujaap);
                if (sonidoEscudoAparece != null) {
                    sonidoEscudoAparece.setVolume(0.7f, 0.7f);
                }
                
                sonidoEscudoExplota = MediaPlayer.create(context, R.raw.burubujaex);
                if (sonidoEscudoExplota != null) {
                    sonidoEscudoExplota.setVolume(0.7f, 0.7f);
                }
                
                // Inicializar sonido de misión completada
                sonidoMisionCompletada = MediaPlayer.create(context, R.raw.mc);
                if (sonidoMisionCompletada != null) {
                    sonidoMisionCompletada.setVolume(1.0f, 1.0f);
                    sonidoMisionCompletada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            audioMisionCompletado = true;
                        }
                    });
                }
                
                // Inicializar sonido del nivel 1
                sonidoNivel1 = MediaPlayer.create(context, R.raw.l1);
                if (sonidoNivel1 != null) {
                    sonidoNivel1.setVolume(1.0f, 1.0f);
                    sonidoNivel1.setLooping(true); // Reproducir en loop continuo
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void liberarSonidos() {
        if (sonidoDisparo != null) {
            try {
                if (sonidoDisparo.isPlaying()) {
                    sonidoDisparo.stop();
                }
                sonidoDisparo.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sonidoDisparo = null;
        }
        
        if (soundPool != null) {
            try {
                if (soundIdExplosion != 0) {
                    soundPool.unload(soundIdExplosion);
                }
                soundPool.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            soundPool = null;
            soundIdExplosion = 0;
        }
        
        if (sonidoEscudoAparece != null) {
            try {
                if (sonidoEscudoAparece.isPlaying()) {
                    sonidoEscudoAparece.stop();
                }
                sonidoEscudoAparece.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sonidoEscudoAparece = null;
        }
        
        if (sonidoEscudoExplota != null) {
            try {
                if (sonidoEscudoExplota.isPlaying()) {
                    sonidoEscudoExplota.stop();
                }
                sonidoEscudoExplota.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sonidoEscudoExplota = null;
        }
        
        if (sonidoMisionCompletada != null) {
            try {
                if (sonidoMisionCompletada.isPlaying()) {
                    sonidoMisionCompletada.stop();
                }
                sonidoMisionCompletada.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sonidoMisionCompletada = null;
        }
        
        if (sonidoNivel1 != null) {
            try {
                if (sonidoNivel1.isPlaying()) {
                    sonidoNivel1.stop();
                }
                sonidoNivel1.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sonidoNivel1 = null;
        }
    }
    
    // ====== MÉTODOS DE FINALIZACIÓN DE MISIÓN ======
    
    private void iniciarMisionCompletada() {
        estadoMision = EstadoMision.MISION_COMPLETADA;
        nivelCompletado = nivel;
        tiempoMisionCompletada = System.currentTimeMillis();
        audioMisionCompletado = false;
        
        // Detener sonido del nivel 1 si está reproduciéndose (para cualquier nivel que termine)
        if (sonidoNivel1 != null) {
            try {
                if (sonidoNivel1.isPlaying()) {
                    sonidoNivel1.stop();
                }
                // Asegurar que el MediaPlayer esté completamente detenido
                sonidoNivel1.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Guardar vidas al inicio del nivel (si no se ha guardado aún)
        if (vidasInicialesNivel == 3 && nivel == 1) {
            vidasInicialesNivel = vidas;
        }
        
        // Calcular bonuses
        calcularBonuses();
        
        // Reproducir sonido de misión completada
        if (sonidoMisionCompletada != null && !audioMuteado) {
            try {
                sonidoMisionCompletada.seekTo(0);
                sonidoMisionCompletada.start();
            } catch (Exception e) {
                e.printStackTrace();
                // Si hay error, continuar de todas formas
                audioMisionCompletado = true;
            }
        } else {
            // Si no hay audio, continuar inmediatamente
            audioMisionCompletado = true;
        }
    }
    
    private void calcularBonuses() {
        // Bonus por completar nivel (base)
        bonusClear = 1000 * nivelCompletado;
        
        // Bonus por tiempo (segundos transcurridos desde inicio del nivel)
        // Si tiempoInicioNivel es 0, usar tiempoInicio como fallback
        long tiempoBase = (tiempoInicioNivel > 0) ? tiempoInicioNivel : tiempoInicio;
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoBase;
        long segundos = tiempoTranscurrido / 1000;
        
        if (segundos < 30) {
            bonusTiempo = 500;
        } else if (segundos <= 60) {
            bonusTiempo = 250;
        } else {
            bonusTiempo = 100;
        }
        
        // Bonus por vidas restantes
        bonusVidas = vidas * 200;
        
        // Bonus por enemigos derrotados (5 puntos por asteroide)
        bonusEnemigos = enemigosDerrotados * 5;
        
        // Inicializar valores animados
        bonusClearActual = 0;
        bonusTiempoActual = 0;
        bonusVidasActual = 0;
        bonusEnemigosActual = 0;
        puntajeTotalAnimado = puntaje; // Empezar desde el puntaje actual
        indiceBonusMostrando = 0;
        bonusCompletado = false;
    }
    
    private void actualizarEstadoMision() {
        if (estadoMision == EstadoMision.MISION_COMPLETADA) {
            // Esperar a que termine el audio
            if (audioMisionCompletado) {
                // Transición a pantalla de bonus
                estadoMision = EstadoMision.PANTALLA_BONUS;
                tiempoInicioBonus = System.currentTimeMillis();
                ultimaActualizacionBonus = System.currentTimeMillis();
                
                // Asegurar que la música del nivel 1 esté completamente detenida cuando se muestran las puntuaciones
                if (sonidoNivel1 != null) {
                    try {
                        if (sonidoNivel1.isPlaying()) {
                            sonidoNivel1.stop();
                        }
                        sonidoNivel1.reset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (estadoMision == EstadoMision.PANTALLA_BONUS) {
            // Animar los bonuses
            actualizarAnimacionBonus();
            
            // Si todos los bonuses se han mostrado, avanzar al siguiente nivel después de un delay
            if (bonusCompletado) {
                long tiempoDesdeCompletado = System.currentTimeMillis() - tiempoInicioBonus;
                if (tiempoDesdeCompletado > 3000) { // Esperar 3 segundos después de completar
                    avanzarSiguienteNivel();
                }
            }
        } else if (estadoMision == EstadoMision.TRANSICION) {
            // En estado de transición, solo esperar a que el countdown se active
            // El countdown cambiará el estado a NORMAL cuando esté listo
            // No hacer nada aquí, solo mantener el estado hasta que el countdown se active
            // Si el countdown no se activa después de un tiempo razonable, cambiar a NORMAL directamente
            // (esto es un fallback de seguridad)
            long tiempoTransicion = System.currentTimeMillis() - tiempoInicioNivel;
            if (tiempoTransicion > 5000) { // Si han pasado más de 5 segundos, continuar sin countdown
                estadoMision = EstadoMision.NORMAL;
                pausado = false;
                juegoIniciado = true;
            }
        }
    }
    
    private void actualizarAnimacionBonus() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimaActualizacionBonus < INTERVALO_ANIMACION_BONUS_MS) {
            return;
        }
        ultimaActualizacionBonus = ahora;
        
        int incremento = 50; // Incremento por frame para animación rápida
        
        switch (indiceBonusMostrando) {
            case 0: // Clear bonus
                if (bonusClearActual < bonusClear) {
                    bonusClearActual = Math.min(bonusClearActual + incremento, bonusClear);
                } else {
                    indiceBonusMostrando = 1;
                }
                break;
            case 1: // Time bonus
                if (bonusTiempoActual < bonusTiempo) {
                    bonusTiempoActual = Math.min(bonusTiempoActual + incremento, bonusTiempo);
                } else {
                    indiceBonusMostrando = 2;
                }
                break;
            case 2: // Lives bonus
                if (bonusVidasActual < bonusVidas) {
                    bonusVidasActual = Math.min(bonusVidasActual + incremento, bonusVidas);
                } else {
                    indiceBonusMostrando = 3;
                }
                break;
            case 3: // Enemies bonus
                if (bonusEnemigosActual < bonusEnemigos) {
                    bonusEnemigosActual = Math.min(bonusEnemigosActual + incremento, bonusEnemigos);
                } else {
                    indiceBonusMostrando = 4;
                }
                break;
            case 4: // Total score
                // El puntaje total debe ser: puntaje base (sin bonuses del nivel) + todos los bonuses
                // puntajeTotalAnimado ya empezó en 'puntaje', así que necesitamos calcular el objetivo correcto
                int puntajeBaseInicial = puntaje; // Este es el puntaje antes de los bonuses del nivel
                int totalBonus = bonusClear + bonusTiempo + bonusVidas + bonusEnemigos;
                int puntajeFinal = puntajeBaseInicial + totalBonus;
                
                // Animamos desde donde estamos (que debería ser puntajeBaseInicial) hasta el puntaje final
                // Asegurarnos de que empezamos desde el puntaje base
                if (puntajeTotalAnimado < puntajeBaseInicial) {
                    puntajeTotalAnimado = puntajeBaseInicial;
                }
                
                if (puntajeTotalAnimado < puntajeFinal) {
                    int diferencia = puntajeFinal - puntajeTotalAnimado;
                    int incrementoTotal = Math.min(incremento * 3, diferencia); // Más rápido para el total
                    puntajeTotalAnimado += incrementoTotal;
                } else {
                    // Todos los bonuses completados
                    puntajeTotalAnimado = puntajeFinal;
                    puntaje = puntajeFinal; // Actualizar puntaje real
                    bonusCompletado = true;
                }
                break;
        }
    }
    
    private void avanzarSiguienteNivel() {
        // Incrementar nivel primero
        nivel++;
        
        // Cambiar estado a TRANSICION para indicar que estamos preparando el siguiente nivel
        // El countdown cambiará el estado a NORMAL cuando esté listo
        estadoMision = EstadoMision.TRANSICION;
        // NO establecer mostrandoNivel = true aquí, el countdown se encargará de eso
        // mostrandoNivel = true; // Comentado: el countdown manejará la pausa
        tiempoMostrarNivel = System.currentTimeMillis();
        tiempoInicioNivel = System.currentTimeMillis();
        vidasInicialesNivel = vidas;
        enemigosDerrotados = 0;
        
        // Resetear flags de bonus
        bonusCompletado = false;
        indiceBonusMostrando = 0;
        
        // Resetear estados de controles para evitar que queden bloqueados
        teclaArriba = false;
        teclaIzquierda = false;
        teclaDerecha = false;
        velocidadJoystick = 0;
        direccionJoystick = 0;
        aceleracionNave = 0;
        giroNave = 0;
        joystickActive = false;
        joystickThumbX = joystickCenterX;
        joystickThumbY = joystickCenterY;
        
        // Asegurar que no estemos en cooldown
        enCooldown = false;
        
        // Asegurar que no estemos en estado inmune
        inmune = false;
        
        // Resetear power-ups
        powerUpActivo = null;
        tiempoPowerUp = 0;
        estadoEscudo = EstadoEscudo.NONE;
        escudoGrafico = null;
        drawableEscudoActual = -1;
        
        // Reposicionar la nave en el centro antes de generar asteroides
        int ancho = getWidth();
        int alto = getHeight();
        if (ancho > 0 && alto > 0 && getContext() != null) {
            if (modoGraficos == 2) {
                // MODO VECTOR PURO
                if (naveVector == null) {
                    // Crear nave vectorial si no existe
                    naveVector = new VectorGrafico(this, VectorGrafico.TipoVector.NAVE, ancho / 2, alto / 2);
                    naveVector.setIncX(0);
                    naveVector.setIncY(0);
                    naveVector.setAngulo(0);
                } else {
                    naveVector.setCenX(ancho / 2);
                    naveVector.setCenY(alto / 2);
                    naveVector.setIncX(0);
                    naveVector.setIncY(0);
                }
            } else {
                // MODO NORMAL O VECTOR IMAGEN
                if (nave == null) {
                    // Crear la nave si no existe
                    try {
                        SharedPreferences pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                        int modoGraficos = pref.getInt("graficos_mode", 0);
                        Drawable drawableNave = AppCompatResources.getDrawable(getContext(),
                                modoGraficos == 0 ? R.drawable.navevector : R.drawable.nave1);
                        if (drawableNave != null) {
                            if (drawableNave instanceof BitmapDrawable) {
                                Bitmap bmp = ((BitmapDrawable) drawableNave).getBitmap();
                                if (bmp != null) {
                                    int nuevoAncho = (int) (bmp.getWidth() * 0.3);
                                    int nuevoAlto = (int) (bmp.getHeight() * 0.3);
                                    Bitmap reducido = Bitmap.createScaledBitmap(bmp, nuevoAncho, nuevoAlto, true);
                                    nave = new Grafico(this, new BitmapDrawable(getResources(), reducido));
                                    if (nave != null) {
                                        nave.setRadioColision(nave.getAncho() / 3);
                                    }
                                }
                            } else {
                                nave = new Grafico(this, drawableNave);
                                if (nave != null) {
                                    nave.setRadioColision(nave.getAncho() / 3);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                if (nave != null) {
                    nave.setCenX(ancho / 2);
                    nave.setCenY(alto / 2);
                    nave.setIncX(0);
                    nave.setIncY(0);
                }
            }
        }
        
        // Limpiar misiles y explosiones del nivel anterior
        if (modoGraficos == 2) {
            if (misilesVector != null) {
                misilesVector.clear();
            }
        } else {
            if (misiles != null) {
                synchronized (misiles) {
                    misiles.clear();
                }
            }
        }
        if (explosiones != null) {
            explosiones.clear();
        }
        if (particulas != null) {
            particulas.clear();
        }
        if (orbesPowerUp != null) {
            orbesPowerUp.clear();
        }
        
        // Asegurar que el juego esté iniciado
        juegoIniciado = true;
        
        // Guardar estado del juego
        guardarEstadoJuego();
        
        // Reiniciar audio para próxima vez
        if (sonidoMisionCompletada != null) {
            try {
                sonidoMisionCompletada.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        audioMisionCompletado = false;
        
        // Generar nuevos asteroides para el nuevo nivel (solo si las dimensiones están listas)
        // Si las dimensiones no están listas, se generarán cuando se llame a onSizeChanged
        if (ancho > 0 && alto > 0 && getContext() != null) {
            try {
                generarAsteroidesNivel();
                // Verificar que los asteroides se generaron correctamente
                boolean asteroidesGenerados = false;
                if (modoGraficos == 2) {
                    asteroidesGenerados = (asteroidesVector != null && !asteroidesVector.isEmpty());
                } else {
                    synchronized (asteroides) {
                        asteroidesGenerados = (asteroides != null && !asteroides.isEmpty());
                    }
                }
                
                if (!asteroidesGenerados) {
                    // Si no se generaron asteroides, intentar de nuevo después de un momento
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getWidth() > 0 && getHeight() > 0 && getContext() != null) {
                                try {
                                    generarAsteroidesNivel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Si hay error, intentar más tarde
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getWidth() > 0 && getHeight() > 0 && getContext() != null) {
                            try {
                                generarAsteroidesNivel();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }, 100);
            }
        }
        
        // Asegurar que el thread esté ejecutándose antes de solicitar countdown
        if (!isThreadAlive()) {
            iniciarThreadJuego();
        }
        
        // Forzar invalidación para asegurar que se redibuje (mostrará pantalla negra durante TRANSICION)
        postInvalidate();
        
        // Solicitar countdown para el nuevo nivel (esto cambiará el estado a NORMAL y mostrará el countdown)
        // Hacer esto inmediatamente para que el countdown se muestre rápidamente
        if (countdownListener != null) {
            // Llamar directamente al countdown en lugar de usar post para asegurar que se ejecute
            post(new Runnable() {
                @Override
                public void run() {
                    solicitarCountdownNivel();
                }
            });
        } else {
            // Si no hay listener, cambiar a NORMAL directamente y continuar
            estadoMision = EstadoMision.NORMAL;
            pausado = false;
            juegoIniciado = true;
            guardarEstadoJuego();
        }
    }
    
    // Interfaz para callback de countdown
    public interface OnCountdownRequestListener {
        void onCountdownRequested();
    }
    
    private OnCountdownRequestListener countdownListener;
    
    public void setOnCountdownRequestListener(OnCountdownRequestListener listener) {
        this.countdownListener = listener;
    }
    
    // Método público para establecer el estado de misión a NORMAL (llamado desde Juego.java)
    public void setEstadoMisionNormal() {
        estadoMision = EstadoMision.NORMAL;
    }
    
    private void solicitarCountdownNivel() {
        // Solicitar countdown al Activity si hay un listener
        if (countdownListener != null) {
            countdownListener.onCountdownRequested();
        }
    }
    
    // Método para guardar el estado del juego
    public void guardarEstadoJuego() {
        try {
            if (getContext() == null) return;
            SharedPreferences gameState = getContext().getSharedPreferences("game_state", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = gameState.edit();
            // Solo marcar como en curso si el juego está iniciado y no terminado
            editor.putBoolean("juego_en_curso", juegoIniciado && !juegoTerminado);
            editor.putInt("nivel", nivel);
            editor.putInt("puntaje", puntaje);
            editor.putInt("vidas", vidas);
            editor.putLong("tiempo_inicio_nivel", tiempoInicioNivel);
            editor.putInt("vidas_iniciales_nivel", vidasInicialesNivel);
            editor.putInt("enemigos_derrotados", enemigosDerrotados);
            // Guardar estado de misión también
            editor.putString("estado_mision", estadoMision.name());
            editor.putInt("nivel_completado", nivelCompletado);
            // Usar commit() en lugar de apply() para asegurar que se guarde inmediatamente
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Método para limpiar el estado del juego (cuando termina)
    public void limpiarEstadoJuego() {
        try {
            // Detener música inmediatamente antes de limpiar el estado
            detenerMusicaInmediatamente();
            
            if (sonidoMisionCompletada != null) {
                try {
                    if (sonidoMisionCompletada.isPlaying()) {
                        sonidoMisionCompletada.stop();
                    }
                    sonidoMisionCompletada.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (getContext() == null) return;
            SharedPreferences gameState = getContext().getSharedPreferences("game_state", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = gameState.edit();
            editor.putBoolean("juego_en_curso", false);
            // Limpiar todos los valores del estado
            editor.remove("nivel");
            editor.remove("puntaje");
            editor.remove("vidas");
            editor.remove("tiempo_inicio_nivel");
            editor.remove("vidas_iniciales_nivel");
            editor.remove("enemigos_derrotados");
            editor.remove("estado_mision");
            editor.remove("nivel_completado");
            // Usar commit() para asegurar que se guarde inmediatamente
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ====== MÉTODOS DE PARTÍCULAS ======
    private void crearParticulasExplosion(float x, float y) {
        for (int i = 0; i < 20; i++) {
            float angle = (float) (Math.random() * 360);
            float speed = 2f + (float) (Math.random() * 4f);
            float vx = (float) (Math.cos(Math.toRadians(angle)) * speed);
            float vy = (float) (Math.sin(Math.toRadians(angle)) * speed);
            int color = Color.argb(255, 255, (int)(150 + Math.random() * 105), 0);
            particulas.add(new Particle(x, y, vx, vy, color, 3f + (float)(Math.random() * 3f), 1.0f));
        }
    }
    
    private void crearParticulasEscudo(float x, float y) {
        for (int i = 0; i < 15; i++) {
            float angle = (float) (Math.random() * 360);
            float speed = 1f + (float) (Math.random() * 2f);
            float vx = (float) (Math.cos(Math.toRadians(angle)) * speed);
            float vy = (float) (Math.sin(Math.toRadians(angle)) * speed);
            int color = Color.argb(255, 100, 200, 255);
            particulas.add(new Particle(x, y, vx, vy, color, 2f + (float)(Math.random() * 2f), 0.8f));
        }
    }
    
    private void actualizarParticulas() {
        List<Particle> toRemove = new ArrayList<>();
        for (Particle p : particulas) {
            p.update();
            if (!p.activa) {
                toRemove.add(p);
            }
        }
        particulas.removeAll(toRemove);
    }
    
    private void dibujarParticulas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        
        for (Particle p : particulas) {
            float alpha = p.life / p.maxLife;
            paint.setColor(Color.argb((int)(alpha * 255), 
                    Color.red(p.color), Color.green(p.color), Color.blue(p.color)));
            canvas.drawCircle(p.x, p.y, p.size * alpha, paint);
        }
    }
    
    // ====== MÉTODOS DE ESTELAS ======
    private void actualizarEstelaNave() {
        if (nave != null && !juegoTerminado && !pausado) {
            // Agregar punto a la estela si la nave se está moviendo
            if (Math.hypot(nave.getIncX(), nave.getIncY()) > 0.5) {
                estelaNave.add(new TrailPoint(nave.getCenX(), nave.getCenY()));
                if (estelaNave.size() > MAX_TRAIL_POINTS) {
                    estelaNave.remove(0);
                }
            }
        }
        
        // Actualizar puntos de la estela
        List<TrailPoint> toRemove = new ArrayList<>();
        for (TrailPoint tp : estelaNave) {
            tp.update();
            if (tp.alpha <= 0) {
                toRemove.add(tp);
            }
        }
        estelaNave.removeAll(toRemove);
    }
    
    private void dibujarEstelaNave(Canvas canvas) {
        if (estelaNave.isEmpty()) return;
        
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(150, 100, 150, 255));
        
        for (TrailPoint tp : estelaNave) {
            paint.setAlpha((int)(tp.alpha * 150));
            canvas.drawCircle(tp.x, tp.y, tp.size, paint);
        }
    }
    
    private void dibujarEstelasMisiles(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        
        for (Grafico m : misiles) {
            // Dibujar estela corta detrás del misil
            double angle = m.getAngulo() + 180;
            float trailLength = 20f;
            float endX = (float)(m.getCenX() + Math.cos(Math.toRadians(angle)) * trailLength);
            float endY = (float)(m.getCenY() + Math.sin(Math.toRadians(angle)) * trailLength);
            
            paint.setColor(Color.argb(200, 255, 255, 255));
            canvas.drawLine(m.getCenX(), m.getCenY(), endX, endY, paint);
        }
    }
    
    // ====== MÉTODOS DE INMUNIDAD ======
    private void dibujarAuraInmunidad(Canvas canvas, float x, float y) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        
        // Aura pulsante azul/cyan
        float alpha = alphaInmunidad;
        paint.setColor(Color.argb((int)(alpha * 150), 100, 200, 255));
        float radius = 40f + (float)(Math.sin(System.currentTimeMillis() / 100.0) * 5);
        canvas.drawCircle(x, y, radius, paint);
        
        paint.setColor(Color.argb((int)(alpha * 100), 150, 220, 255));
        canvas.drawCircle(x, y, radius * 1.3f, paint);
    }
    
    // ====== MÉTODOS DE JOYSTICK VISIBLE ======
    private void dibujarJoystick(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Dibujar base del joystick (círculo exterior semi-transparente)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(150, 100, 100, 100));
        canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paint);
        
        // Dibujar borde del joystick
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(200, 200, 200, 200));
        canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paint);
        
        // Dibujar thumbstick (círculo interior)
        paint.setStyle(Paint.Style.FILL);
        if (joystickActive) {
            paint.setColor(Color.argb(220, 255, 255, 255));
        } else {
            paint.setColor(Color.argb(180, 200, 200, 200));
        }
        canvas.drawCircle(joystickThumbX, joystickThumbY, joystickThumbRadius, paint);
        
        // Dibujar borde del thumbstick
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawCircle(joystickThumbX, joystickThumbY, joystickThumbRadius, paint);
    }
    
    private void dibujarBotonDisparo(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Dibujar base del botón de disparo
        paint.setStyle(Paint.Style.FILL);
        if (botonDisparoPresionado) {
            paint.setColor(Color.argb(220, 255, 100, 100));
        } else {
            paint.setColor(Color.argb(180, 200, 50, 50));
        }
        canvas.drawCircle(botonDisparoX, botonDisparoY, botonDisparoRadius, paint);
        
        // Dibujar borde del botón
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawCircle(botonDisparoX, botonDisparoY, botonDisparoRadius, paint);
        
        // Dibujar símbolo de disparo (círculo pequeño en el centro)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawCircle(botonDisparoX, botonDisparoY, botonDisparoRadius * 0.3f, paint);
    }
    
    // ====== MÉTODOS DE ESTADÍSTICAS ======
    private void dibujarEstadisticas(Canvas canvas) {
        if (juegoTerminado) return;
        
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(3, 2, 2, Color.BLACK);
        aplicarFuentePixelArt(paint);
        
        int yPos = getHeight() - 100;
        int xPos = 20;
        
        // Tiempo de juego
        long segundos = tiempoJuego / 1000;
        long minutos = segundos / 60;
        segundos = segundos % 60;
        canvas.drawText("Tiempo: " + String.format("%02d:%02d", minutos, segundos), xPos, yPos, paint);
        
        // Asteroides destruidos
        yPos += 30;
        canvas.drawText("Destruidos: " + asteroidesDestruidos, xPos, yPos, paint);
        
        // Precisión
        yPos += 30;
        float precision = disparosRealizados > 0 ? (disparosAcertados * 100f / disparosRealizados) : 0;
        canvas.drawText("Precisión: " + String.format("%.1f%%", precision), xPos, yPos, paint);
    }
    
    // ====== MÉTODOS DE GUARDADO DE PROGRESO ======
    private void guardarProgreso() {
        SharedPreferences prefs = getContext().getSharedPreferences("game_progress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("nivel", nivel);
        editor.putInt("puntaje", puntaje);
        editor.putInt("vidas", vidas);
        editor.putInt("asteroides_destruidos", asteroidesDestruidos);
        editor.putLong("tiempo_juego", tiempoJuego);
        editor.putInt("disparos_realizados", disparosRealizados);
        editor.putInt("disparos_acertados", disparosAcertados);
        editor.putLong("fecha_guardado", System.currentTimeMillis());
        editor.apply();
    }
    
    public void cargarProgreso() {
        SharedPreferences prefs = getContext().getSharedPreferences("game_progress", Context.MODE_PRIVATE);
        nivel = prefs.getInt("nivel", 1);
        puntaje = prefs.getInt("puntaje", 0);
        vidas = prefs.getInt("vidas", 3);
        asteroidesDestruidos = prefs.getInt("asteroides_destruidos", 0);
        tiempoJuego = prefs.getLong("tiempo_juego", 0);
        disparosRealizados = prefs.getInt("disparos_realizados", 0);
        disparosAcertados = prefs.getInt("disparos_acertados", 0);
    }
}
