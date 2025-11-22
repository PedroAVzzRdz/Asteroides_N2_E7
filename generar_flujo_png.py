#!/usr/bin/env python3
"""
Script para generar diagrama de flujo PNG del juego AsteroidesApp
Requiere: Pillow (PIL)
Instalar: pip install Pillow
"""

from PIL import Image, ImageDraw, ImageFont
import os

def generar_flujo_png():
    """Genera una imagen PNG con el flujo principal del juego"""
    
    # Configuración
    width = 800
    padding = 30
    box_height = 80
    box_width = 700
    spacing = 20
    arrow_height = 30
    
    # Definir los pasos del flujo
    pasos = [
        ("Jugador", "Entrada: Táctil/Teclado/Sensores"),
        ("Motor del Juego", "VistaJuego.actualizaFisica()"),
        ("Render", "VistaJuego.onDraw()\nDibuja: Nave, Asteroides, Misiles"),
        ("Motor de Colisiones", "verificaColision()\nNave vs Asteroides"),
        ("¿Colisión?", "Sí → Resta vida\nNo → Continúa"),
        ("Ciclo se repite", "~60 FPS\nThreadJuego")
    ]
    
    # Calcular altura total
    total_height = padding * 2 + len(pasos) * (box_height + spacing) + (len(pasos) - 1) * arrow_height
    
    # Crear imagen
    img = Image.new('RGB', (width, total_height), color='white')
    draw = ImageDraw.Draw(img)
    
    # Colores
    box_color = (70, 130, 180)  # Steel blue
    text_color = 'white'
    arrow_color = 'black'
    
    try:
        # Intentar usar fuente
        title_font = ImageFont.truetype("arial.ttf", 20)
        box_font = ImageFont.truetype("arial.ttf", 14)
        small_font = ImageFont.truetype("arial.ttf", 12)
    except:
        try:
            title_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 20)
            box_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 14)
            small_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 12)
        except:
            title_font = ImageFont.load_default()
            box_font = ImageFont.load_default()
            small_font = ImageFont.load_default()
    
    # Dibujar título
    title = "Diagrama de Arquitectura / Flujo del Juego"
    title_bbox = draw.textbbox((0, 0), title, font=title_font)
    title_width = title_bbox[2] - title_bbox[0]
    draw.text(((width - title_width) // 2, padding), title, fill='black', font=title_font)
    
    y = padding + 40
    
    # Dibujar cada paso
    for i, (titulo, descripcion) in enumerate(pasos):
        # Calcular posición del box
        x = (width - box_width) // 2
        
        # Dibujar box
        draw.rectangle(
            [x, y, x + box_width, y + box_height],
            fill=box_color,
            outline='black',
            width=2
        )
        
        # Dibujar texto del título
        title_bbox = draw.textbbox((0, 0), titulo, font=box_font)
        title_x = x + (box_width - (title_bbox[2] - title_bbox[0])) // 2
        draw.text((title_x, y + 10), titulo, fill=text_color, font=box_font)
        
        # Dibujar descripción (puede tener múltiples líneas)
        desc_lines = descripcion.split('\n')
        desc_y = y + 35
        for line in desc_lines:
            line_bbox = draw.textbbox((0, 0), line, font=small_font)
            line_x = x + (box_width - (line_bbox[2] - line_bbox[0])) // 2
            draw.text((line_x, desc_y), line, fill=text_color, font=small_font)
            desc_y += 18
        
        y += box_height + spacing
        
        # Dibujar flecha (excepto después del último paso)
        if i < len(pasos) - 1:
            arrow_x = width // 2
            arrow_y_start = y - spacing
            arrow_y_end = y
            
            # Línea vertical
            draw.line(
                [(arrow_x, arrow_y_start), (arrow_x, arrow_y_end)],
                fill=arrow_color,
                width=3
            )
            
            # Punta de flecha
            arrow_size = 10
            draw.polygon(
                [
                    (arrow_x, arrow_y_end),
                    (arrow_x - arrow_size, arrow_y_end - arrow_size),
                    (arrow_x + arrow_size, arrow_y_end - arrow_size)
                ],
                fill=arrow_color
            )
            
            y += arrow_height
    
    # Guardar imagen
    img.save('diagrama_flujo_juego.png')
    print("Imagen generada: diagrama_flujo_juego.png")

if __name__ == "__main__":
    generar_flujo_png()

