package com.perfulandia.ProductosService.service;

import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        return productoRepository.findById(id).map(p -> {
            p.setNombre(productoActualizado.getNombre());
            p.setStock(productoActualizado.getStock());
            p.setPrecio(productoActualizado.getPrecio());
            return productoRepository.save(p);
        }).orElse(null);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}
