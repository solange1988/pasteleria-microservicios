package com.pasteleria.ms_usuarios.dto;




import lombok.*;

import java.time.LocalDateTime;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String rol;
    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
