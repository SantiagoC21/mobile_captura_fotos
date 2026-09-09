# Facial Dataset Mobile App

Aplicación móvil Android para la captura automatizada de datasets faciales utilizando detección de rostros con ML Kit y cámara frontal.

## 📋 Descripción

Esta aplicación permite capturar automáticamente 10 fotografías de una persona para crear datasets faciales. Utiliza detección de rostros en tiempo real con Google ML Kit para garantizar que las capturas sean de calidad, y sube las imágenes a un backend REST API.

### Características Principales

- **Detección de rostros en tiempo real**: Utiliza ML Kit para detectar automáticamente cuando el rostro está en posición correcta
- **Captura automatizada**: Captura automáticamente 10 fotos cuando detecta un rostro válido
- **Interfaz moderna**: UI/UX moderna con Jetpack Compose y Material Design 3
- **Overlay de guía**: Óvalo visual para guiar al usuario a posicionar su rostro correctamente
- **Gestión de permisos**: Sistema robusto de manejo de permisos de cámara
- **Sincronización con backend**: Envío automático de fotos al servidor REST API
- **Validación de usuarios**: Verifica si una persona ya existe en el sistema antes de crear el dataset

## 🛠 Stack Tecnológico

### Core
- **Kotlin**: 2.2.10
- **Android SDK**: Min SDK 24, Target SDK 36
- **Gradle**: 8.13.1 con Kotlin DSL

### UI Framework
- **Jetpack Compose**: UI declarativa moderna
- **Material Design 3**: Sistema de diseño de Google
- **Navigation Compose**: Navegación entre pantallas
- **Coil**: Carga de imágenes asíncrona

### Camera & ML
- **CameraX**: API moderna para captura de cámara
- **Google ML Kit**: Detección de rostros en tiempo real
- **Accompanist Permissions**: Manejo de permisos

### Networking
- **Retrofit 2**: Cliente HTTP para llamadas REST
- **OkHttp**: Cliente HTTP con logging interceptor
- **Gson**: Serialización/deserialización JSON

### Arquitectura
- **MVVM**: Model-View-ViewModel pattern
- **StateFlow**: Gestión de estado reactivo
- **Coroutines**: Programación asíncrona

## 📁 Estructura del Proyecto

```
mobile_captura_fotos/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/facialdataset/frontend/
│   │   │   │   ├── MainActivity.kt                 # Activity principal
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiClient.kt                # Configuración Retrofit
│   │   │   │   │   ├── ApiService.kt               # Endpoints REST API
│   │   │   │   │   └── models/
│   │   │   │   │       ├── Persona.kt              # Modelos de Persona
│   │   │   │   │       └── CapturaResponse.kt      # Respuesta de captura
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   └── OvalOverlay.kt          # Overlay guía de rostro
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── AppNavigation.kt       # Navegación Compose
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── MainScreen.kt          # Pantalla de inicio
│   │   │   │   │   │   ├── PermissionScreen.kt    # Manejo de permisos
│   │   │   │   │   │   ├── CameraScreen.kt        # Pantalla de cámara
│   │   │   │   │   │   └── PreviewScreen.kt       # Preview de fotos
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt               # Colores de la app
│   │   │   │   │   │   ├── Theme.kt               # Tema Compose
│   │   │   │   │   │   └── Type.kt                # Tipografía
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       └── CameraViewModel.kt     # ViewModel de cámara
│   │   │   │   └── utils/
│   │   │   │       └── ImageUtils.kt              # Utilidades de imagen
│   │   │   ├── res/                               # Recursos Android
│   │   │   └── AndroidManifest.xml                 # Manifest de la app
│   │   ├── androidTest/                           # Tests instrumentados
│   │   └── test/                                   # Tests unitarios
│   ├── build.gradle.kts                            # Configuración Gradle del módulo
│   └── proguard-rules.pro                          # Reglas de ofuscación
├── gradle/
│   ├── libs.versions.toml                          # Version catalog
│   └── wrapper/                                    # Gradle wrapper
├── build.gradle.kts                                # Configuración Gradle raíz
├── settings.gradle.kts                            # Configuración de settings
├── gradlew                                         # Script Gradle (Unix)
├── gradlew.bat                                     # Script Gradle (Windows)
└── .gitignore                                      # Archivos ignorados por Git
```

## 🚀 Configuración y Requisitos

### Requisitos Previos

- **Android Studio**: Koala | 2024.1.1 o superior
- **JDK**: 11 o superior
- **Android SDK**: API 36 (Android 15)
- **Gradle**: 8.13.1 (incluido en el proyecto)
- **Dispositivo Android**: API 24+ (Android 7.0) con cámara frontal

### Configuración del Backend

La aplicación está configurada para conectarse a un servidor backend. Para cambiar la URL:

1. Abre `app/src/main/java/com/facialdataset/frontend/api/ApiClient.kt`
2. Modifica la constante `BASE_URL`:

```kotlin
private const val BASE_URL = "https://tu-servidor.com"
```

