package com.perfulandia.PedidosService.controller;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Para permitir peticiones desde Postman u otro frontend
@Tag(name = "Pedidos", description = "API para gestionar pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Obtiene todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente")
    public List<Pedido> obtenerTodos() {
        return pedidoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Pedido> obtenerPorId(
            @Parameter(description = "ID del pedido a buscar", required = true)
            @PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo pedido")
    @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente")
    public ResponseEntity<Pedido> crear(
            @Parameter(description = "Datos del pedido a crear", required = true)
            @RequestBody Pedido pedido) {
        Pedido nuevo = pedidoService.crear(pedido);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un pedido existente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado para actualizar")
    })
    public ResponseEntity<Pedido> actualizar(
            @Parameter(description = "ID del pedido a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del pedido", required = true)
            @RequestBody Pedido pedido) {
        try {
            Pedido actualizado = pedidoService.actualizar(id, pedido);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un pedido por ID")
    @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pedido a eliminar", required = true)
            @PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{id}")
    @Operation(summary = "Consulta información del producto asociado a un pedido")
    @ApiResponse(responseCode = "200", description = "Información del producto obtenida correctamente")
    public ProductoDTO consultarProducto(
            @Parameter(description = "ID del producto asociado al pedido", required = true)
            @PathVariable Long id) {
        return pedidoService.consultarProductoPorId(id);
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Consulta información del usuario asociado a un pedido")
    @ApiResponse(responseCode = "200", description = "Información del usuario obtenida correctamente")
    public UsuarioDTO consultarUsuario(
            @Parameter(description = "ID del usuario asociado al pedido", required = true)
            @PathVariable Long id) {
        return pedidoService.consultarUsuarioPorId(id);
    }

    // ========================
    // === Endpoints HATEOAS ==
    // ========================

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<Pedido>> obtenerTodosConHateoas() {
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        List<EntityModel<Pedido>> recursos = pedidos.stream()
                .map(pedido -> EntityModel.of(pedido,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(pedido.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).actualizarConHateoas(pedido.getId(), null)).withRel("actualizar"),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).eliminarConHateoas(pedido.getId())).withRel("eliminar")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withSelfRel());
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<Pedido> obtenerPorIdConHateoas(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPorId(id).orElse(null);
        if (pedido == null) return null;

        return EntityModel.of(pedido,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).actualizarConHateoas(id, null)).withRel("actualizar"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).eliminarConHateoas(id)).withRel("eliminar")
        );
    }

    @PostMapping("/hateoas")
    public EntityModel<Pedido> crearConHateoas(@RequestBody Pedido pedido) {
        Pedido nuevo = pedidoService.crear(pedido);
        return EntityModel.of(nuevo,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(nuevo.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

    @PutMapping("/hateoas/{id}")
    public EntityModel<Pedido> actualizarConHateoas(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido actualizado = pedidoService.actualizar(id, pedido);
        return EntityModel.of(actualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

    @DeleteMapping("/hateoas/{id}")
    public EntityModel<String> eliminarConHateoas(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return EntityModel.of("Eliminado con éxito",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }
}
