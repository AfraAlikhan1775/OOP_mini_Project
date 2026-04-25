package com.dao;

import java.util.List;

/**
 * Generic DAO Interface
 * Demonstrates: Interface, Abstraction
 * Defines contract for all DAO implementations
 */
public interface IBaseDAO<T> {

    /**
     * Get an entity by ID
     */
    T getById(Object id);

    /**
     * Get all entities
     */
    List<T> getAll();

    /**
     * Save/Insert an entity
     */
    boolean save(T entity);

    /**
     * Update an entity
     */
    boolean update(T entity);

    /**
     * Delete an entity by ID
     */
    boolean delete(Object id);

    /**
     * Get count of all entities
     */
    int getCount();
}

