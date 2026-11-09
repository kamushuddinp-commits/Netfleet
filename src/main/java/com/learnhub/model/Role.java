package com.learnhub.model;

/**
 * Enumeration of User Roles in LearnHub.
 */
public enum Role {
    ROLE_STUDENT("Student"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
