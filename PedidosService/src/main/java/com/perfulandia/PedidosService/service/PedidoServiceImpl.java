package com.perfulandia.PedidosService.service;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final RestTemplate restTemplate;

    // Constructor con @Autowired para inyección explícita (mejor para testing)
    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, RestTemplate restTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido crear(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));

        existente.setCliente(pedido.getCliente());
        existente.setFecha(pedido.getFecha());
        existente.setEstado(pedido.getEstado());
        existente.setTotal(pedido.getTotal());
        existente.setProductoId(pedido.getProductoId());

        return pedidoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public ProductoDTO consultarProductoPorId(Long id) {
        String url = "http://localhost:8081/api/productos/" + id;
        return restTemplate.getForObject(url, ProductoDTO.class);
    }

    @Override
    public UsuarioDTO consultarUsuarioPorId(Long id) {
        String url = "http://localhost:8082/api/usuarios/" + id;
        return restTemplate.getForObject(url, UsuarioDTO.class);
    }
}
