package com.example.project.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilidad para gestionar EntityManager y EntityManagerFactory de JPA
 */
public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "bdFacturaPU";
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    /**
     * Obtiene el EntityManagerFactory (Singleton)
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            try {
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                System.out.println("✓ EntityManagerFactory creado exitosamente");
            } catch (Exception e) {
                System.err.println("✗ Error al crear EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("No se pudo inicializar JPA", e);
            }
        }
        return emf;
    }

    /**
     * Obtiene un EntityManager para el thread actual
     */
    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen()) {
            try {
                EntityManagerFactory factory = getEntityManagerFactory();
                em = factory.createEntityManager();
                threadLocal.set(em);
                System.out.println("  [JPAUtil] EntityManager creado para thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                System.err.println("  [JPAUtil] ERROR al crear EntityManager: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("No se pudo crear EntityManager", e);
            }
        }
        return em;
    }

    /**
     * Cierra el EntityManager del thread actual
     */
    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null && em.isOpen()) {
            em.close();
            threadLocal.remove();
        }
    }

    /**
     * Cierra el EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        closeEntityManager();
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            System.out.println("EntityManagerFactory cerrado");
        }
    }

    /**
     * Inicia una transacción
     */
    public static void beginTransaction() {
        EntityManager em = getEntityManager();
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    /**
     * Hace commit de la transacción actual
     */
    public static void commit() {
        EntityManager em = getEntityManager();
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    /**
     * Hace rollback de la transacción actual
     */
    public static void rollback() {
        EntityManager em = getEntityManager();
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}

