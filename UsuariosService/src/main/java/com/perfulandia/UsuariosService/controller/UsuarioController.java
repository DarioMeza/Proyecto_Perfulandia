package com.perfulandia.UsuariosService.controller;

import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    public List<Usuario> listar() {
        
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public Usuario obtener(
            @Parameter(description = "ID del usuario a buscar", required = true)
            @PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente")
    public Usuario crear(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @RequestBody Usuario usuario) {
        return usuarioService.crear(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un usuario existente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para actualizar")
    })
    public Usuario actualizar(
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody Usuario usuario) {
        return usuarioService.actualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario por ID")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    public void eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}

