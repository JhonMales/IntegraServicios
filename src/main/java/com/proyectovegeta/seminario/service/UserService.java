package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.Usuario;
import com.proyectovegeta.seminario.model.UsuarioMongo;
import com.proyectovegeta.seminario.repository.UsuarioMongoRepository;
import com.proyectovegeta.seminario.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMongoRepository usuarioMongoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;



    public UserService(UsuarioRepository usuarioRepository, UsuarioMongoRepository usuarioMongoRepository, BCryptPasswordEncoder passwordEncoder, ValidationService validationService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMongoRepository = usuarioMongoRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    public Usuario createUser(String name, String email, String password, String rol) {
            try {
                //Checks if the email is valid
                validationService.validateEmail(email);

                //Checks if the email is already registered
                if (usuarioRepository.findByEmail(email) != null) {
                    throw new RuntimeException("El correo ya esta registrado");
                }
                //Checks if the password is valid
                validationService.validatePassword(password);
                //Creates a new user on the relational database
                Usuario usuario = new Usuario();
                usuario.setNombre(name);
                usuario.setRol(rol);

                //Stores the user's email and password on the non-relational database
                String hashedPassword = passwordEncoder.encode(password);
                System.out.println(hashedPassword);
                usuario.setEmail(email);
                usuario.setPassword(hashedPassword);
                usuario.setUsuarioId(usuario.getUsuarioId());
                return usuarioRepository.save(usuario);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException("Error al crear el usuario");
            }
    }



    public void updateUser(Long userID, Optional<String> username, Optional<String> email, Optional<String> password, Optional<String> rol) {
        // Retrieve the user
        Usuario usuario = usuarioRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Update username if present
        username.ifPresent(usuario::setNombre);

        // Update email if present and valid
        email.ifPresent(e -> {
            validationService.validateEmail(e);
            usuario.setEmail(e);
        });
        // Update password if present and valid
        password.ifPresent(p -> {
            validationService.validatePassword(p);
            String hashedPassword = passwordEncoder.encode(p);
            usuario.setPassword(hashedPassword);
        });
        // Update rol if present
        rol.ifPresent(usuario::setRol);

        // Save the updated user
        usuarioRepository.save(usuario);
    }

    public Usuario findUserById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Usuario findUserByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("El correo no esta registrado");
        }
        return usuario;
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }
}