package com.perfulandia.PedidosService.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
@Data               // Genera getters, setters, equals, hashCode y toString automáticamente
@NoArgsConstructor  // Constructor sin argumentos necesario para JPA
@AllArgsConstructor // Constructor con todos los campos
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;

    private LocalDate fecha;

    private String estado;

    private Double total;

}

