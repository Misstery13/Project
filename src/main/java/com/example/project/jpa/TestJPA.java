package com.example.project.jpa;

import com.example.project.entities.ClienteEntity;
import com.example.project.repositories.ClienteRepository;
import jakarta.persistence.EntityManager;

/**
 * Clase de prueba para verificar que JPA funciona correctamente
 */
public class TestJPA {
    
    public static void probarConexionJPA() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  PRUEBA DE CONEXIÓN JPA");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            // 1. Probar EntityManagerFactory
            System.out.println("1. Creando EntityManagerFactory...");
            JPAUtil.getEntityManagerFactory();
            System.out.println("   ✓ EntityManagerFactory creado\n");
            
            // 2. Probar EntityManager
            System.out.println("2. Creando EntityManager...");
            EntityManager em = JPAUtil.getEntityManager();
            System.out.println("   ✓ EntityManager creado");
            System.out.println("   - Está abierto: " + em.isOpen());
            System.out.println("   - Tiene transacción activa: " + em.getTransaction().isActive() + "\n");
            
            // 3. Probar consulta simple
            System.out.println("3. Probando consulta simple...");
            long count = em.createQuery("SELECT COUNT(c) FROM ClienteEntity c", Long.class).getSingleResult();
            System.out.println("   ✓ Total de clientes en BD: " + count + "\n");
            
            // 4. Probar repositorio
            System.out.println("4. Probando ClienteRepository...");
            ClienteRepository repo = new ClienteRepository();
            var todos = repo.findAll();
            System.out.println("   ✓ Clientes encontrados: " + todos.size());
            if (!todos.isEmpty()) {
                ClienteEntity primero = todos.get(0);
                System.out.println("   - Primer cliente: " + primero.getNombres() + " " + primero.getApellidos());
            }
            System.out.println();
            
            System.out.println("═══════════════════════════════════════════════════");
            System.out.println("  ✓ JPA FUNCIONANDO CORRECTAMENTE");
            System.out.println("═══════════════════════════════════════════════════\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR EN PRUEBA JPA:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n═══════════════════════════════════════════════════\n");
        }
    }
    
    public static void main(String[] args) {
        // Primero verificar datos existentes con JDBC
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  PASO 1: VERIFICACIÓN CON JDBC");
        System.out.println("═══════════════════════════════════════════════════");
        VerificarDatosExistentes.verificarDatosExistentes();
        
        // Luego probar JPA
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  PASO 2: VERIFICACIÓN CON JPA");
        System.out.println("═══════════════════════════════════════════════════");
        probarConexionJPA();
        
        // Verificar que JPA puede leer los datos
        System.out.println("\n\n═══════════════════════════════════════════════════");
        System.out.println("  PASO 3: COMPARACIÓN JDBC vs JPA");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            EntityManager em = JPAUtil.getEntityManager();
            long countJPA = em.createQuery("SELECT COUNT(c) FROM ClienteEntity c", Long.class)
                .getSingleResult();
            
            System.out.println("✓ Total de clientes según JPA: " + countJPA);
            
            if (countJPA > 0) {
                System.out.println("\n✓ JPA puede leer los datos existentes correctamente!");
                System.out.println("\nMostrando primeros 3 clientes con JPA:");
                ClienteRepository repo = new ClienteRepository();
                var clientes = repo.findAll();
                int max = Math.min(3, clientes.size());
                for (int i = 0; i < max; i++) {
                    var c = clientes.get(i);
                    System.out.printf("   %d. %s %s (Cédula: %s, ID: %d)%n", 
                        i + 1, c.getNombres(), c.getApellidos(), c.getCedula(), c.getId());
                }
            } else {
                System.out.println("\n⚠ JPA no encuentra clientes. Verifica el mapeo de la tabla.");
            }
        } catch (Exception e) {
            System.err.println("✗ Error al verificar datos con JPA: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

