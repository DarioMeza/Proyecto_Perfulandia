package com.perfulandia.PedidosService.controller;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}

