# 📋 Guía de Conexión a Base de Datos MySQL

## ✅ Pasos para Conectar tu Base de Datos

### 1️⃣ **Configurar los Parámetros de Conexión**

Abre el archivo: `src/main/java/com/example/project/DatabaseConnection.java`

Modifica las líneas 11-13 con los datos de TU base de datos:

```java
private static final String URL = "jdbc:mysql://localhost:3306/TU_BASE_DE_DATOS";
private static final String USER = "TU_USUARIO";
private static final String PASSWORD = "TU_CONTRASEÑA";
```

**Ejemplos:**

#### Si tu base de datos está en localhost (tu computadora):
```java
private static final String URL = "jdbc:mysql://localhost:3306/facturacion_db";
private static final String USER = "root";
private static final String PASSWORD = "micontraseña123";
```

#### Si tu base de datos está en un servidor remoto:
```java
private static final String URL = "jdbc:mysql://192.168.1.100:3306/facturacion_db";
private static final String USER = "usuario_bd";
private static final String PASSWORD = "contraseña_segura";
```

---

### 2️⃣ **Verificar que MySQL esté corriendo**

**En Windows:**
- Abre "Servicios" (presiona Win+R, escribe `services.msc` y Enter)
- Busca "MySQL" en la lista
- Asegúrate de que esté "En ejecución"

**Desde línea de comandos:**
```bash
mysql -u root -p
```
Te pedirá la contraseña. Si se conecta, MySQL está corriendo.

---

### 3️⃣ **Crear las Tablas Necesarias**

Tu base de datos necesita estas tablas. Ejecuta este SQL:

```sql
-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    cli_id INT PRIMARY KEY AUTO_INCREMENT,
    cli_cedula VARCHAR(20) UNIQUE NOT NULL,
    cli_apellidos VARCHAR(100) NOT NULL,
    cli_nombres VARCHAR(100) NOT NULL,
    cli_direccion VARCHAR(200),
    cli_telefono VARCHAR(20),
    cli_correo VARCHAR(100),
    cli_estado VARCHAR(20) DEFAULT 'Activo',
    cli_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    prod_id INT PRIMARY KEY AUTO_INCREMENT,
    prod_cod VARCHAR(50) UNIQUE NOT NULL,
    prod_nombre VARCHAR(200) NOT NULL,
    prod_pvp DECIMAL(10,2) NOT NULL,
    prod_stock INT DEFAULT 0,
    prod_estado VARCHAR(20) DEFAULT 'Activo',
    prod_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS facturas (
    fac_id INT PRIMARY KEY AUTO_INCREMENT,
    fac_numero VARCHAR(50) UNIQUE NOT NULL,
    fac_fecha DATE NOT NULL,
    fac_cliente_id INT NOT NULL,
    fac_subtotal DECIMAL(10,2) DEFAULT 0,
    fac_iva DECIMAL(10,2) DEFAULT 0,
    fac_descuento DECIMAL(10,2) DEFAULT 0,
    fac_total DECIMAL(10,2) NOT NULL,
    fac_estado VARCHAR(20) DEFAULT 'Activa',
    fac_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fac_cliente_id) REFERENCES clientes(cli_id)
);

-- Tabla de detalles de factura
CREATE TABLE IF NOT EXISTS factura_detalles (
    det_id INT PRIMARY KEY AUTO_INCREMENT,
    det_factura_id INT NOT NULL,
    det_producto_id INT NOT NULL,
    det_cantidad INT NOT NULL,
    det_precio_unitario DECIMAL(10,2) NOT NULL,
    det_aplica_iva BOOLEAN DEFAULT TRUE,
    det_descuento DECIMAL(5,2) DEFAULT 0,
    det_subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (det_factura_id) REFERENCES facturas(fac_id),
    FOREIGN KEY (det_producto_id) REFERENCES productos(prod_id)
);
```

**¿Cómo ejecutar este SQL?**

**Opción A - MySQL Workbench:**
1. Abre MySQL Workbench
2. Conecta a tu base de datos
3. Crea una nueva query (Ctrl+T)
4. Pega el SQL de arriba
5. Ejecuta (Ctrl+Enter o botón ⚡)

**Opción B - Línea de comandos:**
```bash
mysql -u root -p nombre_base_datos < script.sql
```

---

### 4️⃣ **Probar la Conexión**

La aplicación automáticamente intentará conectarse cuando inicies. Verás mensajes en la consola:

✅ **Conexión exitosa:**
```
Driver MySQL cargado correctamente
Conexión a la base de datos establecida exitosamente
Clientes cargados desde BD: X
Productos cargados desde BD: X
```

❌ **Error de conexión:**
```
Error al conectar con la base de datos: ...
Cargando datos de prueba en memoria...
```

---

### 5️⃣ **Agregar Datos de Prueba (Opcional)**

Si quieres tener datos iniciales para probar:

```sql
-- Insertar clientes de prueba
INSERT INTO clientes (cli_cedula, cli_apellidos, cli_nombres, cli_direccion, cli_telefono, cli_correo) VALUES
('2450128257', 'Melena', 'Diana', 'Santa Elena', '0963610580', 'diana.melena25@gmail.com'),
('1234567890', 'García', 'Carlos', 'Guayaquil', '0987654321', 'carlos.garcia@email.com'),
('0987654321', 'López', 'María', 'Quito', '0912345678', 'maria.lopez@email.com');

-- Insertar productos de prueba
INSERT INTO productos (prod_cod, prod_nombre, prod_pvp, prod_stock) VALUES
('LAP001', 'Laptop HP', 899.99, 50),
('MON001', 'Monitor Samsung', 299.99, 40),
('TEC001', 'Teclado Logitech', 49.99, 100),
('MOU001', 'Mouse Inalámbrico', 29.99, 80);
```

---

## 🔧 Problemas Comunes y Soluciones

### ❌ Error: "Access denied for user"
**Solución:** Usuario o contraseña incorrectos. Verifica en `DatabaseConnection.java`

### ❌ Error: "Unknown database"
**Solución:** La base de datos no existe. Créala primero:
```sql
CREATE DATABASE facturacion_db CHARACTER SET utf8mb4;
```

### ❌ Error: "Communications link failure"
**Solución:** 
- MySQL no está corriendo
- El puerto 3306 está bloqueado por firewall
- La IP/host es incorrecta

### ❌ Error: "Table doesn't exist"
**Solución:** Ejecuta el script SQL del paso 3 para crear las tablas

---

## 🎯 Resumen Rápido

1. ✏️ Modifica `DatabaseConnection.java` líneas 11-13
2. ✅ Verifica que MySQL esté corriendo
3. 📝 Ejecuta el SQL para crear las tablas
4. ▶️ Ejecuta tu aplicación
5. 👀 Revisa la consola para ver el estado de conexión

---

## 💡 Ventajas del Sistema

- ✅ **Fallback automático**: Si no puede conectar a BD, usa datos en memoria
- ✅ **Sincronización automática**: Los datos se guardan inmediatamente
- ✅ **Búsqueda optimizada**: Consultas SQL rápidas
- ✅ **Persistencia**: Los datos no se pierden al cerrar la app

---

## 📞 ¿Necesitas Ayuda?

Si tienes errores específicos, copia el mensaje de error completo de la consola para diagnosticar el problema.


