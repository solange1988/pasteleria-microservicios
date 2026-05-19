package com.pasteleria.ms_categorias.dto;




import jakarta.validation.constraints.*;
import lombok.Data;



@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}
