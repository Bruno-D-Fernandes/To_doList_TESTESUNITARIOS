package edu.jUnitEMosquito.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Service
public class JwtService {

    public final String SECRET = "dasdhjadlsahdadjwdpoi10893702837adasda!_";

    public String generateToken(String username){
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer("meu-backEnd")
                .withSubject(username)
                .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);

    }

    public String validateToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (RuntimeException e){
            return "";
        }
    }

}
