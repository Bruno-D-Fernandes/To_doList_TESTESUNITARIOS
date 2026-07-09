package edu.jUnitEMosquito.service;

import edu.jUnitEMosquito.config.security.JwtService;
import edu.jUnitEMosquito.dto.usuario.LoginRequestDTO;
import edu.jUnitEMosquito.dto.usuario.RegisterRequestDTO;
import edu.jUnitEMosquito.exception.usuario.DadoInvalidoException;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

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

        // Generalizar a exception deixa o código mais acoplado?
        boolean emailValido = Pattern.matches("^[\\w]{5,55}[@][\\w]{3,9}[.][\\w]{2,3}[.]?[\\w]{2,3}?$", request.email());
        if(!emailValido) throw new DadoInvalidoException("Email");

        boolean passwordValido = Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$$", request.password());
        if(!passwordValido) throw new DadoInvalidoException("Senha");



        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password());
        authenticationManager.authenticate(authenticationToken);

        return jwtService.generateToken(request.email());
    }


    public void register(RegisterRequestDTO registerRequestDTO){

        // Generalizar a exception deixa o código mais acoplado?
        boolean nomeValido = Pattern.matches("^(?=^[^ ]+(?:[^ ]* ){0,4}[^ ]+$)[A-Za-z0-9 _]{5,25}$", registerRequestDTO.nome());
        if(!nomeValido) throw new DadoInvalidoException("Nome");

        boolean emailValido = Pattern.matches("^[\\w]{5,55}[@][\\w]{3,9}[.][\\w]{2,3}[.]?[\\w]{2,3}?$", registerRequestDTO.email());
        if(!emailValido) throw new DadoInvalidoException("Email");

        boolean passwordValido = Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$$", registerRequestDTO.password());
        if(!passwordValido) throw new DadoInvalidoException("Senha");


        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDTO.password());
        Usuario usuario = new Usuario(registerRequestDTO.nome(), registerRequestDTO.email().toLowerCase(), encryptedPassword);
        usuarioRepository.save(usuario);
    }
}
