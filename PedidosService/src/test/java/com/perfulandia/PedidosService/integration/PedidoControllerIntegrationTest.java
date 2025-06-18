package com.perfulandia.PedidosService.integration;

import com.perfulandia.PedidosService.model.Pedido;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PedidoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private static Long idCreado;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/pedidos";
    }

    @Test
    @Order(1)
    public void testCrearPedido() {
        Pedido pedido = new Pedido();
        pedido.setCliente("ClienteTest");
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        pedido.setTotal(100.0);
        pedido.setProductoId(1L);

        ResponseEntity<Pedido> response = restTemplate.postForEntity(baseUrl, pedido, Pedido.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("ClienteTest", response.getBody().getCliente());

        idCreado = response.getBody().getId();
    }

    @Test
    @Order(2)
    public void testObtenerTodosPedidos() {
        ResponseEntity<Pedido[]> response = restTemplate.getForEntity(baseUrl, Pedido[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(3)
    public void testObtenerPedidoPorId() {
        ResponseEntity<Pedido> response = restTemplate.getForEntity(baseUrl + "/" + idCreado, Pedido.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ClienteTest", response.getBody().getCliente());
    }

    @Test
    @Order(4)
    public void testActualizarPedido() {
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setCliente("ClienteActualizado");
        pedidoActualizado.setFecha(LocalDate.now());
        pedidoActualizado.setEstado("Entregado");
        pedidoActualizado.setTotal(150.0);
        pedidoActualizado.setProductoId(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Pedido> requestUpdate = new HttpEntity<>(pedidoActualizado, headers);

        ResponseEntity<Pedido> response = restTemplate.exchange(baseUrl + "/" + idCreado, HttpMethod.PUT, requestUpdate, Pedido.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ClienteActualizado", response.getBody().getCliente());
        assertEquals("Entregado", response.getBody().getEstado());
    }

    @Test
    public void testEliminarPedido() {
        // Crear pedido para probar
        Pedido pedido = new Pedido();
        pedido.setCliente("Test Cliente");
        pedido.setEstado("Pendiente");
        pedido.setFecha(LocalDate.now());
        pedido.setProductoId(1L);
        pedido.setTotal(100.0);

        Pedido creado = restTemplate.postForObject(baseUrl, pedido, Pedido.class);
        Long id = creado.getId();

        // Eliminar pedido
        restTemplate.delete(baseUrl + "/" + id);

        // Verificar que al hacer GET, devuelva 404 (pedido no existe)
        try {
            restTemplate.getForEntity(baseUrl + "/" + id, Pedido.class);
            fail("El pedido debería haber sido eliminado y no encontrarse");
        } catch (HttpClientErrorException e) {
            // Esperamos 404 Not Found
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

}
