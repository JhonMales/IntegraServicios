package com.proyectovegeta.seminario.service;
import com.proyectovegeta.seminario.model.Usuario;
import com.proyectovegeta.seminario.model.UsuarioMongo;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserService userService,
                                 BCryptPasswordEncoder passwordEncoder,
                                 ValidationService validationService,
                                 JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
    }

    //User login
    public Usuario signIn(String email, String password) {
        //Checks if the email is valid
        validationService.validateEmail(email);

        //Checks if the email is registered
        Usuario usuario = userService.findUserByEmail(email);

        //Checks if the password is correct
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("La contraseña es incorrecta");
        }
        Long userId = usuario.getUsuarioId();
        return userService.findUserById(userId);

        //Returns a JWT with the user id and role
        //return jwtUtil.generateToken(user.getUsuarioId().toString(), user.getRol());
    }

    public boolean validateRequest(String jwt, Long id) {
        Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
        return decodedToken.get("rol").equals("admin") || decodedToken.get("id").equals(id.toString());
    }
}
