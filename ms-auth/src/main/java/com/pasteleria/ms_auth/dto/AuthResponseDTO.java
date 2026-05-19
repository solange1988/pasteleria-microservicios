package com.pasteleria.ms_auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private boolean activo;
    private String mensaje;
}
