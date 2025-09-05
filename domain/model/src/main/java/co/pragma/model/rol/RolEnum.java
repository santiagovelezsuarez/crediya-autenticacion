package co.pragma.model.rol;

import co.pragma.exception.RolNotFoundException;
import lombok.Getter;

@Getter
public enum RolEnum {
    ADMIN(1, "ADMIN", "Administrador del area financiera"),
    ASESOR(2, "ASESOR", "Asesor financiero de crediYa"),
    CLIENTE(3, "CLIENTE", "Cliente de crediYa");

    private final Integer id;
    private final String nombre;
    private final String descripcion;

    RolEnum(Integer id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static RolEnum fromNombre(String nombre) {
        for (RolEnum rol : values()) {
            if (rol.nombre.equalsIgnoreCase(nombre)) {
                return rol;
            }
        }
        throw new RolNotFoundException("Rol no válido: " + nombre);
    }

    public static RolEnum fromId(Integer id) {
        for (RolEnum rol : values()) {
            if (rol.id.equals(id)) {
                return rol;
            }
        }
        throw new RolNotFoundException("ID de rol no válido: " + id);
    }
}
