package org.esfe.servicios.implementaciones;

import org.esfe.dtos.blog.BlogGuardar;
import org.esfe.dtos.blog.BlogModificar;
import org.esfe.dtos.blog.BlogSalida;
import org.esfe.modelos.Blog;
import org.esfe.repositorios.IBlogRepository;
import org.esfe.servicios.interfaces.IBlogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService implements IBlogService {

    @Autowired
    private IBlogRepository blogRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BlogSalida crear(BlogGuardar blogGuardar) {
        Blog blog = modelMapper.map(blogGuardar, Blog.class);

    // aseguramos que el ID sea null para que Hibernate haga un INSERT
    blog.setId(null);

    // asignamos la fecha actual al crear
    blog.setFechaPublicacion(LocalDateTime.now());

    blog = blogRepository.save(blog);
    return modelMapper.map(blog, BlogSalida.class);
    }

    @Override
    public BlogSalida editar(BlogModificar blogModificar) {
    // 1. Busca el blog existente en la base de datos por su ID
    Blog blogExistente = blogRepository.findById(blogModificar.getId())
            .orElseThrow(() -> new RuntimeException("Blog no encontrado con ID: " + blogModificar.getId()));

    modelMapper.map(blogModificar, blogExistente);
    blogExistente = blogRepository.save(blogExistente);
    return modelMapper.map(blogExistente, BlogSalida.class);
    }

    @Override
    public void eliminarPorId(Integer id) {
        blogRepository.deleteById(id);
        
    }

    @Override
    public List<Blog> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo,
            String descripcion) {
        List<Blog> blogs = blogRepository.findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(titulo, descripcion);
        return blogs.stream()
                .map(blog -> modelMapper.map(blog, Blog.class))
                .collect(Collectors.toList());
    }

    @Override
    public BlogSalida obtenerPorId(Integer id) {
        return modelMapper.map(blogRepository.findById(id).get(), BlogSalida.class);
    }

    @Override
    public List<BlogSalida> obtenerTodos() {
        List<Blog> blogs = blogRepository.findAll();

        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogSalida.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<BlogSalida> obtenerTodosPaginados(Pageable pageable) {
        Page<Blog> page = blogRepository.findAll(pageable);
        List<BlogSalida> blogsDto = page.stream()
                .map(blog -> modelMapper.map(blog, BlogSalida.class))
                .collect(Collectors.toList());
        return new PageImpl<>(blogsDto, page.getPageable(), page.getTotalElements());
    }

    
}