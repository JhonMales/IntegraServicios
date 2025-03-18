package com.proyectovegeta.seminario.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    //Checks if the password has at least 8 characters, one uppercase letter, one lowercase letter and one number
    public void validatePassword(String password) {
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, una mayuscula, una minuscula y un numero");
        }
    }

    //Checks if the email has the correct format
    public void validateEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("El correo no es valido");
        }
    }
}