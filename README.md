# AsteroidesApp - Juego de Asteroides para Android

> 📊 **Diagramas disponibles**: Ver [ARQUITECTURA.md](ARQUITECTURA.md) para diagramas de arquitectura y [ESTRUCTURA_CARPETAS.md](ESTRUCTURA_CARPETAS.md) para la estructura completa de carpetas.

##  Descripción del Proyecto

**AsteroidesApp** es un juego de acción espacial desarrollado para Android donde controlas una nave espacial que debe destruir asteroides mientras evitas colisiones. 
El juego cuenta con gráficos vectoriales, múltiples modos de control (táctil, teclado y sensores de movimiento), sistema de puntuación, niveles progresivos y efectos visuales avanzados.

### Características Principales

- **Múltiples modos de control**: Táctil (joystick), teclado y sensores de movimiento (acelerómetro y giroscopio)
-  **Tres modos gráficos**: Vector con imagen, Normal y Vector puro
-  **Sistema de niveles**: Dificultad progresiva con aumento de asteroides
-  **Base de datos de puntuaciones**: Guarda los mejores puntajes con nombres de jugadores
-  **Sistema de audio**: Música de fondo y efectos de sonido configurables
-  **Power-ups**: Escudos, disparos mejorados y velocidad aumentada
-  **Efectos visuales**: Animaciones, partículas, explosiones y fondos animados
-  **Sistema de vidas**: 3 vidas iniciales con sistema de inmunidad temporal
-  **Fragmentación de asteroides**: Los asteroides se dividen al ser destruidos

---

##  Requisitos e Instalación

### Requisitos del Sistema

- **Android SDK mínimo**: API 24 (Android 7.0 Nougat)
- **Android SDK objetivo**: API 36
- **Java**: Versión 11 o superior
- **Gradle**: Versión 8.12.1 o superior
- **Android Studio**: Versión más reciente recomendada

### Dependencias

El proyecto utiliza las siguientes librerías principales:

- `androidx.appcompat:appcompat:1.7.1`
- `com.google.android.material:material:1.12.0`
- `androidx.activity:activity:1.10.1`
- `androidx.constraintlayout:constraintlayout:2.2.1`

### Instalación

1. **Clonar o descargar el repositorio**
   ```bash
   git clone 
   cd Asteroides
   ```

2. **Abrir el proyecto en Android Studio**
   - Abre Android Studio
   - Selecciona "Open an Existing Project"
   - Navega a la carpeta `AsteroidesReal2` y selecciónala

3. **Sincronizar Gradle**
   - Android Studio debería sincronizar automáticamente
   - Si no, ve a `File > Sync Project with Gradle Files`

4. **Conectar un dispositivo o emulador**
   - Conecta un dispositivo Android físico o inicia un emulador
   - Asegúrate de que el dispositivo tenga Android 7.0 o superior

5. **Compilar y ejecutar**
   - Presiona el botón "Run" o usa `Shift + F10`
   - O ejecuta desde la terminal:
     ```bash
     ./gradlew installDebug
     ```

---

##  Cómo Usar el Juego

### Inicio del Juego

1. **Pantalla Principal**
   - Al abrir la aplicación, verás el menú principal con animaciones de fondo
   - Opciones disponibles:
     - **Jugar**: Inicia una nueva partida
     - **Configuración**: Ajusta controles, gráficos y audio
     - **Puntajes**: Ver los mejores puntajes guardados
     - **Acerca de**: Información del juego
     - **Salir**: Cierra la aplicación

2. **Iniciar Partida**
   - Presiona el botón "Jugar"
   - Aparecerá una cuenta regresiva: "¿PREPARADO?" → "¡ADELANTE!"
   - El juego comenzará automáticamente

### Controles

#### Modo Táctil (Por defecto)
- **Joystick** (esquina inferior izquierda): Mueve la nave en la dirección deseada
- **Botón de disparo** (esquina inferior derecha): Dispara misiles
- La nave se moverá automáticamente hacia la dirección del joystick

#### Modo Teclado
- **Flecha Arriba**: Acelerar hacia adelante
- **Flecha Izquierda/Derecha**: Girar la nave
- **Barra Espaciadora**: Disparar misiles

#### Modo Sensores
- **Giro del dispositivo**: Rota la nave (usa giroscopio si está disponible)
- **Inclinación hacia adelante**: Acelera la nave (usa acelerómetro)
- **Botón de disparo**: Disparar misiles

### Controles Durante el Juego

- **Botón de Salir** (esquina inferior izquierda): Regresa al menú principal
- **Botón de Pausa** (esquina inferior derecha): Pausa/reanuda el juego
- **Botón de Mute** (centro inferior): Silencia/activa el audio del juego

