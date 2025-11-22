# Estructura Completa de Carpetas - AsteroidesApp

## Árbol de Directorios Completo

```
AsteroidesReal2/
│
├── 📁 app/                                    # Módulo principal de la aplicación
│   ├── 📄 build.gradle.kts                   # Configuración Gradle del módulo
│   ├── 📄 proguard-rules.pro                 # Reglas de ProGuard para ofuscación
│   │
│   ├── 📁 build/                              # Archivos generados durante la compilación
│   │   ├── 📁 generated/                      # Archivos generados automáticamente
│   │   │   ├── 📁 ap_generated_sources/      # Fuentes generadas
│   │   │   │   ├── 📁 debug/
│   │   │   │   ├── 📁 debugAndroidTest/
│   │   │   │   └── 📁 debugUnitTest/
│   │   │   └── 📁 res/                        # Recursos generados
│   │   │       ├── 📁 pngs/
│   │   │       └── 📁 resValues/
│   │   │
│   │   ├── 📁 intermediates/                  # Archivos intermedios de compilación
│   │   │   ├── 📁 aar_metadata_check/         # Verificación de metadatos AAR
│   │   │   ├── 📁 annotation_processor_list/ # Lista de procesadores de anotaciones
│   │   │   ├── 📁 apk/                        # APKs generados
│   │   │   ├── 📁 app_metadata/               # Metadatos de la aplicación
│   │   │   ├── 📁 assets/                     # Assets procesados
│   │   │   ├── 📁 compatible_screen_manifest/ # Compatibilidad de pantalla
│   │   │   ├── 📁 compile_and_runtime_not_namespaced_r_class_jar/ # Clases R
│   │   │   ├── 📁 compile_app_classes_jar/    # JAR de clases compiladas
│   │   │   ├── 📁 compressed_assets/          # Assets comprimidos
│   │   │   ├── 📁 data_binding_layout_info_type_merge/ # Data binding
│   │   │   ├── 📁 data_binding_layout_info_type_package/
│   │   │   ├── 📁 desugar_graph/              # Desugar de Java 8+
│   │   │   ├── 📁 dex/                        # Archivos DEX
│   │   │   ├── 📁 dex_archive_input_jar_hashes/ # Hashes de JARs
│   │   │   ├── 📁 dex_number_of_buckets_file/ # Buckets DEX
│   │   │   ├── 📁 duplicate_classes_check/    # Verificación de clases duplicadas
│   │   │   ├── 📁 external_file_lib_dex_archives/ # DEX de librerías externas
│   │   │   ├── 📁 external_libs_dex_archive/   # DEX de librerías
│   │   │   ├── 📁 incremental/                 # Compilación incremental
│   │   │   ├── 📁 javac/                      # Archivos Java compilados
│   │   │   ├── 📁 linked_resources_binary_format/ # Recursos enlazados
│   │   │   ├── 📁 local_only_symbol_list/     # Símbolos locales
│   │   │   ├── 📁 manifest_merge_blame_file/  # Merge de manifiestos
│   │   │   ├── 📁 merged_java_res/            # Recursos Java fusionados
│   │   │   ├── 📁 merged_jni_libs/            # Librerías JNI fusionadas
│   │   │   ├── 📁 merged_manifest/            # Manifiesto fusionado
│   │   │   ├── 📁 merged_manifests/           # Manifiestos fusionados
│   │   │   ├── 📁 merged_res/                 # Recursos fusionados
│   │   │   ├── 📁 merged_shaders/              # Shaders fusionados
│   │   │   ├── 📁 mixed_scope_dex_archive/   # DEX de scope mixto
│   │   │   ├── 📁 navigation_json/            # Navegación JSON
│   │   │   ├── 📁 nested_resources_validation_report/ # Validación de recursos
│   │   │   ├── 📁 packaged_manifests/         # Manifiestos empaquetados
│   │   │   ├── 📁 packaged_res/                # Recursos empaquetados
│   │   │   ├── 📁 project_dex_archive/        # DEX del proyecto
│   │   │   ├── 📁 runtime_symbol_list/        # Lista de símbolos runtime
│   │   │   ├── 📁 signing_config_versions/    # Versiones de configuración de firma
│   │   │   ├── 📁 source_set_path_map/         # Mapa de rutas de source sets
│   │   │   ├── 📁 stable_resource_ids_file/   # IDs de recursos estables
│   │   │   ├── 📁 sub_project_dex_archive/    # DEX de subproyectos
│   │   │   ├── 📁 symbol_list_with_package_name/ # Símbolos con nombre de paquete
│   │   │   ├── 📁 tmp/                        # Archivos temporales
│   │   │   └── 📁 validate_signing_config/    # Validación de firma
│   │   │
│   │   ├── 📁 outputs/                        # Salidas de compilación
│   │   │   ├── 📁 apk/                        # APKs generados
│   │   │   │   ├── 📁 androidTest/
│   │   │   │   └── 📁 debug/
│   │   │   └── 📁 logs/                       # Logs de compilación
│   │   │       └── 📄 manifest-merger-debug-report.txt
│   │   │
│   │   └── 📁 tmp/                            # Archivos temporales
│   │       ├── 📁 compileDebugAndroidTestJavaWithJavac/
│   │       ├── 📁 compileDebugJavaWithJavac/
│   │       └── 📁 compileDebugUnitTestJavaWithJavac/
│   │
│   └── 📁 src/                                # Código fuente
│       │
│       ├── 📁 androidTest/                    # Tests de instrumentación
│       │   └── 📁 java/
│       │       └── 📁 com/
│       │           └── 📁 example/
│       │               └── 📁 asteroidesreal/
│       │                   └── 📄 ExampleInstrumentedTest.java
│       │
│       ├── 📁 main/                           # Código fuente principal
│       │   ├── 📄 AndroidManifest.xml         # Manifiesto de la aplicación
│       │   │
│       │   ├── 📁 java/                       # Código Java
│       │   │   └── 📁 com/
│       │   │       └── 📁 example/
│       │   │           └── 📁 asteroidesreal/
│       │   │               │
│       │   │               ├── 📄 MainActivity.java              # Actividad principal (menú)
│       │   │               ├── 📄 Juego.java                     # Actividad del juego
│       │   │               ├── 📄 VistaJuego.java               # Vista principal del juego (4200 líneas)
│       │   │               ├── 📄 SettingsActivity.java           # Actividad de configuración
│       │   │               ├── 📄 PuntajesActivity.java           # Actividad de puntajes
│       │   │               ├── 📄 AcercaDeActivity.java          # Actividad "Acerca de"
│       │   │               │
│       │   │               ├── 📄 Grafico.java                   # Clase para gráficos normales
│       │   │               ├── 📄 VectorGrafico.java             # Clase para gráficos vectoriales
│       │   │               │
│       │   │               ├── 📄 Score.java                     # Modelo de datos de puntuación
│       │   │               ├── 📄 ScoreDatabaseHelper.java        # Helper de base de datos SQLite
│       │   │               ├── 📄 ScoreAdapter.java               # Adapter para RecyclerView de puntajes
│       │   │               │
│       │   │               ├── 📄 CountdownView.java             # Vista personalizada de cuenta regresiva
│       │   │               ├── 📄 StarView.java                  # Vista de estrellas animadas (menú)
│       │   │               ├── 📄 StarViewGame.java              # Vista de estrellas animadas (juego)
│       │   │               ├── 📄 GalaxyView.java                 # Vista de galaxia animada
│       │   │               ├── 📄 NebulaView.java                # Vista de nebulosa animada
│       │   │               ├── 📄 AlienView.java                 # Vista de aliens animados
│       │   │               │
│       │   │               ├── 📄 RainbowButton.java             # Botón con efecto arcoíris
│       │   │               ├── 📄 PixelArtButton.java            # Botón estilo pixel art
│       │   │               ├── 📄 AnimatedGalaxyButton.java      # Botón animado estilo galaxia
│       │   │               │
│       │   │               ├── 📄 PerlinNoise.java               # Generador de ruido Perlin para efectos
│       │   │               └── 📄 Configuracion.java            # Clase helper de configuración
│       │   │
│       │   └── 📁 res/                        # Recursos de la aplicación
│       │       │
│       │       ├── 📁 anim/                    # Animaciones XML
│       │       │   ├── 📄 aparecer.xml                         # Animación de aparición
│       │       │   ├── 📄 button_asteroid_spin.xml             # Animación de botón girando asteroide
│       │       │   ├── 📄 button_blink.xml                     # Animación de parpadeo de botón
│       │       │   ├── 📄 button_click.xml                     # Animación de click de botón
│       │       │   ├── 📄 button_float.xml                     # Animación de flotación de botón
│       │       │   ├── 📄 button_hover_glow.xml                # Animación de resplandor al hover
│       │       │   ├── 📄 button_pulse_galaxy.xml              # Animación de pulso galaxia
│       │       │   ├── 📄 button_rock_asteroid.xml              # Animación de roca asteroide
│       │       │   ├── 📄 button_rotate_continuous.xml          # Animación de rotación continua
│       │       │   ├── 📄 button_rotate_pulse.xml              # Animación de rotación con pulso
│       │       │   ├── 📄 button_wave.xml                     # Animación de onda
│       │       │   ├── 📄 countdown_scale.xml                  # Animación de escala para countdown
│       │       │   ├── 📄 desplazamiento_derecha.xml            # Animación de desplazamiento
│       │       │   └── 📄 giro_con_zoom.xml                    # Animación de giro con zoom
│       │       │
│       │       ├── 📁 drawable/                # Drawables (imágenes y formas)
│       │       │   ├── 📄 aliennormal.png                      # Imagen de alien normal
│       │       │   ├── 📄 alienpaz.png                         # Imagen de alien paz
│       │       │   ├── 📄 asicon.png                           # Icono de la aplicación
│       │       │   ├── 📄 asteroide1.png                       # Imagen de asteroide normal
│       │       │   ├── 📄 asteroidevector.png                  # Imagen de asteroide vectorial
│       │       │   ├── 📄 bg_title.xml                         # Drawable XML de fondo de título
│       │       │   ├── 📄 button_background_space.xml         # Fondo de botón espacial
│       │       │   ├── 📄 button_rainbow.xml                   # Botón arcoíris
│       │       │   ├── 📄 button_retro_galaxy_pressed.xml       # Botón galaxia presionado
│       │       │   ├── 📄 button_retro_galaxy.xml               # Botón galaxia
│       │       │   ├── 📄 ce1.png                              # Corazón estado 1
│       │       │   ├── 📄 ce2.png                              # Corazón estado 2
│       │       │   ├── 📄 cv1.png                              # Corazón vectorial
│       │       │   ├── 📄 es1.png                              # Escudo estado 1
│       │       │   ├── 📄 es1_5.png                            # Escudo estado 1.5
│       │       │   ├── 📄 es2.png                              # Escudo estado 2
│       │       │   ├── 📄 es3.png                              # Escudo estado 3
│       │       │   ├── 📄 es4.png                              # Escudo estado 4
│       │       │   ├── 📄 fondo_animado.xml                    # Fondo animado
│       │       │   ├── 📄 g1.png                               # Power-up tipo 1
│       │       │   ├── 📄 g2.png                               # Power-up tipo 2
│       │       │   ├── 📄 g3.png                               # Power-up tipo 3
│       │       │   ├── 📄 gradiantdrawable.xml                 # Gradiente drawable
│       │       │   ├── 📄 gradiente1.xml                       # Gradiente 1
│       │       │   ├── 📄 gradiente2.xml                       # Gradiente 2
│       │       │   ├── 📄 gradiente3.xml                       # Gradiente 3
│       │       │   ├── 📄 ic_arrow_back.xml                    # Icono de flecha atrás
│       │       │   ├── 📄 ic_launcher_background.xml           # Fondo del launcher
│       │       │   ├── 📄 ic_launcher_foreground.xml           # Primer plano del launcher
│       │       │   ├── 📄 jupiter.png                          # Imagen de Júpiter
│       │       │   ├── 📄 logo.png                             # Logo de la aplicación
│       │       │   ├── 📄 nave1.png                            # Imagen de nave normal
│       │       │   ├── 📄 navevector.png                       # Imagen de nave vectorial
│       │       │   ├── 📄 pd.png                               # Power-up disparo
│       │       │   ├── 📄 pe.png                               # Power-up escudo
│       │       │   ├── 📄 pv.png                               # Power-up velocidad
│       │       │   ├── 📄 s0.png                              # Sprite 0
│       │       │   ├── 📄 s1.png                              # Sprite 1
│       │       │   ├── 📄 s2.png                              # Sprite 2
│       │       │   ├── 📄 s3.png                              # Sprite 3
│       │       │   ├── 📄 s4.png                              # Sprite 4
│       │       │   ├── 📄 s5.png                              # Sprite 5
│       │       │   ├── 📄 s6.png                              # Sprite 6
│       │       │   ├── 📄 s7.png                              # Sprite 7
│       │       │   ├── 📄 s8.png                              # Sprite 8
│       │       │   ├── 📄 s9.png                              # Sprite 9
│       │       │   ├── 📄 s10.png                             # Sprite 10
│       │       │   └── 📄 s11.png                             # Sprite 11
│       │       │
│       │       ├── 📁 font/                    # Fuentes personalizadas
│       │       │   ├── 📄 pixel_font.xml                       # Definición de fuente pixel
│       │       │   └── 📄 upheavtt.ttf                        # Fuente TrueType pixel art
│       │       │
│       │       ├── 📁 layout/                  # Layouts XML
│       │       │   ├── 📄 activity_acercade.xml                # Layout de "Acerca de"
│       │       │   ├── 📄 activity_main.xml                    # Layout del menú principal
│       │       │   ├── 📄 activity_puntajes.xml                # Layout de puntajes
│       │       │   ├── 📄 activity_settings.xml                # Layout de configuración
│       │       │   ├── 📄 configuracion.xml                    # Layout de configuración (alternativo)
│       │       │   ├── 📄 item_score.xml                       # Layout de item de puntaje
│       │       │   └── 📄 juego.xml                            # Layout del juego
│       │       │
│       │       ├── 📁 layout-land/             # Layouts para orientación horizontal
│       │       │   └── 📄 activity_main.xml                    # Layout principal horizontal
│       │       │
│       │       ├── 📁 mipmap-anydpi-v26/       # Iconos adaptativos (Android 8.0+)
│       │       │   ├── 📄 ic_launcher.xml                      # Icono adaptativo
│       │       │   └── 📄 ic_launcher_round.xml                 # Icono adaptativo redondo
│       │       │
│       │       ├── 📁 mipmap-hdpi/             # Iconos alta densidad (240 dpi)
│       │       │   ├── 📄 ic_launcher.webp                      # Icono launcher
│       │       │   └── 📄 ic_launcher_round.webp                # Icono launcher redondo
│       │       │
│       │       ├── 📁 mipmap-mdpi/             # Iconos densidad media (160 dpi)
│       │       │   ├── 📄 ic_launcher.webp
│       │       │   └── 📄 ic_launcher_round.webp
│       │       │
│       │       ├── 📁 mipmap-xhdpi/            # Iconos extra alta densidad (320 dpi)
│       │       │   ├── 📄 ic_launcher.webp
│       │       │   └── 📄 ic_launcher_round.webp
│       │       │
│       │       ├── 📁 mipmap-xxhdpi/           # Iconos extra extra alta densidad (480 dpi)
│       │       │   ├── 📄 ic_launcher.webp
│       │       │   └── 📄 ic_launcher_round.webp
│       │       │
│       │       ├── 📁 mipmap-xxxhdpi/          # Iconos extra extra extra alta densidad (640 dpi)
│       │       │   ├── 📄 ic_launcher.webp
│       │       │   └── 📄 ic_launcher_round.webp
│       │       │
│       │       ├── 📁 raw/                     # Archivos de audio sin comprimir
│       │       │   ├── 📄 burbujaap.mp3                        # Sonido burbuja aparecer
│       │       │   ├── 📄 burubujaex.mp3                       # Sonido burbuja explotar
│       │       │   ├── 📄 explosion.mp3                        # Sonido de explosión
│       │       │   ├── 📄 l1.mp3                               # Música del nivel 1
│       │       │   ├── 📄 laser3.mp3                           # Sonido de láser/disparo
│       │       │   ├── 📄 mc.mp3                               # Música misión completada
│       │       │   └── 📄 pixel_galaxy.mp3                     # Música del menú principal
│       │       │
│       │       ├── 📁 values/                  # Valores de recursos
│       │       │   ├── 📄 animacion_g.xml                      # Animación de galaxia
│       │       │   ├── 📄 colors.xml                          # Colores de la aplicación
│       │       │   ├── 📄 strings.xml                          # Cadenas de texto
│       │       │   ├── 📄 style.xml                           # Estilos de la aplicación
│       │       │   └── 📄 themes.xml                          # Temas de la aplicación
│       │       │
│       │       ├── 📁 values-en/              # Valores en inglés (localización)
│       │       │   └── 📄 strings.xml                          # Cadenas en inglés
│       │       │
│       │       ├── 📁 values-land/             # Valores para orientación horizontal
│       │       │   └── 📄 dimens.xml                          # Dimensiones para landscape
│       │       │
│       │       ├── 📁 values-night/             # Valores para modo oscuro
│       │       │   └── 📄 themes.xml                          # Temas modo oscuro
│       │       │
│       │       └── 📁 xml/                     # Archivos XML de configuración
│       │           ├── 📄 backup_rules.xml                    # Reglas de backup
│       │           └── 📄 data_extraction_rules.xml            # Reglas de extracción de datos
│       │
│       └── 📁 test/                           # Tests unitarios
│           └── 📁 java/
│               └── 📁 com/
│                   └── 📁 example/
│                       └── 📁 asteroidesreal/
│                           └── 📄 ExampleUnitTest.java
│
├── 📁 build/                                  # Archivos de compilación del proyecto raíz
│   └── 📁 reports/
│       └── 📁 problems/
│           └── 📄 problems-report.html
│
├── 📁 gradle/                                 # Configuración de Gradle
│   ├── 📄 libs.versions.toml                  # Versiones de librerías (catalog)
│   └── 📁 wrapper/                            # Gradle Wrapper
│       ├── 📄 gradle-wrapper.jar              # JAR del wrapper
│       └── 📄 gradle-wrapper.properties       # Propiedades del wrapper
│
├── 📄 build.gradle.kts                       # Configuración Gradle del proyecto raíz
├── 📄 settings.gradle.kts                    # Configuración de módulos del proyecto
├── 📄 gradle.properties                       # Propiedades de Gradle
├── 📄 gradlew                                 # Script Gradle Wrapper (Unix/Mac)
├── 📄 gradlew.bat                             # Script Gradle Wrapper (Windows)
├── 📄 local.properties                        # Propiedades locales (rutas SDK, etc.)
├── 📄 README.md                               # Documentación principal
├── 📄 ARQUITECTURA.md                         # Diagramas de arquitectura
├── 📄 ESTRUCTURA_CARPETAS.md                  # Este archivo
└── 📄 SUGERENCIAS_ANIMACIONES_BOTONES.md     # Sugerencias de animaciones
```

