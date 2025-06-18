package com.perfulandia.InventarioService.service;

import com.perfulandia.InventarioService.model.Inventario;
import com.perfulandia.InventarioService.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventario1 = new Inventario(1L, "COD-123", 10);
    }

    @Test
    void listar_deberiaRetornarListaInventarios() {
        List<Inventario> lista = Arrays.asList(inventario1);
        when(inventarioRepository.findAll()).thenReturn(lista);

        List<Inventario> resultado = inventarioService.listar();

        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaInventario() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario1));

        Optional<Inventario> resultado = inventarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("COD-123", resultado.get().getCodigoProducto());
        verify(inventarioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retornaEmpty() {
        when(inventarioRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Inventario> resultado = inventarioService.obtenerPorId(2L);

        assertFalse(resultado.isPresent());
        verify(inventarioRepository, times(1)).findById(2L);
    }

    @Test
    void guardar_deberiaGuardarInventario() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario1);

        Inventario guardado = inventarioService.guardar(inventario1);

        assertNotNull(guardado);
        assertEquals("COD-123", guardado.getCodigoProducto());
        verify(inventarioRepository, times(1)).save(inventario1);
    }

    @Test
    void actualizar_cuandoExiste_actualizaYRetornaInventario() {
        Inventario inventarioActualizado = new Inventario(null, "COD-456", 20);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario1));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventario resultado = inventarioService.actualizar(1L, inventarioActualizado);

        assertNotNull(resultado);
        assertEquals("COD-456", resultado.getCodigoProducto());
        assertEquals(20, resultado.getCantidadDisponible());
        verify(inventarioRepository, times(1)).findById(1L);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void actualizar_cuandoNoExiste_retornaNull() {
        Inventario inventarioActualizado = new Inventario(null, "COD-456", 20);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.actualizar(1L, inventarioActualizado);

        assertNull(resultado);
        verify(inventarioRepository, times(1)).findById(1L);
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void eliminar_deberiaEliminarInventario() {
        doNothing().when(inventarioRepository).deleteById(1L);

        inventarioService.eliminar(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }
}
