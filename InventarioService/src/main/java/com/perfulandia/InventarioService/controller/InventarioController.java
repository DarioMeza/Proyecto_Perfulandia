package com.perfulandia.InventarioService.controller;

import com.perfulandia.InventarioService.dto.ProductoDTO;
import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.service.InventarioService;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    public Inventario obtener(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    public Inventario guardar(@RequestBody Inventario inventario) {
        return inventarioService.guardar(inventario);
    }

    @PutMapping("/{id}")
    public Inventario actualizar(@PathVariable Long id, @RequestBody Inventario inventario) {
        return inventarioService.actualizar(id, inventario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
    }

    @GetMapping("/producto/{id}")
    public ProductoDTO consultarProducto(@PathVariable Long id) {
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
