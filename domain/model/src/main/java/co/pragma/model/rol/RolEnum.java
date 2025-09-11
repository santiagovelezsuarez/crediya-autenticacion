package co.pragma.model.rol;

import co.pragma.exception.business.RolNotFoundException;
import lombok.Getter;

import java.util.Set;

@Getter
public enum RolEnum {
    ADMIN(1, "ADMIN", "Administrador del área financiera", Set.of(
            Permission.REGISTRAR_USUARIO
    )),
    ASESOR(2, "ASESOR", "Asesor financiero de crediYa", Set.of(
            Permission.REGISTRAR_USUARIO,
            Permission.APROBAR_SOLICITUD,
            Permission.RECHAZAR_SOLICITUD,
            Permission.LISTAR_SOLICITUDES_PENDIENTES
    )),
    CLIENTE(3, "CLIENTE", "Cliente de crediYa", Set.of(
            Permission.SOLICITAR_PRESTAMO
    )),
    SISTEMA(4, "SISTEMA", "Sistema", Set.of(
            Permission.VALIDAR_AUTOMATICO
    ));

    private final Integer id;
    private final String nombre;
    private final String descripcion;
    private final Set<Permission> permissions;

    RolEnum(Integer id, String nombre, String descripcion, Set<Permission> permissions) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permissions = permissions;
    }

    public static RolEnum fromNombre(String nombre) {
        for (RolEnum rol : values()) {
            if (rol.nombre.equalsIgnoreCase(nombre)) {
                return rol;
            }
        }
        throw new RolNotFoundException();
    }

    public static RolEnum fromId(Integer id) {
        for (RolEnum rol : values()) {
            if (rol.id.equals(id)) {
                return rol;
            }
        }
        throw new RolNotFoundException();
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public Rol toRol() {
        return Rol.builder()
                .id(this.id)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .build();
    }

    public static Set<Permission> getPermissionsForRole(String roleName) {
        return fromNombre(roleName).getPermissions();
    }
}
