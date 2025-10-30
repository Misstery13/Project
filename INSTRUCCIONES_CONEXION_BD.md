# 📋 Guía de Conexión a Base de Datos SQL Server

## 🔐 Autenticación de Windows (Sin Contraseña)

Tu aplicación está configurada para usar **SQL Server con autenticación integrada de Windows**. Esto significa que no necesitas usuario ni contraseña: SQL Server usa tus credenciales de Windows automáticamente.

### ✅ Configuración Actual

Abre el archivo: `src/main/java/com/example/project/DatabaseConnection.java`

**Configuración actual (línea 26):**
```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bdFactura;integratedSecurity=true";
```

**NO necesitas USER ni PASSWORD** porque `integratedSecurity=true` usa tu usuario de Windows.

### 📝 Cómo Verificar tu Configuración Actual

**Opción 1 - Desde el código:**
Llama al método `DatabaseConnection.mostrarConfiguracion()` en cualquier parte de tu código para ver la configuración actual.

**Opción 2 - Probar la conexión:**
Ejecuta el método `main` de `DatabaseConnection` para probar la conexión:
```bash
# Desde la terminal en el directorio del proyecto
mvn exec:java -Dexec.mainClass="com.example.project.DatabaseConnection"
```

**Opción 3 - Verificar manualmente en SQL Server:**
1. Abre **SQL Server Management Studio (SSMS)**
2. En "Tipo de servidor", selecciona "Motor de base de datos"
3. En "Nombre del servidor", escribe `localhost` o `localhost\NOMBRE_INSTANCIA`
4. En "Autenticación", selecciona "Autenticación de Windows"
5. Haz clic en "Conectar"

### 🎯 ¿Cuál es mi URL, Usuario y Contraseña?

1. **URL:** `jdbc:sqlserver://localhost:1433;databaseName=bdFactura;integratedSecurity=true`
   - `localhost` = servidor local (tu computadora)
   - Si tu instancia tiene nombre: usa `localhost\NOMBRE_INSTANCIA` (ejemplo: `localhost哈哈\SQLEXPRESS`)
   - `1433` = puerto por defecto de SQL Server
   - `bdFactura` = nombre de tu base de datos
   - `integratedSecurity=true` = usa autenticación de Windows
   
   **Para encontrar el nombre de tu instancia SQL Server:**
   - Abre "Servicios" de Windows (Win+R → `services.msc`)
   - Busca "SQL Server (NOMBRE_INSTANCIA)"
   - Ejemplo: "SQL Server (SQLEXPRESS)" → el nombre es `SQLEXPRESS`
   
   **Para verificar si tu base de datos existe:**
   ```sql
   SELECT name FROM sys.databases;
   ```

2. **Usuario:** NO necesitas especificarlo
   - SQL Server usa automáticamente tu usuario de Windows actual
   - Ejemplo: si tu usuario de Windows es "DESKTOP-980N1BK\\Usuario", SQL Server lo usará

3. **Contraseña:** NO necesitas especificarla
   - Con `integratedSecurity=true`, SQL Server usa tu sesión de Windows

---

## ✅ Pasos para Conectar tu Base de Datos

### 1️⃣ **Verificar que SQL Server esté corriendo**

**En Windows:**
- Presiona `Win+R`, escribe `services.msc` y presiona Enter
- Busca "SQL Server" en la lista
- Asegúrate de que esté "En ejecución"
- Si ves múltiples servicios, busca el que corresponde a tu instancia
  - Ejemplo: "SQL Server (SQLEXPRESS)" o "SQL Server (MSSQLSERVER)"

### 2️⃣ **Verificar la configuración de la URL**

Abre `src/main/java/com/example/project/DatabaseConnection.java` y verifica la línea 26:

**Si tu instancia es la predeterminada (sin nombre):**
```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bdFactura;integratedSecurity=true";
```

**Si tu instancia tiene nombre (ejemplo: SQLEXPRESS):**
```java
private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=bdFactura;integratedSecurity=true";
```

**Nota:** Usa doble barra invertida `\\` para el nombre de instancia en la URL JDBC.

### 3️⃣ **Crear la Base de Datos (si no existe)**

Abre SQL Server Management Studio (SSMS) y ejecuta:
```sql
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'bdFactura')
BEGIN
    CREATE DATABASE bdFactura;
END
```

O desde la línea de comandos:
```bash
sqlcmd -S localhost -E -Q "CREATE DATABASE bdFactura"
```

