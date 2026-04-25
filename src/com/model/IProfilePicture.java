package com.model;

/**
 * Interface for entities with profile pictures
 * Demonstrates: Interface Segregation Principle
 */
public interface IProfilePicture {

    String getImagePath();

    void setImagePath(String imagePath);

    boolean hasProfilePicture();
}

