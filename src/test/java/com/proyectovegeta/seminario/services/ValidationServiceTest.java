package com.proyectovegeta.seminario.services;

import com.proyectovegeta.seminario.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    @Test
    void validatePasswordSuccessfully() {
        assertDoesNotThrow(() -> validationService.validatePassword("Password1"));
    }

    @Test
    void validatePasswordFailsWhenPasswordIsShort() {
        assertThrows(RuntimeException.class, () -> validationService.validatePassword("Pass1"));
    }

    @Test
    void validatePasswordFailsWhenNoUppercaseLetter() {
        assertThrows(RuntimeException.class, () -> validationService.validatePassword("password1"));
    }

    @Test
    void validatePasswordFailsWhenNoLowercaseLetter() {
        assertThrows(RuntimeException.class, () -> validationService.validatePassword("PASSWORD1"));
    }

    @Test
    void validatePasswordFailsWhenNoNumber() {
        assertThrows(RuntimeException.class, () -> validationService.validatePassword("Password"));
    }

    @Test
    void validateEmailSuccessfully() {
        assertDoesNotThrow(() -> validationService.validateEmail("test@example.com"));
    }

    @Test
    void validateEmailFailsWhenNoAtSymbol() {
        assertThrows(RuntimeException.class, () -> validationService.validateEmail("testexample.com"));
    }

    @Test
    void validateEmailFailsWhenNoDomain() {
        assertThrows(RuntimeException.class, () -> validationService.validateEmail("test@"));
    }

    @Test
    void validateEmailFailsWhenNoUsername() {
        assertThrows(RuntimeException.class, () -> validationService.validateEmail("@example.com"));
    }
}