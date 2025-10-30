# Script para iniciar SQL Server Browser
# Ejecuta este script como Administrador

Write-Host "Intentando iniciar SQL Server Browser..." -ForegroundColor Yellow

try {
    $service = Get-Service -Name "SQLBrowser" -ErrorAction Stop
    if ($service.Status -eq "Running") {
        Write-Host "✓ SQL Server Browser ya está corriendo" -ForegroundColor Green
    } else {
        Write-Host "Iniciando SQL Server Browser..." -ForegroundColor Yellow
        Start-Service -Name "SQLBrowser" -ErrorAction Stop
        Start-Sleep -Seconds 2
        $service.Refresh()
        if ($service.Status -eq "Running") {
            Write-Host "✓ SQL Server Browser iniciado exitosamente" -ForegroundColor Green
        } else {
            Write-Host "✗ No se pudo iniciar SQL Server Browser" -ForegroundColor Red
            Write-Host "  El servicio puede requerir configuración adicional" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
    Write-Host "" 
    Write-Host "SOLUCIONES:" -ForegroundColor Yellow
    Write-Host "1. Ejecuta PowerShell como Administrador" -ForegroundColor White
    Write-Host "2. O habilita TCP/IP con un puerto fijo en SQL Server Configuration Manager" -ForegroundColor White
    Write-Host "3. Ver instrucciones en: HABILITAR_TCP_IP_SQL_SERVER.md" -ForegroundColor White
}

Write-Host ""
Write-Host "Estado actual del servicio:" -ForegroundColor Cyan
Get-Service -Name "SQLBrowser" | Format-Table Name, Status, DisplayName -AutoSize

