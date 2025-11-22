# Tutorial de Instalación y Uso - AsteroidesApp

> **Objetivo**: Este tutorial te permitirá clonar, configurar y ejecutar el proyecto sin necesidad de ayuda adicional.

---

## Vista del Proyecto

<div align="center">

![Imagen del Proyecto en GitHub](https://via.placeholder.com/800x400/1a1a2e/ffffff?text=Captura+de+Pantalla+del+Proyecto+en+GitHub)

**Nota**: Reemplaza esta imagen con una captura de pantalla real del proyecto en GitHub que muestre:
- La estructura del repositorio
- El README principal
- O una vista general del proyecto en GitHub

</div>

---

## Tabla de Contenidos

1. [Cómo Poner el Proyecto en Marcha](#cómo-poner-el-proyecto-en-marcha)
2. [Cómo Instalar Dependencias](#cómo-instalar-dependencias)
3. [Cómo Ejecutar el Entorno Local](#cómo-ejecutar-el-entorno-local)
4. [Cómo Correr Tests o el Servidor](#cómo-correr-tests-o-el-servidor)
5. [Cómo Contribuir](#cómo-contribuir)

---

## Cómo Poner el Proyecto en Marcha

### Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

#### Software Necesario

- **Java Development Kit (JDK) 11 o superior**
  - Descarga desde: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://openjdk.org/)
  - Verifica la instalación: `java -version`

- **Android Studio** (versión más reciente recomendada)
  - Descarga desde: [Android Studio](https://developer.android.com/studio)
  - Incluye Android SDK, Gradle y todas las herramientas necesarias

- **Git** (para clonar el repositorio)
  - Descarga desde: [Git](https://git-scm.com/downloads)
  - Verifica la instalación: `git --version`

#### Requisitos del Sistema

- **Android SDK mínimo**: API 24 (Android 7.0 Nougat)
- **Android SDK objetivo**: API 36
- **Gradle**: Versión 8.12.1 o superior (incluido en Android Studio)
- **Sistema Operativo**: Windows, macOS o Linux

### Clonar el Repositorio

#### Paso 1: Obtener la URL del Repositorio

1. Ve a la página principal del repositorio en GitHub
2. Haz clic en el botón verde **"Code"**
3. Copia la URL del repositorio (HTTPS o SSH)

#### Paso 2: Clonar el Repositorio

Abre tu terminal (PowerShell en Windows, Terminal en macOS/Linux) y ejecuta:

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/AsteroidesReal2.git

# O si usas SSH:
# git clone git@github.com:tu-usuario/AsteroidesReal2.git
```

#### Paso 3: Navegar al Directorio del Proyecto

```bash
cd AsteroidesReal2
```

### Configurar el Proyecto

#### Paso 1: Abrir el Proyecto en Android Studio

1. Abre Android Studio
2. Selecciona **"Open an Existing Project"** o **"File > Open"**
3. Navega a la carpeta `AsteroidesReal2` y selecciónala
4. Haz clic en **"OK"**

#### Paso 2: Configurar Android SDK

1. En Android Studio, ve a **"File > Settings"** (o **"Android Studio > Preferences"** en macOS)
2. Navega a **"Appearance & Behavior > System Settings > Android SDK"**
3. Asegúrate de tener instalado:
   - **Android SDK Platform 36** (o la versión objetivo)
   - **Android SDK Build-Tools**
   - **Android SDK Platform-Tools**

#### Paso 3: Configurar el Emulador o Dispositivo Físico

##### Opción A: Usar un Dispositivo Físico

1. **Habilitar Modo Desarrollador en tu dispositivo Android**:
   - Ve a **Configuración > Acerca del teléfono**
   - Toca **"Número de compilación"** 7 veces
   - Regresa a **Configuración** y busca **"Opciones de desarrollador"**
   - Activa **"Depuración USB"**

2. **Conectar el dispositivo**:
   - Conecta tu dispositivo Android a la computadora con un cable USB
   - Acepta el diálogo de depuración USB en el dispositivo
   - En Android Studio, deberías ver tu dispositivo en la lista de dispositivos

##### Opción B: Usar un Emulador

1. En Android Studio, ve a **"Tools > Device Manager"**
2. Haz clic en **"Create Device"**
3. Selecciona un dispositivo virtual (por ejemplo, Pixel 5)
4. Selecciona una imagen del sistema (API 24 o superior)
5. Haz clic en **"Finish"**
6. Inicia el emulador haciendo clic en el botón de **"Play"**

#### Paso 4: Verificar la Configuración

```bash
# Verificar que Gradle está configurado correctamente
.\gradlew tasks

# Verificar que el proyecto compila sin errores
.\gradlew build
```

---

## Cómo Instalar Dependencias

### Opción 1: Instalación Automática (Recomendada)

Las dependencias se instalan automáticamente cuando abres el proyecto en Android Studio:

1. **Sincronizar Gradle**
   - Android Studio detectará automáticamente el archivo `build.gradle.kts`
   - Aparecerá un mensaje: **"Gradle files have changed since last project sync"**
   - Haz clic en **"Sync Now"** o ve a **"File > Sync Project with Gradle Files"**
   - Espera a que se descarguen todas las dependencias (puede tomar varios minutos la primera vez)

### Opción 2: Instalación Manual desde Terminal

Si prefieres instalar las dependencias manualmente:

```bash
# En Windows (PowerShell o CMD)
.\gradlew build --refresh-dependencies

# En macOS/Linux
./gradlew build --refresh-dependencies
```

### Dependencias del Proyecto

El proyecto utiliza las siguientes librerías principales (ya configuradas en `app/build.gradle.kts`):

- `androidx.appcompat:appcompat:1.7.1` - Compatibilidad con versiones anteriores de Android
- `com.google.android.material:material:1.12.0` - Componentes Material Design
- `androidx.activity:activity:1.10.1` - Componentes de Activity
- `androidx.constraintlayout:constraintlayout:2.2.1` - Layouts con constraints

**No necesitas instalar estas dependencias manualmente**, Gradle las descarga automáticamente cuando sincronizas el proyecto.

---

## Cómo Ejecutar el Entorno Local

### Método 1: Desde Android Studio (Recomendado)

1. **Asegúrate de tener un dispositivo o emulador conectado**
   - Verifica en la barra superior de Android Studio que aparece tu dispositivo

2. **Ejecutar la aplicación**:
   - Haz clic en el botón verde **"Run"** en la barra de herramientas
   - O presiona **`Shift + F10`** (Windows/Linux) o **`Ctrl + R`** (macOS)
   - O ve a **"Run > Run 'app'"**

3. **Esperar a que compile e instale**
   - Android Studio compilará el proyecto
   - Instalará la aplicación en tu dispositivo/emulador
   - La aplicación se abrirá automáticamente

### Método 2: Desde la Terminal

```bash
# En Windows (PowerShell o CMD)
.\gradlew installDebug

# En macOS/Linux
./gradlew installDebug
```

**Nota**: Asegúrate de tener un dispositivo conectado o un emulador ejecutándose antes de ejecutar este comando.

### Método 3: Generar APK e Instalarlo Manualmente

```bash
# Generar APK de debug
.\gradlew assembleDebug

# El APK se generará en: app/build/outputs/apk/debug/app-debug.apk
# Puedes instalarlo manualmente en tu dispositivo
```

### Verificar que el Proyecto Está Funcionando

Una vez que la aplicación se haya instalado y ejecutado:

1. Deberías ver la pantalla principal del juego con el menú
2. Puedes navegar por las opciones del menú
3. Puedes iniciar una partida presionando "Jugar"
4. El juego debería responder a los controles correctamente

---

## Cómo Correr Tests o el Servidor

### Tests Unitarios

```bash
# Ejecutar todos los tests unitarios
.\gradlew test

# En macOS/Linux
./gradlew test

# Ejecutar tests de un paquete específico
.\gradlew test --tests "com.example.asteroidesreal.*"
```

### Tests de Instrumentación (Android)

#### Desde Android Studio

1. Haz clic derecho en la carpeta `androidTest` en el panel de proyecto
2. Selecciona **"Run 'Tests in 'androidTest''"**

#### Desde la Terminal

```bash
# Ejecutar tests de instrumentación
.\gradlew connectedAndroidTest

# En macOS/Linux
./gradlew connectedAndroidTest

# Nota: Requiere un dispositivo o emulador conectado
```

### Ver Resultados de Tests

- Los resultados aparecerán en la consola de Android Studio
- También puedes verlos en: `app/build/reports/tests/`
- Los reportes HTML se generan en: `app/build/reports/tests/test/index.html`

### Compilar el Proyecto sin Ejecutar Tests

```bash
# Compilar sin ejecutar tests
.\gradlew build -x test

# En macOS/Linux
./gradlew build -x test
```

---

## Cómo Contribuir

Este proyecto es de código abierto y las contribuciones son bienvenidas. Sigue estos pasos para contribuir al proyecto:

### Paso 1: Fork del Repositorio

1. Ve a la página principal del repositorio en GitHub
2. Haz clic en el botón **"Fork"** en la esquina superior derecha
3. Esto creará una copia del repositorio en tu cuenta de GitHub

### Paso 2: Clonar tu Fork

```bash
# Clonar tu fork (reemplaza TU-USUARIO con tu nombre de usuario)
git clone https://github.com/TU-USUARIO/AsteroidesReal2.git
cd AsteroidesReal2
```

### Paso 3: Configurar el Repositorio Remoto

```bash
# Agregar el repositorio original como "upstream"
git remote add upstream https://github.com/USUARIO-ORIGINAL/AsteroidesReal2.git

# Verificar los remotos configurados
git remote -v
```

### Paso 4: Crear una Rama para tu Feature

```bash
# Actualizar tu rama principal
git checkout main
git pull upstream main

# Crear una nueva rama para tu feature
git checkout -b feature/mi-nueva-funcionalidad

# O para corregir un bug:
git checkout -b fix/correccion-de-bug
```

**Convenciones de nombres para ramas**:
- `feature/` - Para nuevas funcionalidades
- `fix/` - Para correcciones de bugs
- `docs/` - Para cambios en documentación
- `refactor/` - Para refactorización de código

### Paso 5: Hacer Cambios y Commits

1. **Realiza tus cambios** en el código
2. **Prueba tus cambios** para asegurarte de que funcionan correctamente
3. **Ejecuta los tests** para verificar que no rompiste nada
4. **Haz commit de tus cambios**:

```bash
# Ver los archivos modificados
git status

# Agregar archivos al staging
git add .

# O agregar archivos específicos
git add app/src/main/java/com/example/asteroidesreal/MiArchivo.java

# Hacer commit con un mensaje descriptivo
git commit -m "Agregar nueva funcionalidad: descripción breve"
```

**Buenas prácticas para commits**:
- Usa mensajes claros y descriptivos
- Escribe en presente: "Agrega funcionalidad" no "Agregué funcionalidad"
- Limita la primera línea a 50 caracteres
- Agrega más detalles en el cuerpo del commit si es necesario

### Paso 6: Sincronizar con el Repositorio Original

Antes de hacer push, asegúrate de tener los últimos cambios:

```bash
# Obtener los últimos cambios del repositorio original
git fetch upstream

# Fusionar los cambios en tu rama
git merge upstream/main
```

### Paso 7: Subir tus Cambios

```bash
# Subir tu rama a tu fork
git push origin feature/mi-nueva-funcionalidad
```

### Paso 8: Crear un Pull Request

1. Ve a la página de tu fork en GitHub
2. Verás un mensaje que dice **"Compare & pull request"** - haz clic en él
3. O ve a la página del repositorio original y haz clic en **"Pull Requests" > "New Pull Request"**
4. Selecciona tu fork y tu rama
5. **Completa el formulario del Pull Request**:
   - **Título**: Describe brevemente tus cambios
   - **Descripción**: Explica qué cambios hiciste y por qué
   - Menciona si hay issues relacionados (ej: "Fixes #123")
6. Haz clic en **"Create Pull Request"**

### Paso 9: Revisar y Responder a Comentarios

- Los mantenedores del proyecto revisarán tu Pull Request
- Pueden pedir cambios o hacer preguntas
- Responde a los comentarios y haz los cambios necesarios
- Puedes hacer más commits en tu rama y se agregarán automáticamente al PR

### Guías de Contribución

- **Código**: Sigue las convenciones de código Java del proyecto
- **Commits**: Usa mensajes claros y descriptivos
- **Tests**: Agrega tests para nuevas funcionalidades cuando sea posible
- **Documentación**: Actualiza la documentación si es necesario
- **Comunicación**: Sé respetuoso y constructivo en los comentarios

---

## Solución de Problemas Comunes

### Error: "SDK location not found"

**Solución**:
1. Crea un archivo `local.properties` en la raíz del proyecto
2. Agrega la ruta de tu Android SDK:
   ```
   sdk.dir=C\:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk
   ```
   (Ajusta la ruta según tu sistema operativo)

### Error: "Gradle sync failed"

**Solución**:
1. Ve a **"File > Invalidate Caches / Restart"**
2. Selecciona **"Invalidate and Restart"**
3. Espera a que Android Studio reinicie y sincronice nuevamente

### Error: "Device not found" o "No devices"

**Solución**:
1. Verifica que tu dispositivo esté conectado: `adb devices`
2. Asegúrate de que la depuración USB esté activada
3. Reinicia el servidor ADB: `adb kill-server` y luego `adb start-server`

### Error al compilar: "Unsupported class file major version"

**Solución**:
- Asegúrate de tener JDK 11 o superior instalado
- Verifica la versión: `java -version`
- Configura la versión de Java en Android Studio: **"File > Project Structure > SDK Location"**

---

## Recursos Adicionales

- **Documentación de Android**: [developer.android.com](https://developer.android.com)
- **Documentación de Gradle**: [gradle.org](https://docs.gradle.org)
- **Guía de Git**: [git-scm.com](https://git-scm.com/doc)
- **Arquitectura del Proyecto**: Ver [ARQUITECTURA.md](ARQUITECTURA.md)
- **Estructura de Carpetas**: Ver [ESTRUCTURA_CARPETAS.md](ESTRUCTURA_CARPETAS.md)

---

## Checklist de Verificación

Antes de considerar que todo está configurado correctamente, verifica:

- [ ] Java 11+ instalado y configurado
- [ ] Android Studio instalado
- [ ] Repositorio clonado exitosamente
- [ ] Gradle sincronizado sin errores
- [ ] Dependencias instaladas correctamente
- [ ] Dispositivo o emulador configurado
- [ ] Aplicación se ejecuta correctamente
- [ ] Tests se ejecutan sin errores

---

## Listo

Si has completado todos los pasos anteriores, deberías tener el proyecto funcionando correctamente. 

**¿Necesitas ayuda?**
- Abre un issue en GitHub
- Revisa la documentación adicional en el proyecto
- Consulta los recursos adicionales mencionados arriba

---

**Última actualización**: 2024
