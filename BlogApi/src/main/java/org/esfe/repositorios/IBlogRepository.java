package org.esfe.repositorios;

import org.esfe.modelos.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBlogRepository  extends JpaRepository<Blog, Integer> {
    List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
    List<Blog> findByTituloContainingIgnoreCase(String titulo);
    List<Blog> findByDescripcionContainingIgnoreCase(String descripcion);
}
//se añadio comentario para poder subir el commit y relacionarlo con jira ya que sino no deja vincularlo.