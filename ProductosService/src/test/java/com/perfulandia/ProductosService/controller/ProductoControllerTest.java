package com.perfulandia.ProductosService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private Producto producto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        producto = new Producto(1L, "Perfume Test", 5, 9990.0);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testObtenerTodos() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        when(productoService.listarProductos()).thenReturn(productos);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Perfume Test"));
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Perfume Test"));
    }

    @Test
    void testCrear() throws Exception {
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Perfume Test"));
    }

    @Test
    void testActualizar() throws Exception {
        when(productoService.actualizarProducto(eq(1L), any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Perfume Test"));
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
