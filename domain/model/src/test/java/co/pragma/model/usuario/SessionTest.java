package co.pragma.model.usuario;

import co.pragma.model.rol.Permission;
import co.pragma.model.rol.RolEnum;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {

    private Session createSessionWithPermissions(Set<String> permissions) {
        return Session.builder()
                .userId("test_user_id")
                .role("ADMIN")
                .email("test@mail.co")
                .permissions(permissions)
                .build();
    }

    @Test
    void shouldReturnTrueWhenPermissionExists() {
        Set<String> permissions = new HashSet<>();
        permissions.add(Permission.REGISTRAR_USUARIO.name());
        Session session = createSessionWithPermissions(permissions);

        assertTrue(session.hasPermission(Permission.REGISTRAR_USUARIO));
    }

    @Test
    void shouldReturnFalseWhenPermissionDoesNotExist() {
        Set<String> permissions = new HashSet<>();
        permissions.add(Permission.APROBAR_SOLICITUD.name());
        Session session = createSessionWithPermissions(permissions);

        assertFalse(session.hasPermission(Permission.REGISTRAR_USUARIO));
    }

    @Test
    void shouldReturnFalseWhenPermissionsAreEmpty() {
        Session session = createSessionWithPermissions(Set.of());

        assertFalse(session.hasPermission(Permission.REGISTRAR_USUARIO));
    }

    @Test
    void shouldReturnFalseWhenUserIdIsEmpty() {
        Session session = Session.builder().userId("").build();

        assertFalse(session.isAuthenticated());
    }

    @Test
    void shouldReturTrueWhenHasRole() {
        Session session = createSessionWithPermissions(Set.of());
        assertTrue(session.hasRole(RolEnum.ADMIN.name()));
    }
}
