package co.pragma.model.usuario;

import lombok.Getter;

@Getter
public enum TipoDocumento {
    CC("Cédula de Ciudadanía", "CC"),
    CE("Cédula de Extranjería", "CE"),
    PA("Pasaporte", "PA"),
    TI("Tarjeta de Identidad", "TI");

    private final String descripcion;
    private final String codigo;

    TipoDocumento(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public static TipoDocumento fromCodigo(String codigo) {
        for (TipoDocumento tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de documento no válido: " + codigo);
    }
}
