package co.pragma.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserContextTest {

    @Test
    void hasPermissionShouldReturnTrueWhenPermissionExists() {
        UserContext user = new UserContext("123", "user@example.com", "ADMIN", List.of("READ", "WRITE"));
        assertTrue(user.hasPermission("READ"));
    }

    @Test
    void hasPermissionShouldReturnFalseWhenPermissionDoesNotExist() {
        UserContext user = new UserContext("123", "user@example.com", "ADMIN", List.of("READ"));
        assertFalse(user.hasPermission("WRITE"));
    }

    @Test
    void hasPermissionShouldReturnFalseWhenPermissionsListIsEmpty() {
        UserContext user = new UserContext("123", "user@example.com", "ADMIN", List.of());
        assertFalse(user.hasPermission("ANY_PERMISSION"));
    }

    @Test
    void hasPermissionShouldReturnFalseWhenPermissionsListIsNull() {
        UserContext user = new UserContext("123", "user@example.com", "ADMIN", null);
        assertFalse(user.hasPermission("ANY_PERMISSION"));
    }

    @Test
    void isRoleShouldReturnTrueWhenRoleMatchesIgnoringCase() {
        UserContext user = new UserContext("123", "user@example.com", "ADMIN", List.of());
        assertTrue(user.isRole("admin"));
    }

    @Test
    void isRoleShouldReturnFalseWhenRoleDoesNotMatch() {
        UserContext user = new UserContext("123", "user@example.com", "USER", List.of());
        assertFalse(user.isRole("ADMIN"));
    }

}