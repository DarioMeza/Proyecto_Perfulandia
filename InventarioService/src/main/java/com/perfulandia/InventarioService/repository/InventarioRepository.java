package com.perfulandia.InventarioService.repository;

import com.perfulandia.InventarioService.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Inventario findByCodigoProducto(String codigoProducto);
}
