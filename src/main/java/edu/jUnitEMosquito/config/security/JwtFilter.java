package edu.jUnitEMosquito.config.security;

import edu.jUnitEMosquito.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UsuarioRepository usuarioRepository;

    @Autowired
    public JwtFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request.getHeader("Authorization"));

        if(token != null){
            String email = jwtService.validateToken(token);
            UserDetails byUsername = usuarioRepository.findByEmail(email);

            Authentication authentication = new UsernamePasswordAuthenticationToken(byUsername, null, byUsername.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }

    public String getToken(String authorization){
        if(authorization == null || authorization.length() < 7) return null;
        return authorization.replace("Bearer ", "");
    }
}
