package edu.jUnitEMosquito.services;

import edu.jUnitEMosquito.config.security.JwtService;
import edu.jUnitEMosquito.dto.usuario.LoginRequestDTO;
import edu.jUnitEMosquito.dto.usuario.RegisterRequestDTO;
import edu.jUnitEMosquito.exception.usuario.DadoInvalidoException;
import edu.jUnitEMosquito.model.Usuario;
import edu.jUnitEMosquito.repository.UsuarioRepository;
import edu.jUnitEMosquito.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @InjectMocks
    UsuarioService usuarioService;

    @Nested
    class LoginTests {

        @Test
        @DisplayName("login retorna token quando credenciais válidas")
        void loginSuccess() {
            LoginRequestDTO dto = new LoginRequestDTO("User01@dom.com", "Abc1@");

            when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
            when(jwtService.generateToken("User01@dom.com")).thenReturn("tok");

            String token = usuarioService.login(dto);

            assertEquals("tok", token);
            verify(authenticationManager, times(1)).authenticate(any());
            verify(jwtService, times(1)).generateToken("User01@dom.com");
        }

        @Test
        @DisplayName("lança exceção quando email inválido")
        void loginInvalidEmail() {
            LoginRequestDTO dto = new LoginRequestDTO("a@b.c", "Abc1@");

            assertThrows(DadoInvalidoException.class, () -> usuarioService.login(dto));
        }

        @Test
        @DisplayName("lança exceção quando senha inválida")
        void loginInvalidPassword() {
            LoginRequestDTO dto = new LoginRequestDTO("User01@dom.com", "abc");

            assertThrows(DadoInvalidoException.class, () -> usuarioService.login(dto));
        }
    }

    @Nested
    class RegisterTests {

        @Test
        @DisplayName("registra usuário com dados válidos")
        void registerSuccess() {
            RegisterRequestDTO dto = new RegisterRequestDTO("NomeUsr","user02@dom.com","Abc1@");

            usuarioService.register(dto);

            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("lança exceção quando nome inválido")
        void registerInvalidName() {
            RegisterRequestDTO dto = new RegisterRequestDTO("a","user02@dom.com","Abc1@");

            assertThrows(DadoInvalidoException.class, () -> usuarioService.register(dto));
        }

        @Test
        @DisplayName("lança exceção quando email inválido")
        void registerInvalidEmail() {
            RegisterRequestDTO dto = new RegisterRequestDTO("NomeUsr","a@b.c","Abc1@");

            assertThrows(DadoInvalidoException.class, () -> usuarioService.register(dto));
        }

        @Test
        @DisplayName("lança exceção quando senha inválida")
        void registerInvalidPassword() {
            RegisterRequestDTO dto = new RegisterRequestDTO("NomeUsr","user02@dom.com","abc");

            assertThrows(DadoInvalidoException.class, () -> usuarioService.register(dto));
        }
    }
}
