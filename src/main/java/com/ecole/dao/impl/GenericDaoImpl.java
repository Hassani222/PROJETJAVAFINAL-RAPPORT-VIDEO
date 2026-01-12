package com.ecole.dao.impl;

import com.ecole.dao.GenericDao;
import com.ecole.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation générique du DAO utilisant Hibernate.
 * Fournit les opérations CRUD de base pour toutes les entités.
 * 
 * @param <T>  Le type d'entité
 * @param <ID> Le type de l'identifiant
 */
public abstract class GenericDaoImpl<T, ID> implements GenericDao<T, ID> {

    protected final Class<T> entityClass;

    protected GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Obtient une session Hibernate.
     * 
     * @return Session Hibernate
     */
    protected Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    @Override
    public T create(T entity) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la création de l'entité", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = getSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = getSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    @Override
    public T update(T entity) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            T mergedEntity = session.merge(entity);
            transaction.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour de l'entité", e);
        }
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            T managedEntity = session.merge(entity);
            session.remove(managedEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de l'entité", e);
        }
    }

    @Override
    public boolean deleteById(ID id) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression par ID", e);
        }
    }

    @Override
    public long count() {
        try (Session session = getSession()) {
            return session.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class)
                    .uniqueResult();
        }
    }

    @Override
    public boolean existsById(ID id) {
        try (Session session = getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id", Long.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
