package com.perfulandia.InventarioService.controller;

import com.perfulandia.InventarioService.dto.ProductoDTO;
import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "API para gestionar el inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @Operation(summary = "Lista todos los registros de inventario")
    @ApiResponse(responseCode = "200", description = "Listado de inventario obtenido correctamente")
    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro de inventario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public Inventario obtener(
            @Parameter(description = "ID del registro de inventario", required = true)
            @PathVariable Long id) {
        return inventarioService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "Guarda un nuevo registro en inventario")
    @ApiResponse(responseCode = "200", description = "Registro guardado exitosamente")
    public Inventario guardar(
            @Parameter(description = "Objeto inventario a guardar", required = true)
            @RequestBody Inventario inventario) {
        return inventarioService.guardar(inventario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro de inventario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado para actualizar")
    })
    public Inventario actualizar(
            @Parameter(description = "ID del registro a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del registro", required = true)
            @RequestBody Inventario inventario) {
        return inventarioService.actualizar(id, inventario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un registro de inventario por ID")
    @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del registro a eliminar", required = true)
            @PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();  // <-- Aquí va el 204 NO_CONTENT
    }



    @GetMapping("/producto/{id}")
    @Operation(summary = "Consulta información detallada de un producto por ID")
    @ApiResponse(responseCode = "200", description = "Información del producto obtenida correctamente")
    public ProductoDTO consultarProducto(
            @Parameter(description = "ID del producto a consultar", required = true)
            @PathVariable Long id) {
        return inventarioService.consultarProductoPorId(id);
    }

    // listar() (GET /api/inventario)
    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<Inventario>> listarConHateoas() {
        List<Inventario> inventarios = inventarioService.listar();
        List<EntityModel<Inventario>> recursos = inventarios.stream()
                .map(inventario -> EntityModel.of(inventario,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).obtenerConHateoas(inventario.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).actualizarConHateoas(inventario.getId(), null)).withRel("actualizar"),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).eliminarConHateoas(inventario.getId())).withRel("eliminar")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).listarConHateoas()).withSelfRel());
    }
    // obtener() (GET /api/inventario/{id})
    @GetMapping("/hateoas/{id}")
    public EntityModel<Inventario> obtenerConHateoas(@PathVariable Long id) {
        Inventario inventario = inventarioService.obtenerPorId(id).orElse(null);
        if (inventario == null) return null;

        return EntityModel.of(inventario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).obtenerConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).actualizarConHateoas(id, null)).withRel("actualizar"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).eliminarConHateoas(id)).withRel("eliminar")
        );
    }
    // guardar() (POST /api/inventario)
    @PostMapping("/hateoas")
    public EntityModel<Inventario> guardarConHateoas(@RequestBody Inventario inventario) {
        Inventario guardado = inventarioService.guardar(inventario);
        return EntityModel.of(guardado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).obtenerConHateoas(guardado.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).listarConHateoas()).withRel("todos")
        );
    }
    // actualizar()  (PUT /api/inventario/{id})
    @PutMapping("/hateoas/{id}")
    public EntityModel<Inventario> actualizarConHateoas(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario actualizado = inventarioService.actualizar(id, inventario);
        return EntityModel.of(actualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).obtenerConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).listarConHateoas()).withRel("todos")
        );
    }
    // eliminar() (DELETE /api/inventario/{id})
    @DeleteMapping("/hateoas/{id}")
    public EntityModel<String> eliminarConHateoas(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return EntityModel.of("Eliminado con éxito",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(InventarioController.class).listarConHateoas()).withRel("todos")
        );
    }

}
