package com.perfulandia.PedidosService.controller;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.service.PedidoService;
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
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {
        Pedido nuevo = pedidoService.crear(pedido);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido actualizado = pedidoService.actualizar(id, pedido);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{id}")
    public ProductoDTO consultarProducto(@PathVariable Long id) {
        return pedidoService.consultarProductoPorId(id);
    }

    @GetMapping("/usuario/{id}")
    public UsuarioDTO consultarUsuario(@PathVariable Long id) {
        return pedidoService.consultarUsuarioPorId(id);
    }

    // obtener()
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

    // obtenerporid()
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

    // crear()
    @PostMapping("/hateoas")
    public EntityModel<Pedido> crearConHateoas(@RequestBody Pedido pedido) {
        Pedido nuevo = pedidoService.crear(pedido);
        return EntityModel.of(nuevo,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(nuevo.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }


    // actualizar()
    @PutMapping("/hateoas/{id}")
    public EntityModel<Pedido> actualizarConHateoas(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido actualizado = pedidoService.actualizar(id, pedido);
        return EntityModel.of(actualizado,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerPorIdConHateoas(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

    // eliminar()
    @DeleteMapping("/hateoas/{id}")
    public EntityModel<String> eliminarConHateoas(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return EntityModel.of("Eliminado con éxito",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PedidoController.class).obtenerTodosConHateoas()).withRel("todos")
        );
    }

}

