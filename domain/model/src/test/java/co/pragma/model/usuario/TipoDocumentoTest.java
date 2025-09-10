package co.pragma.model.usuario;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TipoDocumentoTest {

    @Test
    void shouldReturnCorrectTipoWhenCodigoIsValid() {
        assertThat(TipoDocumento.fromCodigo("CC")).isEqualTo(TipoDocumento.CC);
        assertThat(TipoDocumento.fromCodigo("CE")).isEqualTo(TipoDocumento.CE);
        assertThat(TipoDocumento.fromCodigo("PA")).isEqualTo(TipoDocumento.PA);
        assertThat(TipoDocumento.fromCodigo("TI")).isEqualTo(TipoDocumento.TI);
    }

    @Test
    void shouldThrowExceptionWhenCodigoIsInvalid() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> TipoDocumento.fromCodigo("XYZ"));

        assertThat(exception.getMessage()).contains("Tipo de documento no válido");
    }

    @Test
    void shouldReturnCorrectDescripcionAndCodigo() {
        assertThat(TipoDocumento.CC.getDescripcion()).contains("Cédula");
        assertThat(TipoDocumento.CC.getCodigo()).isEqualTo("CC");

        assertThat(TipoDocumento.PA.getDescripcion()).isEqualTo("Pasaporte");
        assertThat(TipoDocumento.PA.getCodigo()).isEqualTo("PA");
    }
}