## Descripción de Carpetas Principales

### 📁 app/
Módulo principal de la aplicación Android. Contiene todo el código fuente, recursos y configuración del proyecto.

### 📁 app/src/main/
Contiene el código fuente principal de la aplicación.

#### 📁 app/src/main/java/com/example/asteroidesreal/
**Actividades (Activities):**
- `MainActivity.java` - Actividad principal con menú
- `Juego.java` - Actividad del juego
- `SettingsActivity.java` - Configuración
- `PuntajesActivity.java` - Lista de puntajes
- `AcercaDeActivity.java` - Información del juego

**Vistas Personalizadas (Custom Views):**
- `VistaJuego.java` - Vista principal del juego (4200 líneas, contiene toda la lógica)
- `CountdownView.java` - Cuenta regresiva
- `StarView.java` / `StarViewGame.java` - Fondos de estrellas
- `GalaxyView.java` - Fondo de galaxia
- `NebulaView.java` - Fondo de nebulosa
- `AlienView.java` - Aliens animados

**Componentes Gráficos:**
- `Grafico.java` - Gráficos normales con imágenes
- `VectorGrafico.java` - Gráficos vectoriales puros

**Componentes UI:**
- `RainbowButton.java` - Botón con efecto arcoíris
- `PixelArtButton.java` - Botón estilo pixel art
- `AnimatedGalaxyButton.java` - Botón animado galaxia

