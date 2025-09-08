package co.pragma.model.rol;

import co.pragma.exception.business.RolNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RolEnumTest {

    @Test
    void shouldReturnAdminByName() {
        RolEnum rol = RolEnum.fromNombre("ADMIN");
        assertThat(rol).isEqualTo(RolEnum.ADMIN);
    }

    @Test
    void shouldReturnAdminByNameIgnoringCase() {
        RolEnum rol = RolEnum.fromNombre("admin");
        assertThat(rol).isEqualTo(RolEnum.ADMIN);
    }

    @Test
    void shouldReturnAdminById() {
        RolEnum rol = RolEnum.fromId(1);
        assertThat(rol).isEqualTo(RolEnum.ADMIN);
    }

    @Test
    void shouldThrowExceptionWhenNameNotFound() {
        assertThatThrownBy(() -> RolEnum.fromNombre("TODERO"))
                .isInstanceOf(RolNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenIdNotFound() {
        assertThatThrownBy(() -> RolEnum.fromId(999))
                .isInstanceOf(RolNotFoundException.class);
    }

    @Test
    void shouldHaveUniqueIds() {
        long distinctCount = java.util.Arrays.stream(RolEnum.values())
                .map(RolEnum::getId)
                .distinct()
                .count();

        assertThat(distinctCount).isEqualTo(RolEnum.values().length);
    }
}
