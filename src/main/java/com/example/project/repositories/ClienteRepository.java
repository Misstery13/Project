package com.example.project.repositories;

import com.example.project.entities.ClienteEntity;
import com.example.project.jpa.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones con ClienteEntity
 */
public class ClienteRepository {
    
    /**
     * Guarda o actualiza un cliente
     */
    public ClienteEntity save(ClienteEntity cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            JPAUtil.beginTransaction();
            if (cliente.getId() == null) {
                em.persist(cliente);
            } else {
                cliente = em.merge(cliente);
            }
            JPAUtil.commit();
            return cliente;
        } catch (Exception e) {
            JPAUtil.rollback();
            System.err.println("Error al guardar cliente: " + e.getMessage());
            throw new RuntimeException("Error al guardar cliente", e);
        }
    }

    /**
     * Busca un cliente por ID
     */
    public Optional<ClienteEntity> findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ClienteEntity cliente = em.find(ClienteEntity.class, id);
            return Optional.ofNullable(cliente);
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca un cliente por cédula exacta
     */
    public Optional<ClienteEntity> findByCedula(String cedula) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE c.cedula = :cedula AND (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL)",
                ClienteEntity.class
            );
            query.setParameter("cedula", cedula);
            List<ClienteEntity> resultados = query.getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por cédula: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca clientes por cédula (búsqueda parcial - contiene el texto)
     */
    public List<ClienteEntity> findByCedulaPartial(String cedula) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + cedula.trim() + "%";
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE c.cedula LIKE :pattern AND (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL) ORDER BY c.nombres",
                ClienteEntity.class
            );
            query.setParameter("pattern", searchPattern);
            List<ClienteEntity> resultados = query.getResultList();
            System.out.println("✓ ClienteRepository.findByCedulaPartial('" + cedula + "') encontró " + resultados.size() + " clientes");
            return resultados;
        } catch (Exception e) {
            System.err.println("Error al buscar por cédula parcial: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Busca todos los clientes activos
     */
    public List<ClienteEntity> findAll() {
        EntityManager em = null;
        try {
            System.out.println("  [Repository] Obteniendo EntityManager...");
            em = JPAUtil.getEntityManager();
            System.out.println("  [Repository] EntityManager obtenido: " + (em != null ? "OK" : "NULL"));
            System.out.println("  [Repository] EntityManager abierto: " + (em != null && em.isOpen()));
            
            System.out.println("  [Repository] Ejecutando consulta JPQL...");
            // Buscar clientes activos o con estado NULL (acepta 'A' o 'Activo')
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL) ORDER BY c.nombres",
                ClienteEntity.class
            );
            
            System.out.println("  [Repository] Obteniendo resultados...");
            List<ClienteEntity> resultados = query.getResultList();
            System.out.println("✓ ClienteRepository.findAll() encontró " + resultados.size() + " clientes");
            
            if (resultados.isEmpty()) {
                System.out.println("  [ADVERTENCIA] No se encontraron clientes activos en la BD");
                System.out.println("  [DEBUG] Verificando si hay clientes en la tabla...");
                // Consulta adicional para verificar con JPQL
                try {
                    Long total = em.createQuery("SELECT COUNT(c) FROM ClienteEntity c", Long.class).getSingleResult();
                    System.out.println("  [DEBUG] Total de clientes en tabla (todos los estados): " + total);
                    Long activos = em.createQuery("SELECT COUNT(c) FROM ClienteEntity c WHERE c.estado = 'A' OR c.estado = 'Activo'", Long.class).getSingleResult();
                    System.out.println("  [DEBUG] Total de clientes activos: " + activos);
                } catch (Exception e2) {
                    System.err.println("  [DEBUG] Error en consulta COUNT: " + e2.getMessage());
                }
                
                // Intentar consulta SQL nativa directa
                try {
                    System.out.println("  [DEBUG] Intentando consulta SQL nativa...");
                    @SuppressWarnings("unchecked")
                    List<Object[]> nativeResults = em.createNativeQuery("SELECT cli_id, cli_cedula, cli_nombres, cli_apellidos, cli_estado FROM cliente").getResultList();
                    System.out.println("  [DEBUG] Consulta SQL nativa encontró " + nativeResults.size() + " registros");
                    for (Object[] row : nativeResults) {
                        System.out.println("    - ID: " + row[0] + ", Cédula: " + row[1] + ", Nombre: " + row[2] + " " + row[3] + ", Estado: " + row[4]);
                    }
                } catch (Exception e3) {
                    System.err.println("  [DEBUG] Error en consulta SQL nativa: " + e3.getMessage());
                    e3.printStackTrace();
                }
            }
            
            return resultados;
        } catch (Exception e) {
            System.err.println("✗ Error al buscar todos los clientes: " + e.getMessage());
            System.err.println("  Tipo de error: " + e.getClass().getName());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Busca clientes por criterio (cédula, nombres, apellidos)
     */
    public List<ClienteEntity> search(String criterio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + criterio.trim() + "%";
            // JPQL usa CONCAT en lugar de + para concatenar
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE " +
                "(c.cedula LIKE :pattern OR c.nombres LIKE :pattern OR c.apellidos LIKE :pattern " +
                "OR CONCAT(CONCAT(c.nombres, ' '), c.apellidos) LIKE :pattern) " +
                "AND (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL) " +
                "ORDER BY c.nombres",
                ClienteEntity.class
            );
            query.setParameter("pattern", searchPattern);
            List<ClienteEntity> resultados = query.getResultList();
            System.out.println("✓ ClienteRepository.search('" + criterio + "') encontró " + resultados.size() + " clientes");
            return resultados;
        } catch (Exception e) {
            System.err.println("✗ Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Busca clientes por apellidos
     */
    public List<ClienteEntity> findByApellidos(String apellidos) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + apellidos.trim() + "%";
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE c.apellidos LIKE :pattern AND (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL) ORDER BY c.nombres",
                ClienteEntity.class
            );
            query.setParameter("pattern", searchPattern);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al buscar por apellidos: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Busca clientes por nombres
     */
    public List<ClienteEntity> findByNombres(String nombres) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + nombres.trim() + "%";
            TypedQuery<ClienteEntity> query = em.createQuery(
                "SELECT c FROM ClienteEntity c WHERE c.nombres LIKE :pattern AND (c.estado = 'A' OR c.estado = 'Activo' OR c.estado IS NULL) ORDER BY c.nombres",
                ClienteEntity.class
            );
            query.setParameter("pattern", searchPattern);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al buscar por nombres: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elimina (inactiva) un cliente
     */
    public boolean delete(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            JPAUtil.beginTransaction();
            Optional<ClienteEntity> clienteOpt = findById(id);
            if (clienteOpt.isPresent()) {
                ClienteEntity cliente = clienteOpt.get();
                cliente.setEstado("Inactivo");
                em.merge(cliente);
                JPAUtil.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            JPAUtil.rollback();
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}

