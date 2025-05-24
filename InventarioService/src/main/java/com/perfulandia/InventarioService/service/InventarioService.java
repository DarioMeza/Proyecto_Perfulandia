package com.perfulandia.InventarioService.service;

import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import com.perfulandia.InventarioService.dto.ProductoDTO;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> obtenerPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Long id, Inventario inventario) {
        return inventarioRepository.findById(id).map(inv -> {
            inv.setCodigoProducto(inventario.getCodigoProducto());
            inv.setCantidadDisponible(inventario.getCantidadDisponible());
            return inventarioRepository.save(inv);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    public ProductoDTO consultarProductoPorId(Long idProducto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/productos/" + idProducto; // Usa el puerto real del microservicio Productos
        return restTemplate.getForObject(url, ProductoDTO.class);
    }
}
