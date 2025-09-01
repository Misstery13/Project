# Sistema Multidioma - JavaFX Application

## Descripción
Este proyecto implementa un sistema multidioma completo para una aplicación JavaFX, permitiendo cambiar entre español e inglés desde el menú de la aplicación.

## Características Implementadas

### 1. Archivos de Propiedades
- `messages.properties` - Archivo por defecto (español)
- `messages_es.properties` - Archivo específico para español
- `messages_en.properties` - Archivo específico para inglés

### 2. Clase LanguageManager
La clase `LanguageManager` maneja toda la lógica de internacionalización:
- Carga de archivos de propiedades
- Cambio de idioma en tiempo real
- Aplicación automática de traducciones a la interfaz

### 3. Menú de Idioma
Se agregó un nuevo menú "Idioma" con opciones:
- Español
- English

### 4. Traducciones Incluidas
- Menús principales (Administración, Proceso, Reportes)
- Elementos del menú (Pantalla 1, Pantalla 2, Eliminar, Acerca de)
- Botones del panel izquierdo (Clientes, Pantalla 2, Tabla)
- Títulos de paneles (Administración, Proceso, Reportes)
- Etiquetas de la barra inferior (Usuario, Fecha, Versión, Hora)
- Título de la ventana

## Cómo Usar

### Cambiar Idioma
1. Ejecutar la aplicación
2. Ir al menú "Idioma" en la barra superior
3. Seleccionar "Español" o "English"
4. La interfaz se actualizará automáticamente

### Agregar Nuevos Idiomas
1. Crear un nuevo archivo `messages_[código_idioma].properties`
2. Agregar las traducciones correspondientes
3. Modificar el método `changeLanguage()` en `LanguageManager` para incluir el nuevo idioma

### Agregar Nuevas Traducciones
1. Agregar la clave en todos los archivos de propiedades
2. Modificar el método correspondiente en `LanguageManager` para aplicar la traducción

## Estructura de Archivos

```
src/main/resources/
├── messages.properties          # Archivo por defecto
├── messages_es.properties       # Español
└── messages_en.properties       # Inglés

src/main/java/com/example/project/
├── LanguageManager.java         # Clase principal de internacionalización
├── HelloController.java         # Controlador modificado
└── HelloApplication.java        # Aplicación principal modificada
```

## Autor
Diana Melena

## Notas Técnicas
- El sistema usa `ResourceBundle` de Java para manejar las traducciones
- Las traducciones se aplican dinámicamente sin necesidad de reiniciar la aplicación
- El idioma por defecto es español
- El sistema es extensible para agregar más idiomas fácilmente
