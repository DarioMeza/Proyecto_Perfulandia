package com.perfulandia.InventarioService.integration;

import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InventarioIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Test
    void testCrearYObtenerInventario() {
        Inventario nuevoInventario = new Inventario(null, "CODIGO-123", 20);

        ResponseEntity<Inventario> responseCreate = restTemplate.postForEntity("/api/inventario", nuevoInventario, Inventario.class);
        assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.OK);

        Inventario inventarioCreado = responseCreate.getBody();
        assertThat(inventarioCreado).isNotNull();
        assertThat(inventarioCreado.getId()).isNotNull();
        assertThat(inventarioCreado.getCodigoProducto()).isEqualTo("CODIGO-123");

        ResponseEntity<Inventario> responseGet = restTemplate.getForEntity("/api/inventario/" + inventarioCreado.getId(), Inventario.class);
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);

        Inventario inventarioObtenido = responseGet.getBody();
        assertThat(inventarioObtenido).isNotNull();
        assertThat(inventarioObtenido.getCodigoProducto()).isEqualTo("CODIGO-123");
    }

    @Test
    void testEliminarInventario() {
        Inventario inventario = new Inventario(null, "CODIGO-ELIMINAR", 5);
        Inventario guardado = inventarioRepository.save(inventario);

        ResponseEntity<Void> responseDelete = restTemplate.exchange("/api/inventario/" + guardado.getId(),
                HttpMethod.DELETE, null, Void.class);
        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        boolean existe = inventarioRepository.existsById(guardado.getId());
        assertThat(existe).isFalse();
    }

    @Test
    void testActualizarInventario() {
        Inventario original = new Inventario(null, "CODIGO-ORIGINAL", 10);
        Inventario guardado = inventarioRepository.save(original);

        Inventario actualizado = new Inventario(null, "CODIGO-ACTUALIZADO", 15);

        HttpEntity<Inventario> requestUpdate = new HttpEntity<>(actualizado);
        ResponseEntity<Inventario> responseUpdate = restTemplate.exchange(
                "/api/inventario/" + guardado.getId(),
                HttpMethod.PUT,
                requestUpdate,
                Inventario.class
        );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
        Inventario inventarioActualizado = responseUpdate.getBody();
        assertThat(inventarioActualizado).isNotNull();
        assertThat(inventarioActualizado.getCodigoProducto()).isEqualTo("CODIGO-ACTUALIZADO");
        assertThat(inventarioActualizado.getCantidadDisponible()).isEqualTo(15);
    }

    @Test
    void testListarInventarios() {
        inventarioRepository.save(new Inventario(null, "COD-1", 3));
        inventarioRepository.save(new Inventario(null, "COD-2", 7));

        ResponseEntity<Inventario[]> response = restTemplate.getForEntity("/api/inventario", Inventario[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Inventario[] inventarios = response.getBody();
        assertThat(inventarios).isNotEmpty();
        assertThat(inventarios.length).isGreaterThanOrEqualTo(2);
    }

}
