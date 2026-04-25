package com.dao;

import com.database.DatabaseInitializer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all DAOs
 * Demonstrates: Abstract Class, Inheritance, Generics, Template Method Pattern
 */
public abstract class BaseDAO<T> implements IBaseDAO<T> {

    /**
     * Template method for getting count
     */
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Abstract method - subclasses must implement
     */
    protected abstract String getTableName();

    /**
     * Abstract method - subclasses must implement
     */
    @Override
    public abstract T getById(Object id);

    /**
     * Abstract method - subclasses must implement
     */
    @Override
    public abstract List<T> getAll();

    /**
     * Abstract method - subclasses must implement
     */
    @Override
    public abstract boolean save(T entity);

    /**
     * Abstract method - subclasses must implement
     */
    @Override
    public abstract boolean update(T entity);

    /**
     * Abstract method - subclasses must implement
     */
    @Override
    public abstract boolean delete(Object id);

    /**
     * Helper method to safely close resources
     */
    protected void closeResources(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

