package com.perfulandia.UsuariosService.service;

import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        // Mock para repositorio y passwordEncoder
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        // Inyectamos ambos al servicio
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);

        // Mock para que encode devuelva la misma cadena que recibe
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void obtenerTodos_retornaListaUsuarios() {
        Usuario u1 = new Usuario("Benja", "benja@mail.com", "ADMIN");
        Usuario u2 = new Usuario("Ana", "ana@mail.com", "USER");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> resultado = usuarioService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Benja", resultado.get(0).getNombre());
        assertEquals("Ana", resultado.get(1).getNombre());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_usuarioExistente_retornaUsuario() {
        Usuario usuario = new Usuario("Benja", "benja@mail.com", "ADMIN");
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Benja", resultado.get().getNombre());

        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_usuarioNoExistente_retornaEmpty() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());

        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void crear_usuarioNuevo_retornaUsuarioCreado() {
        Usuario nuevoUsuario = new Usuario("Benja", "benja@mail.com", "ADMIN");

        when(usuarioRepository.save(nuevoUsuario)).thenReturn(nuevoUsuario);

        Usuario resultado = usuarioService.crear(nuevoUsuario);

        assertNotNull(resultado);
        assertEquals("Benja", resultado.getNombre());

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    void actualizar_usuarioExistente_actualizaYRetornaUsuario() {
        Usuario usuarioExistente = new Usuario("Benja", "benja@mail.com", "ADMIN");
        usuarioExistente.setId(1L);

        Usuario datosActualizados = new Usuario("Benjamín", "benja123@mail.com", "USER");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.actualizar(1L, datosActualizados);

        assertEquals("Benjamín", resultado.getNombre());
        assertEquals("benja123@mail.com", resultado.getCorreo());
        assertEquals("USER", resultado.getRol());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void actualizar_usuarioNoExistente_lanzaExcepcion() {
        Usuario datosActualizados = new Usuario("Benjamín", "benja123@mail.com", "USER");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizar(99L, datosActualizados);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void eliminar_llamaDeleteById() {
        Long id = 1L;

        doNothing().when(usuarioRepository).deleteById(id);

        usuarioService.eliminar(id);

        verify(usuarioRepository, times(1)).deleteById(id);
    }
}
