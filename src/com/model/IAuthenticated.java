package com.model;

/**
 * Interface for entities with authentication/role functionality
 * Demonstrates: Interface, Contract-based Design
 */
public interface IAuthenticated {

    String getUsername();

    String getPassword();

    String getRole();

    void setPassword(String newPassword);
}

