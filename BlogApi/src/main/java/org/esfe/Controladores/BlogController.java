package org.esfe.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.esfe.servicios.interfaces.IBlogService;
import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.modelos.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.esfe.repositorios.IBlogRepository;
import java.util.Collections;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IBlogRepository blogRepository;
    @Autowired
    private ModelMapper modelMapper;
    
    @GetMapping
    public ResponseEntity<Page<BlogSalida>> mostrarTodosPaginados(Pageable pageable){
        Page<BlogSalida> blogs = blogService.obtenerTodosPaginados(pageable);
        if(blogs.hasContent()){
            return ResponseEntity.ok(blogs);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<BlogSalida>> mostrarTodos(){
        List<BlogSalida> blogs = blogService.obtenerTodos();
        if(!blogs.isEmpty()){
            return ResponseEntity.ok(blogs);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogSalida> buscarPorId(@PathVariable Integer id){
        BlogSalida blog = blogService.obtenerPorId(id);

        if(blog != null){
            return ResponseEntity.ok(blog);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BlogSalida> crear(@RequestBody BlogGuardar blogGuardar){
        BlogSalida blog = blogService.crear(blogGuardar);
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogSalida> editar(@PathVariable Integer id, @RequestBody BlogModificar blogModificar){
        BlogSalida blog = blogService.editar(blogModificar);
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity eliminar(@PathVariable Integer id){
        blogService.eliminarPorId(id);
        return ResponseEntity.ok("Blog eliminado correctamente");
    }

    @GetMapping("/buscar")
    public List<BlogSalida> buscarBlogs(
        @RequestParam(required = false) String titulo,
        @RequestParam(required = false) String descripcion) {

    List<Blog> blogs;

    if (titulo != null && descripcion != null) {
        // Buscar por título O descripción
        blogs = blogRepository.findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(titulo, descripcion);
    } else if (titulo != null) {
        // Buscar solo por título
        blogs = blogRepository.findByTituloContainingIgnoreCase(titulo);
    } else if (descripcion != null) {
        // Buscar solo por descripción
        blogs = blogRepository.findByDescripcionContainingIgnoreCase(descripcion);
    } else {
        // Si no se proporciona ningún parámetro todos los blogs
        blogs = blogRepository.findAll();
    }

    return blogs.stream()
            .map(blog -> modelMapper.map(blog, BlogSalida.class))
            .collect(Collectors.toList());
    }
}