**Modelos y Datos:**
- `Score.java` - Modelo de puntuación
- `ScoreDatabaseHelper.java` - Helper SQLite
- `ScoreAdapter.java` - Adapter para RecyclerView

**Utilidades:**
- `PerlinNoise.java` - Generador de ruido Perlin
- `Configuracion.java` - Helper de configuración

### 📁 app/src/main/res/
Recursos de la aplicación Android.

#### 📁 res/anim/
14 archivos de animación XML para botones y efectos visuales.

#### 📁 res/drawable/
48 archivos: imágenes PNG y drawables XML para sprites, iconos, fondos y efectos.

#### 📁 res/font/
Fuentes personalizadas: fuente pixel art en formato TTF y XML.

#### 📁 res/layout/
7 layouts XML para las diferentes pantallas de la aplicación.

#### 📁 res/layout-land/
Layouts específicos para orientación horizontal.

#### 📁 res/mipmap-*/
Iconos de la aplicación en diferentes densidades de pantalla (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) y versiones adaptativas.

#### 📁 res/raw/
7 archivos de audio MP3: música de fondo y efectos de sonido.

#### 📁 res/values/
Archivos de valores: strings, colors, styles, themes.

#### 📁 res/values-en/
Localización en inglés.

#### 📁 res/values-land/
Valores específicos para orientación horizontal.

