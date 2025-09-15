package co.pragma.model.rol;

import co.pragma.model.session.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Integer id;
    private String nombre;
    private String descripcion;

    public Set<Permission> getPermissions() {
        RolEnum rolEnum = RolEnum.fromNombre(nombre);
        return rolEnum.getPermissions();
    }

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }
}
