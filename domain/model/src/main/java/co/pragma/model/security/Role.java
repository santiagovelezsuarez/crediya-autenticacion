package co.pragma.model.security;

import java.util.Set;

import static co.pragma.model.security.Permission.REGISTRAR_USUARIO;

public enum Role {
    ADMIN(Set.of(REGISTRAR_USUARIO)),
    ASESOR(Set.of(REGISTRAR_USUARIO)),
    CLIENTE(Set.of());

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}