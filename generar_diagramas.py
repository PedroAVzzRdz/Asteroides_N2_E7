#!/usr/bin/env python3
"""
Script para generar diagramas PNG de la estructura del proyecto AsteroidesApp
Requiere: Pillow (PIL)
Instalar: pip install Pillow
"""

from PIL import Image, ImageDraw, ImageFont
import os

def generar_estructura_png():
    """Genera una imagen PNG con la estructura completa de carpetas"""
    
    # Configuración
    width = 1200
    padding = 20
    line_height = 25
    font_size = 14
    
    # Estructura de carpetas (simplificada para la imagen)
    estructura = """
AsteroidesReal2/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   ├── build/ (generados)
│   └── src/
│       ├── androidTest/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/asteroidesreal/
│       │   │   ├── MainActivity.java
│       │   │   ├── Juego.java
│       │   │   ├── VistaJuego.java (4200 líneas)
│       │   │   ├── SettingsActivity.java
│       │   │   ├── PuntajesActivity.java
│       │   │   ├── AcercaDeActivity.java
│       │   │   ├── Grafico.java
│       │   │   ├── VectorGrafico.java
│       │   │   ├── Score.java
│       │   │   ├── ScoreDatabaseHelper.java
│       │   │   ├── ScoreAdapter.java
│       │   │   ├── CountdownView.java
│       │   │   ├── StarView.java
│       │   │   ├── StarViewGame.java
│       │   │   ├── GalaxyView.java
│       │   │   ├── NebulaView.java
│       │   │   ├── AlienView.java
│       │   │   ├── RainbowButton.java
│       │   │   ├── PixelArtButton.java
│       │   │   ├── AnimatedGalaxyButton.java
│       │   │   ├── PerlinNoise.java
│       │   │   └── Configuracion.java
│       │   └── res/
│       │       ├── anim/ (14 archivos)
│       │       ├── drawable/ (48 archivos)
│       │       ├── font/ (2 archivos)
│       │       ├── layout/ (7 archivos)
│       │       ├── layout-land/ (1 archivo)
│       │       ├── mipmap-*/ (iconos)
│       │       ├── raw/ (7 archivos MP3)
│       │       ├── values/ (5 archivos)
│       │       ├── values-en/ (1 archivo)
│       │       ├── values-land/ (1 archivo)
│       │       ├── values-night/ (1 archivo)
│       │       └── xml/ (2 archivos)
│       └── test/
├── build/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
├── README.md
├── ARQUITECTURA.md
└── ESTRUCTURA_CARPETAS.md
"""
    
    lines = estructura.strip().split('\n')
    height = len(lines) * line_height + padding * 2
    
    # Crear imagen
    img = Image.new('RGB', (width, height), color='white')
    draw = ImageDraw.Draw(img)
    
    try:
        # Intentar usar fuente monospace
        font = ImageFont.truetype("consola.ttf", font_size)
    except:
        try:
            font = ImageFont.truetype("Courier", font_size)
        except:
            font = ImageFont.load_default()
    
    y = padding
    for line in lines:
        # Colorear según tipo
        if line.strip().endswith('/'):
            color = 'blue'
        elif line.strip().endswith('.java'):
            color = 'green'
        elif line.strip().endswith('.xml') or line.strip().endswith('.kts'):
            color = 'orange'
        elif line.strip().endswith('.md'):
            color = 'purple'
        elif line.strip().endswith('.png') or line.strip().endswith('.mp3'):
            color = 'red'
        else:
            color = 'black'
        
        draw.text((padding, y), line, fill=color, font=font)
        y += line_height
    
    # Guardar imagen
    img.save('estructura_carpetas.png')
    print("Imagen generada: estructura_carpetas.png")

if __name__ == "__main__":
    generar_estructura_png()