### Mecánicas de Juego

1. **Destruir Asteroides**
   - Dispara misiles para destruir asteroides
   - Cada asteroide destruido aumenta tu puntuación
   - Los asteroides grandes se fragmentan en piezas más pequeñas

2. **Evitar Colisiones**
   - Si chocas con un asteroide, pierdes una vida
   - Tienes 3 vidas al inicio
   - Después de una colisión, tienes inmunidad temporal (2 segundos)

3. **Power-ups**
   - Al destruir asteroides, pueden aparecer orbes de power-up:
     - **Escudo**: Protege contra una colisión
     - **Disparo Mejorado**: Aumenta el daño
     - **Velocidad**: Aumenta la velocidad de la nave

4. **Avanzar de Nivel**
   - Destruye todos los asteroides del nivel
   - Aparecerá una pantalla de bonus con puntos adicionales
   - El siguiente nivel tendrá más asteroides y mayor dificultad

5. **Game Over**
   - Cuando pierdes todas las vidas, el juego termina
   - Ingresa tu nombre para guardar tu puntuación
   - Tu puntuación se guardará en la base de datos

### Configuración

Accede a **Configuración** desde el menú principal para ajustar:

- **Modo de Gráficos**: 
  - Vector (Imagen): Gráficos vectoriales con imágenes
  - Normal: Gráficos estándar
  - Vector (Puro): Solo gráficos vectoriales sin imágenes

- **Controles**:
  - Activar/desactivar teclado
  - Activar/desactivar táctil
  - Activar/desactivar sensores

- **Audio**:
  - Activar/desactivar música de fondo

- **Fragmentos**:
  - Ajustar número de fragmentos al destruir asteroides (2-10)

---

##  Estructura del Proyecto

> 📁 **Nota**: Para una estructura detallada de todas las carpetas y archivos, consulta [ESTRUCTURA_CARPETAS.md](ESTRUCTURA_CARPETAS.md)
> 🏗️ **Arquitectura**: Para diagramas de arquitectura del sistema, consulta [ARQUITECTURA.md](ARQUITECTURA.md)

```
AsteroidesReal2/
│
├── app/
│   ├── build.gradle.kts          # Configuración de Gradle del módulo
│   ├── proguard-rules.pro        # Reglas de ProGuard
│   │
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml    # Manifiesto de la aplicación
│           │
│           ├── java/com/example/asteroidesreal/
│           │   ├── MainActivity.java           # Actividad principal (menú)
│           │   ├── Juego.java                  # Actividad del juego
│           │   ├── VistaJuego.java             # Vista principal del juego (lógica)
│           │   ├── SettingsActivity.java       # Actividad de configuración
│           │   ├── PuntajesActivity.java        # Actividad de puntajes
│           │   ├── AcercaDeActivity.java       # Actividad "Acerca de"
│           │   │
│           │   ├── Grafico.java                # Clase para gráficos normales
│           │   ├── VectorGrafico.java          # Clase para gráficos vectoriales
│           │   │
│           │   ├── Score.java                  # Modelo de puntuación
│           │   ├── ScoreDatabaseHelper.java    # Helper de base de datos SQLite
│           │   ├── ScoreAdapter.java            # Adapter para lista de puntajes
│           │   │
│           │   ├── CountdownView.java          # Vista de cuenta regresiva
│           │   ├── StarView.java               # Vista de estrellas (menú)
│           │   ├── StarViewGame.java           # Vista de estrellas (juego)
│           │   ├── GalaxyView.java             # Vista de galaxia
│           │   ├── NebulaView.java             # Vista de nebulosa
│           │   ├── AlienView.java              # Vista de aliens animados
│           │   │
│           │   ├── RainbowButton.java          # Botón con efecto arcoíris
│           │   ├── PixelArtButton.java        # Botón estilo pixel art
│           │   ├── AnimatedGalaxyButton.java   # Botón animado galaxia
│           │   │
│           │   ├── PerlinNoise.java           # Generador de ruido Perlin
│           │   └── Configuracion.java         # Clase de configuración
│           │
│           └── res/
│               ├── layout/                     # Layouts XML
│               │   ├── activity_main.xml
│               │   ├── juego.xml
│               │   ├── activity_settings.xml
│               │   ├── activity_puntajes.xml
│               │   └── ...
│               │
│               ├── drawable/                   # Imágenes y drawables
│               │   ├── asicon.png              # Icono de la aplicación
│               │   ├── nave1.png, navevector.png
│               │   ├── asteroide1.png, asteroidevector.png
│               │   └── ...
│               │
│               ├── anim/                        # Animaciones
│               │   ├── button_click.xml
│               │   ├── button_pulse_galaxy.xml
│               │   └── ...
│               │
│               ├── raw/                         # Archivos de audio
│               │   ├── pixel_galaxy.mp3        # Música del menú
│               │   ├── l1.mp3                  # Música del nivel
│               │   ├── laser3.mp3              # Sonido de disparo
│               │   ├── explosion.mp3           # Sonido de explosión
│               │   └── ...
│               │
│               ├── values/                      # Valores (strings, colors, etc.)
│               │   ├── strings.xml
│               │   ├── colors.xml
│               │   └── ...
│               │
│               └── mipmap-*/                    # Iconos de la aplicación
│
├── build.gradle.kts              # Configuración de Gradle del proyecto
├── settings.gradle.kts           # Configuración de módulos
├── gradle.properties             # Propiedades de Gradle
└── README.md                     # Este archivo
```

