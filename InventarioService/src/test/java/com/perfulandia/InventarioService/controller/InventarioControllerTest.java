package com.perfulandia.InventarioService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventarioRepository.deleteAll();
        inventario = new Inventario(null, "COD-PRUEBA", 50);
        inventario = inventarioRepository.save(inventario);
    }

    @Test
    void listarInventarios() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigoProducto", is("COD-PRUEBA")));
    }

    @Test
    void obtenerInventarioPorId() throws Exception {
        mockMvc.perform(get("/api/inventario/{id}", inventario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoProducto", is("COD-PRUEBA")));
    }

    @Test
    void guardarInventario() throws Exception {
        Inventario nuevo = new Inventario(null, "COD-NUEVO", 25);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoProducto", is("COD-NUEVO")))
                .andExpect(jsonPath("$.cantidadDisponible", is(25)));
    }

    @Test
    void actualizarInventario() throws Exception {
        inventario.setCantidadDisponible(80);

        mockMvc.perform(put("/api/inventario/{id}", inventario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible", is(80)));
    }

    @Test
    void eliminarInventario() throws Exception {
        mockMvc.perform(delete("/api/inventario/{id}", inventario.getId()))
                .andExpect(status().isNoContent());  // espera 204


        Optional<Inventario> eliminado = inventarioRepository.findById(inventario.getId());
        assertTrue(eliminado.isEmpty());
    }
}