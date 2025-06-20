package com.perfulandia.UsuariosService.integration;

import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testCrearUsuarioYListarUsuarios() throws Exception {
        Usuario nuevoUsuario = new Usuario("Benja", "benja@mail.com", "1234", "ADMIN");

        // Crear usuario con POST
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Benja")))
                .andExpect(jsonPath("$.correo", is("benja@mail.com")))
                .andExpect(jsonPath("$.rol", is("ADMIN")));

        // Listar usuarios para validar que se creó el usuario
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Benja")));
    }

    @Test
    void testObtenerUsuarioPorId() throws Exception {
        Usuario usuario = new Usuario("Ana", "ana@mail.com", "1234", "USER");
        usuario = usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.correo", is("ana@mail.com")))
                .andExpect(jsonPath("$.rol", is("USER")));
    }

    @Test
    void testEliminarUsuario() throws Exception {
        Usuario usuario = new Usuario("Carlos", "carlos@mail.com", "1234", "USER");
        usuario = usuarioRepository.save(usuario);

        mockMvc.perform(delete("/api/usuarios/{id}", usuario.getId()))
                .andExpect(status().isOk());

        // Validar que la lista está vacía después de eliminar
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testActualizarUsuario() throws Exception {
        // Primero guardamos un usuario original en la BD
        Usuario usuarioOriginal = new Usuario("Pedro", "pedro@mail.com", "1234", "USER");
        usuarioOriginal = usuarioRepository.save(usuarioOriginal);

        // Datos actualizados para el usuario (incluye password dummy solo para evitar null)
        Usuario usuarioActualizado = new Usuario("Pedro Actualizado", "pedro_actualizado@mail.com", "4567", "ADMIN");

        // Hacemos la petición PUT para actualizar
        mockMvc.perform(put("/api/usuarios/{id}", usuarioOriginal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Pedro Actualizado")))
                .andExpect(jsonPath("$.correo", is("pedro_actualizado@mail.com")))
                .andExpect(jsonPath("$.rol", is("ADMIN")));

        // Validar que en la BD quedó actualizado
        Usuario usuarioEnBd = usuarioRepository.findById(usuarioOriginal.getId()).orElseThrow();
        assert usuarioEnBd.getNombre().equals("Pedro Actualizado");
        assert usuarioEnBd.getCorreo().equals("pedro_actualizado@mail.com");
        assert usuarioEnBd.getRol().equals("ADMIN");
    }
}
