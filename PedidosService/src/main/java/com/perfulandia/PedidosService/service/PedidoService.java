package com.perfulandia.PedidosService.service;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    List<Pedido> obtenerTodos();
    Optional<Pedido> obtenerPorId(Long id);
    Pedido crear(Pedido pedido);
    Pedido actualizar(Long id, Pedido pedido);
    void eliminar(Long id);
    ProductoDTO consultarProductoPorId(Long idProducto);
    UsuarioDTO consultarUsuarioPorId(Long idUsuario);


}

