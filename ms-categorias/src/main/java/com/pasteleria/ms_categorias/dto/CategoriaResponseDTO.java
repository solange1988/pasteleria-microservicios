package com.pasteleria.ms_categorias.dto;





import lombok.*;

import java.time.LocalDateTime;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
