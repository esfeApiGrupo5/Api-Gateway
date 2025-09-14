package org.esfe.servicios.interfaces;

import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.modelos.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBlogService {
    List<BlogSalida> obtenerTodos();

    Page<BlogSalida> obtenerTodosPaginados(Pageable pageable);

    BlogSalida obtenerPorId(Integer id);

    BlogSalida crear(BlogGuardar blogGuardar);

    BlogSalida editar(BlogModificar blogModificar);

    void eliminarPorId(Integer id);

    List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
}