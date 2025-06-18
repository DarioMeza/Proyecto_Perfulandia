package com.perfulandia.UsuariosService.controller;

import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;  // para convertir objetos a JSON

    @Test
    void listarUsuarios_retornaLista() throws Exception {
        Usuario u1 = new Usuario("Benja", "benja@mail.com", "ADMIN");
        u1.setId(1L);
        Usuario u2 = new Usuario("Ana", "ana@mail.com", "USER");
        u2.setId(2L);

        when(usuarioService.obtenerTodos()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Benja"))
                .andExpect(jsonPath("$[1].correo").value("ana@mail.com"));
    }

    @Test
    void obtenerUsuario_porId_retornaUsuario() throws Exception {
        Usuario usuario = new Usuario("Benja", "benja@mail.com", "ADMIN");
        usuario.setId(1L);

        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Benja"));
    }

    @Test
    void crearUsuario_retornaUsuarioCreado() throws Exception {
        Usuario nuevoUsuario = new Usuario("Benja", "benja@mail.com", "ADMIN");
        nuevoUsuario.setId(1L);

        when(usuarioService.crear(any(Usuario.class))).thenReturn(nuevoUsuario);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Benja"));
    }

    @Test
    void eliminarUsuario_retorna204() throws Exception {
        Mockito.doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk()); // en tu controlador no retorna void ni status 204 explícito, usa isOk()
    }
}
