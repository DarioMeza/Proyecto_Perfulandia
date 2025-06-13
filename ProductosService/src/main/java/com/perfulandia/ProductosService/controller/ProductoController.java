package com.perfulandia.ProductosService.controller;

import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtiene todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    public List<Producto> obtenerTodos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public Producto obtenerPorId(
            @Parameter(description = "ID del producto a buscar", required = true)
            @PathVariable Long id) {
        return productoService.obtenerProductoPorId(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo producto")
    @ApiResponse(responseCode = "200", description = "Producto creado exitosamente")
    public Producto crear(
            @Parameter(description = "Datos del producto a crear", required = true)
            @RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un producto existente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para actualizar")
    })
    public Producto actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del producto", required = true)
            @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto por ID")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    public void eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long id) {
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
