package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    private String descripcion;

    private String autor;

    private LocalDateTime fechaPublicacion;
}
//se añadio comentario para poder subir el commit y relacionarlo con jira ya que sino no deja vincularlo.