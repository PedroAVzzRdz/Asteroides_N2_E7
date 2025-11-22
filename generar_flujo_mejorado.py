#!/usr/bin/env python3
"""
Script mejorado para generar diagrama de flujo PNG del juego AsteroidesApp
Similar al estilo del diagrama de referencia
"""

from PIL import Image, ImageDraw, ImageFont
import os

def generar_flujo_mejorado():
    """Genera una imagen PNG con el flujo principal del juego en estilo vertical"""
    
    # Configuración
    width = 600
    padding = 40
    box_height = 100
    box_width = 520
    spacing = 15
    arrow_height = 25
    
    # Definir los pasos del flujo (similar al diagrama de referencia)
    pasos = [
        ("Jugador", "Entrada tactil/teclado/sensores"),
        ("Motor del juego", "Actualiza fisica\nVistaJuego.actualizaFisica()"),
        ("Render", "Dibuja asteroides y naves\nVistaJuego.onDraw()"),
        ("Motor verifica colisiones", "verificaColision()\nNave vs Asteroides"),
        ("Si colision", "Fin del juego / restar vida\nvidas--"),
        ("Ciclo se repite", "60 FPS\nThreadJuego.run()")
    ]
    
    # Calcular altura total
    total_height = padding * 2 + len(pasos) * (box_height + spacing) + (len(pasos) - 1) * arrow_height + 50
    
    # Crear imagen
    img = Image.new('RGB', (width, total_height), color='white')
    draw = ImageDraw.Draw(img)
    
    # Colores (similar al diagrama de referencia - teal/blue)
    box_color = (0, 128, 128)  # Teal
    text_color = 'white'
    arrow_color = 'black'
    title_color = 'black'
    
    try:
        # Intentar usar fuente Arial
        title_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 18)
        box_title_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 16)
        box_desc_font = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 13)
    except:
        try:
            title_font = ImageFont.truetype("arial.ttf", 18)
            box_title_font = ImageFont.truetype("arial.ttf", 16)
            box_desc_font = ImageFont.truetype("arial.ttf", 13)
        except:
            title_font = ImageFont.load_default()
            box_title_font = ImageFont.load_default()
            box_desc_font = ImageFont.load_default()
    
    # Dibujar título
    title = "Diagrama de arquitectura / flujo recomendado"
    title_bbox = draw.textbbox((0, 0), title, font=title_font)
    title_width = title_bbox[2] - title_bbox[0]
    draw.text((padding, padding), title, fill=title_color, font=title_font)
    
    y = padding + 50
    
    # Dibujar cada paso
    for i, (titulo, descripcion) in enumerate(pasos):
        # Calcular posición del box (centrado)
        x = (width - box_width) // 2
        
        # Dibujar box con bordes redondeados (simulado)
        draw.rectangle(
            [x, y, x + box_width, y + box_height],
            fill=box_color,
            outline='black',
            width=2
        )
        
        # Dibujar texto del título (centrado)
        title_bbox = draw.textbbox((0, 0), titulo, font=box_title_font)
        title_text_width = title_bbox[2] - title_bbox[0]
        title_x = x + (box_width - title_text_width) // 2
        draw.text((title_x, y + 15), titulo, fill=text_color, font=box_title_font)
        
        # Dibujar descripción (puede tener múltiples líneas, centrado)
        desc_lines = descripcion.split('\n')
        desc_y = y + 45
        for line in desc_lines:
            line_bbox = draw.textbbox((0, 0), line, font=box_desc_font)
            line_text_width = line_bbox[2] - line_bbox[0]
            line_x = x + (box_width - line_text_width) // 2
            draw.text((line_x, desc_y), line, fill=text_color, font=box_desc_font)
            desc_y += 20
        
        y += box_height + spacing
        
        # Dibujar flecha hacia abajo (excepto después del último paso)
        if i < len(pasos) - 1:
            arrow_x = width // 2
            arrow_y_start = y - spacing
            arrow_y_end = y
            
            # Línea vertical de la flecha
            draw.line(
                [(arrow_x, arrow_y_start), (arrow_x, arrow_y_end)],
                fill=arrow_color,
                width=3
            )
            
            # Punta de flecha (triángulo hacia abajo)
            arrow_size = 8
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
    output_file = 'diagrama_flujo_recomendado.png'
    img.save(output_file)
    print(f"Imagen generada: {output_file}")
    print(f"Dimensiones: {width}x{total_height} pixels")

if __name__ == "__main__":
    generar_flujo_mejorado()