**Nota**: Para pruebas con dispositivos físicos en red local, usa la IP de tu máquina de desarrollo:

```kotlin
private const val BASE_URL = "http://192.168.1.X:8000"
```

## 📦 Instalación

1. **Clona el repositorio**:
   ```bash
   git clone <repository-url>
   cd mobile_captura_fotos
   ```

2. **Abre el proyecto en Android Studio**:
   - File → Open → Selecciona el directorio del proyecto

3. **Sincroniza Gradle**:
   - Android Studio sincronizará automáticamente las dependencias
   - Si no, ejecuta: `File → Sync Project with Gradle Files`

4. **Configura el backend** (ver sección anterior)

5. **Ejecuta la aplicación**:
   - Conecta un dispositivo Android o inicia un emulador
   - Presiona el botón "Run" (▶) en Android Studio
   - O usa Gradle: `./gradlew installDebug`

## 🎯 Uso

### Flujo de la Aplicación

1. **Pantalla Principal**:
   - Ingresa el nombre completo de la persona
   - La aplicación verifica si la persona ya existe en el sistema
   - Si no existe, crea un nuevo registro

2. **Pantalla de Permisos**:
   - Solicita permiso de cámara si no está concedido
   - Muestra explicación si el usuario deniega el permiso

3. **Pantalla de Cámara**:
   - Muestra la vista de la cámara frontal
   - Detecta rostros automáticamente con ML Kit
   - Captura 10 fotos automáticamente cuando detecta un rostro válido
   - Muestra un contador de progreso
   - Guía visual con óvalo para posicionar el rostro

4. **Pantalla de Preview**:
   - Muestra las 10 fotos capturadas
   - Permite eliminar fotos individuales
   - Permite recapturar fotos
   - Envía todas las fotos al backend al confirmar

### API Endpoints

La aplicación se comunica con el backend mediante los siguientes endpoints:

#### Verificar Persona
```
GET /personas/verificar/{nombre}
```
**Respuesta**:
```json
{
  "existe": false,
  "persona_id": null,
  "total_fotos": 0
}
```

#### Crear Persona
```
POST /personas/
Content-Type: application/json

{
  "nombre": "Juan Pérez"
}
```
**Respuesta**:
```json
{
  "id": 123,
  "nombre": "Juan Pérez",
  "creado_en": "2024-01-01T00:00:00Z",
  "total_fotos": 0
}
```

#### Enviar Foto
```
POST /capturas/{persona_id}
Content-Type: multipart/form-data

imagen: <file>
```
**Respuesta**:
```json
{
  "mensaje": "Foto capturada exitosamente",
  "foto_id": 456,
  "ruta_archivo": "/path/to/foto.jpg",
  "total_capturas": 1,
  "limite_alcanzado": false
}
```

## 🏗 Arquitectura

### MVVM Pattern

La aplicación sigue el patrón MVVM (Model-View-ViewModel):

- **Model**: Clases de datos (`Persona`, `CapturaResponse`, etc.)
- **View**: Composables de Jetpack Compose (`MainScreen`, `CameraScreen`, etc.)
- **ViewModel**: `CameraViewModel` gestiona el estado y la lógica de negocio

### Flujo de Datos

```
User Input → Screen → ViewModel → API Client → Backend
                ↓           ↓
            StateFlow ← State Updates
```

### Gestión de Estado

El estado se gestiona mediante `StateFlow` en el ViewModel:

```kotlin
data class CameraUiState(
    val fotos: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val uploadSuccess: Boolean = false,
    val personaId: Int = -1
)
```

## 🔧 Configuración Adicional

### Permisos Android

La aplicación requiere los siguientes permisos (definidos en `AndroidManifest.xml`):

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

### Configuración de ProGuard

Las reglas de ProGuard están en `app/proguard-rules.pro` para proteger la aplicación en modo release.

### Configuración de Gradle

Las versiones de las dependencias se gestionan centralizadamente en `gradle/libs.versions.toml`:

```toml
[versions]
agp = "8.13.1"
kotlin = "2.2.10"
composeBom = "2026.02.01"
```

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
./gradlew test
```

### Ejecutar Tests Instrumentados
```bash
./gradlew connectedAndroidTest
```

## 📱 Build Variants

### Debug
```bash
./gradlew assembleDebug
```
Genera APK de debug en `app/build/outputs/apk/debug/`

### Release
```bash
./gradlew assembleRelease
```
Genera APK de release en `app/build/outputs/apk/release/`

## 🤝 Contribución

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es parte de un proyecto académico de la UNI.

## 👨‍💻 Autor

Desarrollado para el proyecto de captura de datasets faciales.

## 🐛 Issues

Para reportar bugs o sugerir mejoras, por favor abre un issue en el repositorio.

## 🔗 Recursos

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [ML Kit Face Detection](https://developers.google.com/ml-kit/vision/face-detection)
- [Retrofit Documentation](https://square.github.io/retrofit/)
