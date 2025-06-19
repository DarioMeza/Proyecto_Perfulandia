package com.perfulandia.PedidosService.service;

import com.perfulandia.PedidosService.dto.ProductoDTO;
import com.perfulandia.PedidosService.dto.UsuarioDTO;
import com.perfulandia.PedidosService.model.Pedido;
import com.perfulandia.PedidosService.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerTodos() {
        Pedido pedido1 = new Pedido(1L, "Cliente1", LocalDate.now(), "Pendiente", 100.0, 10L);
        Pedido pedido2 = new Pedido(2L, "Cliente2", LocalDate.now(), "Entregado", 200.0, 20L);
        List<Pedido> lista = Arrays.asList(pedido1, pedido2);

        when(pedidoRepository.findAll()).thenReturn(lista);

        List<Pedido> resultado = pedidoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorIdExistente() {
        Pedido pedido = new Pedido(1L, "Cliente1", LocalDate.now(), "Pendiente", 100.0, 10L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Cliente1", resultado.get().getCliente());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    public void testObtenerPorIdNoExistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoService.obtenerPorId(1L);

        assertFalse(resultado.isPresent());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    public void testCrearPedido() {
        Pedido pedido = new Pedido(null, "Cliente1", LocalDate.now(), "Pendiente", 150.0, 5L);
        Pedido pedidoGuardado = new Pedido(1L, "Cliente1", LocalDate.now(), "Pendiente", 150.0, 5L);

        when(pedidoRepository.save(pedido)).thenReturn(pedidoGuardado);

        Pedido resultado = pedidoService.crear(pedido);

        assertNotNull(resultado.getId());
        assertEquals(1L, resultado.getId());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    public void testActualizarPedidoExistente() {
        Pedido pedidoExistente = new Pedido(1L, "Cliente1", LocalDate.now(), "Pendiente", 100.0, 10L);
        Pedido pedidoActualizado = new Pedido(null, "Cliente2", LocalDate.now(), "Entregado", 200.0, 20L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = pedidoService.actualizar(1L, pedidoActualizado);

        assertEquals("Cliente2", resultado.getCliente());
        assertEquals("Entregado", resultado.getEstado());
        assertEquals(200.0, resultado.getTotal());
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedidoExistente);
    }

    @Test
    public void testActualizarPedidoNoExistente() {
        Pedido pedidoActualizado = new Pedido(null, "Cliente2", LocalDate.now(), "Entregado", 200.0, 20L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.actualizar(1L, pedidoActualizado);
        });

        assertEquals("Pedido no encontrado con id: 1", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    public void testEliminarPedido() {
        doNothing().when(pedidoRepository).deleteById(1L);

        pedidoService.eliminar(1L);

        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testConsultarProductoPorId() {
        ProductoDTO productoMock = new ProductoDTO(10L, "Producto1", 99.99);
        String urlEsperada = "http://localhost:8081/api/productos/10";

        when(restTemplate.getForObject(urlEsperada, ProductoDTO.class)).thenReturn(productoMock);

        ProductoDTO resultado = pedidoService.consultarProductoPorId(10L);

        assertNotNull(resultado);
        assertEquals("Producto1", resultado.getNombre());

        verify(restTemplate, times(1)).getForObject(urlEsperada, ProductoDTO.class);
    }

    @Test
    public void testConsultarUsuarioPorId() {
        UsuarioDTO usuarioMock = new UsuarioDTO(5L, "Usuario1", "usuario1@example.com");
        String urlEsperada = "http://localhost:8082/api/usuarios/5";

        when(restTemplate.getForObject(urlEsperada, UsuarioDTO.class)).thenReturn(usuarioMock);

        UsuarioDTO resultado = pedidoService.consultarUsuarioPorId(5L);

        assertNotNull(resultado);
        assertEquals("Usuario1", resultado.getNombre());

        verify(restTemplate, times(1)).getForObject(urlEsperada, UsuarioDTO.class);
    }
}
