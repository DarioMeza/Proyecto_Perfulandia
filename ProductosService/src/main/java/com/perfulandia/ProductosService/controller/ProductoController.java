package com.perfulandia.ProductosService.controller;

import com.perfulandia.ProductosService.dto.ProductoDTO;
import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        ProductoDTO dto = new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );

        return ResponseEntity.ok(dto);
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
}
