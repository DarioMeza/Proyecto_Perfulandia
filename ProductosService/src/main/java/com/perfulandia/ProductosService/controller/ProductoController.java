package com.perfulandia.ProductosService.controller;

import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id).orElse(null);
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    // GET /api/productos/hateoas
    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<Producto>> obtenerTodosConHateoas() {
        List<EntityModel<Producto>> productos = productoService.listarProductos().stream()
                .map(p -> EntityModel.of(p,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerPorIdConHateoas(p.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).actualizarConHateoas(p.getId(), null)).withRel("actualizar"),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).eliminarConHateoas(p.getId())).withRel("eliminar")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerTodosConHateoas()).withSelfRel());
    }

    // GET /api/productos/hateoas/{id}
    @GetMapping("/hateoas/{id}")
    public EntityModel<Producto> obtenerPorIdConHateoas(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id).orElse(null);
        if (producto == null) return null;

        return EntityModel.of(producto,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerPorIdConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).actualizarConHateoas(id, null)).withRel("actualizar"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).eliminarConHateoas(id)).withRel("eliminar")
        );
    }

    // POST /api/productos/hateoas
    @PostMapping("/hateoas")
    public EntityModel<Producto> crearConHateoas(@RequestBody Producto producto) {
        Producto nuevo = productoService.guardarProducto(producto);
        return EntityModel.of(nuevo,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerPorIdConHateoas(nuevo.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

    // PUT /api/productos/hateoas/{id}
    @PutMapping("/hateoas/{id}")
    public EntityModel<Producto> actualizarConHateoas(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizarProducto(id, producto);
        return EntityModel.of(actualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerPorIdConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

    // DELETE /api/productos/hateoas/{id}
    @DeleteMapping("/hateoas/{id}")
    public EntityModel<String> eliminarConHateoas(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return EntityModel.of("Producto eliminado correctamente",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductoController.class).obtenerTodosConHateoas()).withRel("todos"));
    }

}
