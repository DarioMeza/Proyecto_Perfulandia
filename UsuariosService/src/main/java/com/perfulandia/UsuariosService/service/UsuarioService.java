package com.perfulandia.UsuariosService.service;

import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepo.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    public Usuario crear(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario nuevoUsuario) {
        return usuarioRepo.findById(id)
                .map(u -> {
                    u.setNombre(nuevoUsuario.getNombre());
                    u.setCorreo(nuevoUsuario.getCorreo());
                    u.setRol(nuevoUsuario.getRol());
                    return usuarioRepo.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminar(Long id) {
        usuarioRepo.deleteById(id);
    }
}