### 4️⃣ **Crear las Tablas Necesarias**

Tu aplicación puede crear las tablas automáticamente cuando se ejecute, o puedes crearlas manualmente:

**Opción A - Automático:**
Ejecuta tu aplicación y llama al método:
```java
DatabaseConnection.getInstance().crearTablasNecesarias();
```

**Opción B - Manualmente desde SSMS:**
Las tablas se crearán con la sintaxis SQL Server (IDENTITY en lugar de AUTO_INCREMENT, etc.)

### 5️⃣ **Verificar Permisos**

Asegúrate de que tu usuario de Windows tenga permisos en SQL Server:

**Desde SSMS:**
1. Conecta como administrador
2. Expande "Seguridad" → "Inicios de sesión"
3. Si tu usuario de Windows no aparece:
   - Click derecho en "Inicios de sesión" → "Nuevo inicio de sesión"
   - Selecciona "Buscar..." y busca tu usuario de Windows
   - En "Roles de servidor", marca "sysadmin" (o al menos "dbcreator" y "public")
   - En "Mapeo de usuario", marca la base de datos `bdFactura` y otorga permisos

---

## 🔧 Problemas Comunes y Soluciones

### ❌ Error: "This driver is not configured for picks up security"

**Solución:** Necesitas el archivo `sqljdbc_auth.dll`:
1. Descarga el driver JDBC de SQL Server desde Microsoft
2. Extrae `sqljdbc_auth.dll` (está en la carpeta `sqljdbc_X.X\enu\auth\x64` o `x86`)
3. Copia el archivo a una carpeta en tu PATH (ejemplo: `C:\Windows\System32`)
4. O especifica la ruta: `-Djava.library.path=C:\ruta\a\sqljdbc_auth.dll`

### ❌ Error: "Cannot open database 'bdFactura' requested by the login"

**Solución:** 
1. La base de datos no existe → Créala (paso 3)
2. Tu usuario no tiene permisos → Otorga permisos (paso 5)

### ❌ Error: "Login failed for user"

**Solución:**
1. Asegúrate de que SQL Server esté configurado para aceptar autenticación de Windows:
   - Abre SSMS
   - Click derecho en el servidor → "Propiedades" → "Seguridad"
   - Marca "Autenticación de Windows y autenticación de SQL Server"

### ❌ Error: "Communications link failure" o "Connection refused"

**Solución:**
1. SQL Server no está corriendo → Verifica los servicios (paso 1)
2. El puerto está bloqueado → Verifica el firewall
3. La instancia tiene un nombre diferente → Actualiza la URL con el nombre correcto

### ❌ Error: "The server was not found or was not accessible"

**Solución:**
1. Verifica el nombre de la instancia en la URL
2. Si usas instancia nombrada, usa `localhost\\INSTANCIA` (con doble barra)
3. Prueba conectarte desde SSMS primero para verificar el nombre correcto

---

## 🎯 Resumen Rápido

1. ✅ Verifica que SQL Server esté corriendo (Servicios de Windows)
2. ✏️ Ajusta la URL en `DatabaseConnection.java` si tu instancia tiene nombre
3. 📝 Crea la base de datos `bdFactura` si no existe
4. 🔐 Asegúrate de tener permisos en SQL Server
5. ▶️ Ejecuta tu aplicación
6. 👀 Revisa la consola para ver el estado de conexión

---

## 💡 Notas Importantes

- ✅ **Autenticación integrada de Windows:** La forma más segura de conectar con SQL Server
- ✅ **No necesitas contraseña:** SQL Server usa tu sesión de Windows
- ✅ **Usuario automático:** SQL Server identifica tu usuario de Windows automáticamente
- ⚠️ **sqljdbc_auth.dll:** Puede ser necesario para autenticación integrada en algunos sistemas

---

## 📞 ¿Necesitas Ayuda?

Si tienes errores específicos:
1. Copia el mensaje de error completo de la consola
2. Verifica que SQL Server Management Studio se conecte correctamente
3. Revisa los logs de SQL Server en "Visor de eventos" de Windows

---

## 🔗 Recursos Adicionales

- [Documentación oficial del driver JDBC de SQL Server](https://docs.microsoft.com/en-us/sql/connect/jdbc/)
- [Descargar SQL Server Management Studio (SSMS)](https://docs.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms)
- [Descargar el driver JDBC de Microsoft](https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server)