### Componentes Principales

#### Actividades (Activities)
- **MainActivity**: Pantalla principal con menú y animaciones
- **Juego**: Actividad principal del juego, maneja el ciclo de vida
- **SettingsActivity**: Configuración de controles, gráficos y audio
- **PuntajesActivity**: Muestra la lista de mejores puntajes
- **AcercaDeActivity**: Información sobre el juego

#### Vistas Personalizadas (Custom Views)
- **VistaJuego**: Contiene toda la lógica del juego, física, colisiones, renderizado
- **CountdownView**: Muestra la cuenta regresiva antes de iniciar
- **StarView/StarViewGame**: Fondos animados de estrellas
- **GalaxyView/NebulaView**: Fondos animados de galaxia/nebulosa
- **AlienView**: Animación de aliens en el menú

#### Modelos de Datos
- **Score**: Modelo de datos para puntuaciones
- **ScoreDatabaseHelper**: Manejo de base de datos SQLite para puntuaciones

#### Base de Datos
- **Nombre**: `scores.db`
- **Ubicación**: `/data/data/com.example.asteroidesreal/databases/scores.db`
- **Tabla**: `scores` con columnas: `_id`, `puntaje`, `fecha`, `nombre`

---

##  Características Técnicas

### Sistema de Física
- Movimiento basado en velocidad e inercia
- Fricción aplicada cuando no hay input
- Sistema de colisiones con radios de colisión
- Fragmentación de asteroides al ser destruidos

### Sistema de Audio
- Música de fondo en el menú principal
- Música durante el juego (solo en niveles activos)
- Efectos de sonido para disparos, explosiones y power-ups
- Control de mute/unmute durante el juego
- Detención automática al pausar o salir

### Sistema de Sensores
- Soporte para acelerómetro (inclinación)
- Soporte para giroscopio (rotación)
- Activación/desactivación dinámica de sensores
- Calibración automática de sensibilidad

### Sistema de Guardado
- Guardado automático del estado del juego
- Persistencia de puntuaciones en SQLite
- Recuperación de estado al rotar la pantalla
- Limpieza de estado al terminar el juego

---

##  Comandos Útiles

### Compilar el proyecto
```bash
./gradlew build
```

### Instalar en dispositivo conectado
```bash
./gradlew installDebug
```

### Limpiar el proyecto
```bash
./gradlew clean
```

### Generar APK de debug
```bash
./gradlew assembleDebug
```

### Generar APK de release
```bash
./gradlew assembleRelease
```

---

##  Notas Adicionales

### Permisos Requeridos
- `VIBRATE`: Para efectos hápticos al chocar

### Configuración de Orientación
- El juego soporta rotación de pantalla
- La orientación del juego se ajusta automáticamente
- Hay un cooldown de 3 segundos después de rotar para evitar colisiones accidentales

### Base de Datos
- La base de datos se crea automáticamente al guardar el primer puntaje
- Los puntajes se ordenan de mayor a menor
- Se guardan hasta 100 mejores puntajes

### Recursos
- Todos los recursos (imágenes, sonidos, animaciones) están en `app/src/main/res/`
- Los archivos de audio están en formato MP3 en `res/raw/`
- Las imágenes están en formato PNG en `res/drawable/`

---

## ‍Desarrollo

### Versión
- **Versión actual**: 1.0
- **Código de versión**: 1

### Tecnologías Utilizadas
- **Lenguaje**: Java 11
- **Plataforma**: Android (API 24+)
- **Base de datos**: SQLite
- **Gráficos**: Canvas API de Android
- **Audio**: MediaPlayer y SoundPool

---

##  Licencia

Este proyecto es de código abierto. Consulta el archivo de licencia para más detalles.

---

##  Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Haz un fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---





