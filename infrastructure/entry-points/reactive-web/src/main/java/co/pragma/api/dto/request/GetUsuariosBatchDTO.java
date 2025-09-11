package co.pragma.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUsuariosBatchDTO {

    @NotNull(message = "La lista de IDs no puede ser nula")
    @NotEmpty(message = "La lista de IDs no puede estar vacía")
    private List<UUID> userIds;

}
