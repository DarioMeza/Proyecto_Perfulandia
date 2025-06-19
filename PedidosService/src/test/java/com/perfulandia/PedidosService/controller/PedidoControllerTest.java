package com.perfulandia.PedidosService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setup() {
        pedido1 = new Pedido(1L, "Cliente1", LocalDate.of(2024, 6, 18), "Pendiente", 100.0, 10L);
        pedido2 = new Pedido(2L, "Cliente2", LocalDate.of(2024, 6, 19), "Entregado", 200.0, 20L);
    }

    @Test
    public void testObtenerTodos() throws Exception {
        Mockito.when(pedidoService.obtenerTodos()).thenReturn(Arrays.asList(pedido1, pedido2));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cliente").value("Cliente1"))
                .andExpect(jsonPath("$[1].estado").value("Entregado"));
    }

    @Test
    public void testObtenerPorIdExistente() throws Exception {
        Mockito.when(pedidoService.obtenerPorId(1L)).thenReturn(Optional.of(pedido1));

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente").value("Cliente1"))
                .andExpect(jsonPath("$.total").value(100.0));
    }

    @Test
    public void testObtenerPorIdNoExistente() throws Exception {
        Mockito.when(pedidoService.obtenerPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCrearPedido() throws Exception {
        Pedido nuevoPedido = new Pedido(null, "ClienteNuevo", LocalDate.now(), "Pendiente", 150.0, 30L);
        Pedido pedidoGuardado = new Pedido(3L, "ClienteNuevo", LocalDate.now(), "Pendiente", 150.0, 30L);

        Mockito.when(pedidoService.crear(any(Pedido.class))).thenReturn(pedidoGuardado);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoPedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.cliente").value("ClienteNuevo"));
    }

    @Test
    public void testActualizarPedidoExistente() throws Exception {
        Pedido pedidoActualizado = new Pedido(null, "ClienteActualizado", LocalDate.now(), "Entregado", 300.0, 10L);

        Mockito.when(pedidoService.actualizar(eq(1L), any(Pedido.class))).thenReturn(pedidoActualizado);

        mockMvc.perform(put("/api/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente").value("ClienteActualizado"))
                .andExpect(jsonPath("$.estado").value("Entregado"))
                .andExpect(jsonPath("$.total").value(300.0));
    }

    @Test
    public void testActualizarPedidoNoExistente() throws Exception {
        Pedido pedidoActualizado = new Pedido(null, "ClienteActualizado", LocalDate.now(), "Entregado", 300.0, 10L);

        Mockito.when(pedidoService.actualizar(eq(1L), any(Pedido.class))).thenThrow(new RuntimeException("Pedido no encontrado con id: 1"));

        mockMvc.perform(put("/api/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoActualizado)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEliminarPedido() throws Exception {
        Mockito.doNothing().when(pedidoService).eliminar(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());
    }
}

