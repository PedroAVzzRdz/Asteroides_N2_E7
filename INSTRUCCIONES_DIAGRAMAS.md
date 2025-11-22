# Instrucciones para Generar Diagramas PNG

Este documento explica cómo convertir los diagramas de arquitectura y estructura de carpetas a formato PNG.

## Opción 1: Usar Mermaid Live Editor (Recomendado)

### Para Diagramas de Arquitectura (ARQUITECTURA.md)

1. Abre [Mermaid Live Editor](https://mermaid.live/)
2. Copia el código Mermaid del archivo `ARQUITECTURA.md`
3. Pega el código en el editor
4. Haz clic en "Actions" → "Download PNG"
5. Guarda la imagen con el nombre deseado

### Diagramas Disponibles en ARQUITECTURA.md:
- Arquitectura del Sistema (graph TB)
- Flujo de Datos (sequenceDiagram)
- Arquitectura de Componentes (classDiagram)
- Sistema de Capas (graph LR)

## Opción 2: Usar Mermaid CLI

### Instalación
```bash
npm install -g @mermaid-js/mermaid-cli
```

### Generar PNG desde archivo Mermaid
```bash
mmdc -i ARQUITECTURA.md -o arquitectura.png
```

## Opción 3: Usar Python Script

### Para Estructura de Carpetas

1. Instalar dependencias:
```bash
pip install Pillow
```

2. Ejecutar el script:
```bash
python generar_diagramas.py
```

Esto generará `estructura_carpetas.png`

## Opción 4: Usar Herramientas Online

### Para Diagramas Mermaid:
- [Mermaid Live Editor](https://mermaid.live/)
- [Mermaid.ink](https://mermaid.ink/)

### Para Estructura de Carpetas:
- [Tree Generator](https://tree.nathanfriend.com/)
- [ASCII Tree Generator](https://ascii-tree-generator.com/)

## Opción 5: Usar Visual Studio Code

1. Instala la extensión "Markdown Preview Mermaid Support"
2. Abre `ARQUITECTURA.md`
3. Usa el preview de Markdown
4. Exporta como imagen desde el preview

## Opción 6: Convertir desde Terminal (Linux/Mac)

### Usar Graphviz para diagramas de flujo:
```bash
# Instalar Graphviz
sudo apt-get install graphviz  # Ubuntu/Debian
brew install graphviz          # macOS

# Convertir DOT a PNG (si generas archivos .dot)
dot -Tpng diagrama.dot -o diagrama.png
```

## Recomendación

Para obtener los mejores resultados:

1. **Diagramas Mermaid**: Usa [Mermaid Live Editor](https://mermaid.live/) - es la forma más fácil y rápida
2. **Estructura de Carpetas**: Usa el script Python `generar_diagramas.py` o copia el contenido de `ESTRUCTURA_CARPETAS.md` a un generador de árboles online

## Notas

- Los diagramas Mermaid en `ARQUITECTURA.md` están listos para copiar y pegar
- La estructura de carpetas en `ESTRUCTURA_CARPETAS.md` está en formato texto plano y puede convertirse fácilmente
- Todos los diagramas están optimizados para impresión y presentación



