# Ejemplo de Uso - Sistema Multidioma

## Cómo Probar el Sistema

### 1. Ejecutar la Aplicación
```bash
# Usando Maven wrapper
.\mvnw.cmd javafx:run

# O usando Java directamente
java -cp target/classes com.example.project.HelloApplication
```

### 2. Cambiar Idioma
1. **Al iniciar**: La aplicación se abre en español por defecto
2. **Cambiar a inglés**: 
   - Hacer clic en el menú "Idioma" en la barra superior
   - Seleccionar "English"
3. **Cambiar a español**:
   - Hacer clic en el menú "Language" (ahora en inglés)
   - Seleccionar "Español"

### 3. Elementos que se Traducen

#### Menú Principal
- **Español**: Administración, Proceso, Reportes, Idioma
- **Inglés**: Administration, Process, Reports, Language

#### Elementos del Menú
- **Español**: Pantalla 1, Pantalla 2, Eliminar, Acerca de
- **Inglés**: Screen 1, Screen 2, Delete, About

#### Panel Izquierdo
- **Español**: Administración, Proceso, Reportes
- **Inglés**: Administration, Process, Reports

#### Botones
- **Español**: Clientes, Pantalla 2, Tabla
- **Inglés**: Clients, Screen 2, Table

#### Barra Inferior
- **Español**: Usuario:, Fecha:, Versión:, Hora:
- **Inglés**: User:, Date:, Version:, Time:

#### Título de Ventana
- **Español**: Sistema - Programación Visual
- **Inglés**: System - Visual Programming

## Características del Sistema

### ✅ Funcionalidades Implementadas
- [x] Cambio de idioma en tiempo real
- [x] No requiere reiniciar la aplicación
- [x] Traducción completa de la interfaz
- [x] Menú dedicado para cambio de idioma
- [x] Sistema extensible para más idiomas

### 🔧 Cómo Extender el Sistema

#### Agregar un Nuevo Idioma (ejemplo: Francés)

1. **Crear archivo de propiedades**:
```properties
# messages_fr.properties
menu.administration=Administration
menu.process=Processus
menu.reports=Rapports
# ... más traducciones
```

2. **Modificar LanguageManager.java**:
```java
public static void changeLanguage(String language) {
    Locale newLocale;
    switch (language.toLowerCase()) {
        case "fr":
        case "french":
            newLocale = new Locale("fr");
            break;
        case "en":
        case "english":
            newLocale = new Locale("en");
            break;
        case "es":
        case "spanish":
        default:
            newLocale = new Locale("es");
            break;
    }
    loadLanguage(newLocale);
}
```

3. **Agregar opción al menú**:
```xml
<MenuItem fx:id="menu_french" mnemonicParsing="false" onAction="#acc_changeToFrench" text="Français" />
```

#### Agregar Nueva Traducción

1. **Agregar clave en todos los archivos de propiedades**:
```properties
# messages.properties, messages_es.properties, messages_en.properties
new.element=Nuevo Elemento
new.element=New Element
```

2. **Modificar el método correspondiente en LanguageManager**:
```java
// En applyLanguageToMenuBar o el método apropiado
switch (element.getText()) {
    case "Nuevo Elemento":
    case "New Element":
        element.setText(getMessage("new.element"));
        break;
}
```

## Notas Importantes

- **Idioma por defecto**: Español
- **Cambio instantáneo**: No requiere reiniciar la aplicación
- **Fallback**: Si no encuentra una traducción, muestra la clave
- **Extensibilidad**: Fácil agregar nuevos idiomas y traducciones

## Autor
Diana Melena - Sistema Multidioma JavaFX
