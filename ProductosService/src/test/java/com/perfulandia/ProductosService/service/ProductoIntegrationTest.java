package com.perfulandia.ProductosService.service;

import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void testCrearYObtenerProducto() {
        // Crear nuevo producto
        Producto nuevoProducto = new Producto(null, "Perfume Integracion", 10, 15000.0);

        ResponseEntity<Producto> responseCreate = restTemplate.postForEntity("/api/productos", nuevoProducto, Producto.class);
        assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        Producto productoCreado = responseCreate.getBody();
        assertThat(productoCreado).isNotNull();
        assertThat(productoCreado.getId()).isNotNull();
        assertThat(productoCreado.getNombre()).isEqualTo("Perfume Integracion");

        // Obtener producto creado
        ResponseEntity<Producto> responseGet = restTemplate.getForEntity("/api/productos/" + productoCreado.getId(), Producto.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        Producto productoObtenido = responseGet.getBody();
        assertThat(productoObtenido).isNotNull();
        assertThat(productoObtenido.getNombre()).isEqualTo("Perfume Integracion");
    }

    @Test
    void testEliminarProducto() {
        // Primero crear producto para eliminar
        Producto producto = new Producto(null, "Producto a eliminar", 5, 8000.0);
        Producto guardado = productoRepository.save(producto);

        // Eliminar con DELETE
        ResponseEntity<Void> responseDelete = restTemplate.exchange("/api/productos/" + guardado.getId(),
                HttpMethod.DELETE, null, Void.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verificar que ya no existe
        boolean existe = productoRepository.existsById(guardado.getId());
        assertThat(existe).isFalse();
    }

    @Test
    void testActualizarProducto() {
        Producto original = new Producto(null, "Producto Original", 3, 12000.0);
        Producto guardado = productoRepository.save(original);

        Producto actualizado = new Producto(null, "Producto Actualizado", 5, 15000.0);

        HttpEntity<Producto> requestUpdate = new HttpEntity<>(actualizado);
        ResponseEntity<Producto> responseUpdate = restTemplate.exchange(
                "/api/productos/" + guardado.getId(),
                HttpMethod.PUT,
                requestUpdate,
                Producto.class
        );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
        Producto productoActualizado = responseUpdate.getBody();
        assertThat(productoActualizado).isNotNull();
        assertThat(productoActualizado.getNombre()).isEqualTo("Producto Actualizado");
        assertThat(productoActualizado.getPrecio()).isEqualTo(15000.0);
    }

    @Test
    void testListarProductos() {
        // Agregar dos productos
        productoRepository.save(new Producto(null, "Prod 1", 2, 10000.0));
        productoRepository.save(new Producto(null, "Prod 2", 3, 20000.0));

        ResponseEntity<Producto[]> response = restTemplate.getForEntity("/api/productos", Producto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

}
