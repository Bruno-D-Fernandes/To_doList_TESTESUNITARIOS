package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.config.security.JwtService;
import edu.jUnitEMosquito.dto.usuario.LoginRequestDTO;
import edu.jUnitEMosquito.dto.usuario.RegisterRequestDTO;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String login(LoginRequestDTO request) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password());
        authenticationManager.authenticate(authenticationToken);

        return jwtService.generateToken(request.email());
    }


    public void register(RegisterRequestDTO registerRequestDTO){
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDTO.password());
        Usuario usuario = new Usuario(registerRequestDTO.nome(), registerRequestDTO.email().toLowerCase(), encryptedPassword);
        usuarioRepository.save(usuario);
    }
}