#### 📁 res/values-night/
Temas para modo oscuro.

#### 📁 res/xml/
Configuraciones XML: reglas de backup y extracción de datos.

### 📁 app/build/
Archivos generados durante la compilación (no se versionan en git).

### 📁 gradle/
Configuración de Gradle y wrapper.

### 📁 build/
Archivos de compilación del proyecto raíz.

## Base de Datos

**Ubicación en el dispositivo:**
```
/data/data/com.example.asteroidesreal/databases/scores.db
```

**Estructura:**
- **Tabla:** `scores`
- **Columnas:**
  - `_id` (INTEGER PRIMARY KEY AUTOINCREMENT)
  - `puntaje` (INTEGER NOT NULL)
  - `fecha` (INTEGER NOT NULL)
  - `nombre` (TEXT NOT NULL)

## Archivos de Configuración

- `build.gradle.kts` (raíz) - Configuración del proyecto
- `app/build.gradle.kts` - Configuración del módulo app
- `settings.gradle.kts` - Configuración de módulos
- `gradle.properties` - Propiedades de Gradle
- `AndroidManifest.xml` - Manifiesto de la aplicación
- `proguard-rules.pro` - Reglas de ProGuard

## Notas

- Los archivos en `app/build/` son generados automáticamente y no deben editarse manualmente
- Los recursos en `res/` se organizan por tipo y densidad de pantalla
- El código fuente está en `app/src/main/java/`
- Los tests están en `app/src/test/` (unitarios) y `app/src/androidTest/` (instrumentación)



