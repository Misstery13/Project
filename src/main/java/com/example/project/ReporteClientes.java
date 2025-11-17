package com.example.project;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para generar reportes de clientes usando JasperReports
 */
public class ReporteClientes {
    
    /**
     * Genera y muestra el reporte de clientes
     * @return true si se generó correctamente, false en caso contrario
     */
    public static boolean generarReporte() {
        try {
            System.out.println("=== Generando Reporte de Clientes ===");
            
            // Obtener conexión a la base de datos
            Connection connection = DatabaseConnection.getInstance().getConnection();
            if (connection == null) {
                System.err.println("✗ Error: No se pudo obtener conexión a la base de datos");
                return false;
            }
            System.out.println("✓ Conexión a BD establecida");
            
            // Compilar el reporte desde .jrxml para usar las rutas actualizadas de las imágenes
            System.out.println("Compilando reporte desde .jrxml (para usar rutas actualizadas de imágenes)...");
            InputStream jrxmlStream = ReporteClientes.class.getResourceAsStream("/Reportes/Coffee_Landscape.jrxml");
            if (jrxmlStream == null) {
                System.err.println("✗ Error: No se encontró el archivo Coffee_Landscape.jrxml en /Reportes/");
                return false;
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            jrxmlStream.close();
            System.out.println("✓ Reporte compilado desde .jrxml con rutas actualizadas");
            
            // Parámetros del reporte
            Map<String, Object> parameters = new HashMap<>();
            
            // Cargar las imágenes desde el classpath y pasarlas como parámetros
            // Esto evita que JasperReports busque las imágenes en su repositorio
            try {
                InputStream coffeeImg = ReporteClientes.class.getResourceAsStream("/Reportes/coffee.jpg");
                if (coffeeImg != null) {
                    System.out.println("✓ Imagen coffee.jpg encontrada, cargando...");
                    // Leer la imagen como BufferedImage
                    BufferedImage coffeeBufferedImage = ImageIO.read(coffeeImg);
                    coffeeImg.close();
                    if (coffeeBufferedImage != null) {
                        // Pasar directamente el BufferedImage como parámetro
                        parameters.put("coffee.jpg", coffeeBufferedImage);
                        System.out.println("✓ Imagen coffee.jpg cargada como parámetro");
                    }
                } else {
                    System.out.println("⚠ Imagen coffee.jpg no encontrada en /Reportes/");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error al cargar coffee.jpg: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                InputStream coffeeStainImg = ReporteClientes.class.getResourceAsStream("/Reportes/coffee_stain.png");
                if (coffeeStainImg != null) {
                    System.out.println("✓ Imagen coffee_stain.png encontrada, cargando...");
                    // Leer la imagen como BufferedImage
                    BufferedImage coffeeStainBufferedImage = ImageIO.read(coffeeStainImg);
                    coffeeStainImg.close();
                    if (coffeeStainBufferedImage != null) {
                        // Pasar directamente el BufferedImage como parámetro
                        parameters.put("coffee_stain.png", coffeeStainBufferedImage);
                        System.out.println("✓ Imagen coffee_stain.png cargada como parámetro");
                    }
                } else {
                    System.out.println("⚠ Imagen coffee_stain.png no encontrada en /Reportes/");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error al cargar coffee_stain.png: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Configurar el contexto para manejar imágenes faltantes
            JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
            
            // Configurar para que muestre espacio en blanco si no encuentra imágenes
            jasperReportsContext.setProperty("net.sf.jasperreports.image.load.on.error.type", "Blank");
            jasperReportsContext.setProperty("net.sf.jasperreports.image.load.on.error", "true");
            
            // Configurar propiedades del sistema para manejar imágenes faltantes
            System.setProperty("net.sf.jasperreports.image.load.on.error.type", "Blank");
            System.setProperty("net.sf.jasperreports.image.load.on.error", "true");
            
            // Llenar el reporte con datos de la base de datos
            System.out.println("Llenando reporte con datos de la BD...");
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                connection
            );
            System.out.println("✓ Reporte llenado con " + jasperPrint.getPages().size() + " página(s)");
            
            // Mostrar el reporte en un visor
            System.out.println("Abriendo visor de reportes...");
            JasperViewer.viewReport(jasperPrint, false);
            System.out.println("✓ Reporte mostrado correctamente");
            
            // Cerrar la conexión
            connection.close();
            
            return true;
            
        } catch (JRException e) {
            System.err.println("✗ Error de JasperReports: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Genera el reporte y lo guarda como PDF
     * @param rutaArchivo Ruta completa donde guardar el PDF
     * @return true si se generó correctamente, false en caso contrario
     */
    public static boolean generarReportePDF(String rutaArchivo) {
        try {
            System.out.println("=== Generando Reporte de Clientes como PDF ===");
            
            // Obtener conexión a la base de datos
            Connection connection = DatabaseConnection.getInstance().getConnection();
            if (connection == null) {
                System.err.println("✗ Error: No se pudo obtener conexión a la base de datos");
                return false;
            }
            
            // Cargar el archivo .jasper compilado
            InputStream jasperStream = ReporteClientes.class.getResourceAsStream("/Reportes/Coffee_Landscape.jasper");
            if (jasperStream == null) {
                System.err.println("✗ Error: No se encontró el archivo Coffee_Landscape.jasper");
                return false;
            }
            
            // Cargar el reporte compilado
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            
            // Parámetros del reporte
            Map<String, Object> parameters = new HashMap<>();
            
            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                connection
            );
            
            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, rutaArchivo);
            System.out.println("✓ Reporte PDF generado en: " + rutaArchivo);
            
            // Cerrar la conexión
            connection.close();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error al generar reporte PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

