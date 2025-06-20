package com.perfulandia.UsuariosService.service;

import com.perfulandia.UsuariosService.dto.AuthRequest;
import com.perfulandia.UsuariosService.model.Usuario;
import com.perfulandia.UsuariosService.repository.UsuarioRepository;
import com.perfulandia.UsuariosService.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(AuthRequest authRequest) {
        Usuario usuario = usuarioRepository.findByCorreo(authRequest.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));


        System.out.println("Password recibido: " + authRequest.getPassword());
        System.out.println("Password almacenado: " + usuario.getPassword());

        if (!passwordEncoder.matches(authRequest.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return jwtUtil.generateToken(usuario);
    }
}

