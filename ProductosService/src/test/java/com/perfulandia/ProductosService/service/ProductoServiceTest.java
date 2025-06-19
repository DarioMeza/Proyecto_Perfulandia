package com.perfulandia.ProductosService.service;

import com.perfulandia.ProductosService.model.Producto;
import com.perfulandia.ProductosService.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producto = new Producto(1L, "Perfume X", 10, 19990.0);
    }

    @Test
    void testListarProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(1, resultado.size());
        assertEquals("Perfume X", resultado.get(0).getNombre());
    }

    @Test
    void testObtenerProductoPorId() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.obtenerProductoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Perfume X", resultado.get().getNombre());
    }

    @Test
    void testGuardarProducto() {
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.guardarProducto(producto);

        assertNotNull(resultado);
        assertEquals("Perfume X", resultado.getNombre());
    }

    @Test
    void testActualizarProductoExistente() {
        Producto actualizado = new Producto(1L, "Perfume Y", 20, 14990.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        Producto resultado = productoService.actualizarProducto(1L, actualizado);

        assertNotNull(resultado);
        assertEquals("Perfume Y", resultado.getNombre());
        assertEquals(20, resultado.getStock());
    }

    @Test
    void testActualizarProductoInexistente() {
        Producto actualizado = new Producto(1L, "Perfume Y", 20, 14990.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        Producto resultado = productoService.actualizarProducto(1L, actualizado);

        assertNull(resultado);
    }

    @Test
    void testEliminarProducto() {
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }
}
