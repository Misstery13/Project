# 🔧 Cómo Habilitar TCP/IP en SQL Server

## ⚠️ Problema Actual

SQL Server está corriendo pero **TCP/IP está deshabilitado**, por lo que JDBC no puede conectarse. Actualmente solo acepta conexiones por memoria compartida (shared memory).

## ✅ Solución: Habilitar TCP/IP

### Método 1: SQL Server Configuration Manager (Recomendado)

1. **Abre SQL Server Configuration Manager:**
   - Presiona `Win + R`
   - Escribe: `SQLServerManager16.msc` (o `SQLServerManager15.msc` si es SQL Server 2019)
   - Presiona Enter

2. **Habilita TCP/IP:**
   - En el panel izquierdo, expande **"Configuración de red de SQL Server"**
   - Click en **"Protocolos para DIANAMAIN"** (o para MSSQLSERVER si usas la instancia predeterminada)
   
3. **En el panel derecho:**
   - Click derecho en **"TCP/IP"**
   - Selecciona **"Habilitar"**

4. **Configura el puerto TCP/IP:**
   - Click derecho en **"TCP/IP"** → **"Propiedades"**
   - Ve a la pestaña **"Direcciones IP"**
   - Baja hasta **"IPAll"** (al final de la lista)
   - En **"Puerto TCP"**, escribe: `1433` (o déjalo en blanco para puerto dinámico)
   - Elimina cualquier valor de **"Puerto TCP dinámico"** si quieres un puerto fijo
   - Click en **"Aceptar"**

5. **Reinicia SQL Server:**
   - Ve a **"Servicios de SQL Server"** en el panel izquierdo
   - Click derecho en **"SQL Server (DIANAMAIN)"** (o MSSQLSERVER)
   - Selecciona **"Reiniciar"**

### Verificar que TCP/IP está Habilitado

Después de habilitar TCP/IP, verifica que el puerto está escuchando:

```powershell
netstat -ano | findstr "1433"
```

Deberías ver algo como:
```
TCP    0.0.0.0:1433         0.0.0.0:0              LISTENING       [PID]
```

## 📝 Notas Importantes

- ⚠️ **Se requieren permisos de Administrador** para cambiar la configuración de SQL Server
- 🔄 **Debes reiniciar SQL Server** después de habilitar TCP/IP
- 🔥 **Verifica el Firewall**: Asegúrate de que Windows Firewall permite conexiones en el puerto 1433
- ✅ **Después de habilitar TCP/IP**, tu aplicación Java debería poder conectarse

## 🔗 Referencias

- [Microsoft Docs: Habilitar y configurar TCP/IP](https://docs.microsoft.com/sql/tools/configuration-manager/tcp-ip-properties-configure-ip-addresses)

