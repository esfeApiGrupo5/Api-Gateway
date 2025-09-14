package org.esfe.dtos.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BlogGuardar implements Serializable {
    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 50, message = "El nombre del autor no puede exceder los 50 caracteres")
    private String autor;
}
//se añadio comentario para poder subir el commit y relacionarlo con jira ya que sino no deja vincularlo.