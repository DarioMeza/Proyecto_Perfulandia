package com.perfulandia.PedidosService.repository;

import com.perfulandia.PedidosService.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Puedes agregar métodos personalizados si los necesitas, por ejemplo:
    // List<Pedido> findByCliente(String cliente);
}
