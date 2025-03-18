package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.UserLoginRequest;
import com.proyectovegeta.seminario.dto.UserLoginResponse;
import com.proyectovegeta.seminario.dto.UserRegistrationRequest;
import com.proyectovegeta.seminario.dto.UserUpdateRequest;
import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.Usuario;
import com.proyectovegeta.seminario.service.AuthenticationService;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import com.proyectovegeta.seminario.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @Mock
    private ServiceUnitManager serviceUnitManager;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void signUpSuccessfully() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("email")
                .password("password")
                .username("username").build();

        userController.SignUp(request);

        verify(userService, times(1)).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void signUpWithException() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
            .username("username")
            .email("email")
            .password("password")
            .build();
        doThrow(new RuntimeException("Error")).when(userService).createUser(anyString(), anyString(), anyString(), anyString());

        ResponseEntity<String> response = userController.SignUp(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error", response.getBody());
    }

    @Test
    public void signInSuccessfully() {
        UserLoginRequest request = UserLoginRequest.builder()
            .username("username")
            .password("password")
            .build();
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);
        usuario.setRol("usuario");
        UserLoginResponse expectedResponse = UserLoginResponse.builder()
                .token("Token")
                .rol("usuario")
                .username(null)
                .serviceUnitId(null)
                .build();
        when(authenticationService.signIn(anyString(), anyString())).thenReturn(usuario);
        when(serviceUnitManager.getCurrentEmployer(anyLong())).thenReturn(new Empleado());
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("Token");

        ResponseEntity<UserLoginResponse> response = userController.SignIn(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void signInWithException() {
        UserLoginRequest request = UserLoginRequest.builder()
            .username("username")
            .password("password")
            .build();
        when(authenticationService.signIn(anyString(), anyString())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<UserLoginResponse> response = userController.SignIn(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void updateUserSuccessfully() {
        UserUpdateRequest request = UserUpdateRequest.builder()
            .username(Optional.of("username"))
            .email(Optional.of("email"))
            .password(Optional.of("password"))
            .build();
        when(authenticationService.validateRequest(anyString(), anyLong())).thenReturn(true);
        doNothing().when(userService).updateUser(any(Long.class), any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class));

        ResponseEntity<String> response = userController.updateUser(request, 1L, "Bearer Token");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User updated successfully!", response.getBody());
    }

    @Test
    public void updateUserUnauthorized() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username(Optional.of("username"))
                .email(Optional.of("email"))
                .password(Optional.of("password"))
                .build();
        when(authenticationService.validateRequest(anyString(), anyLong())).thenReturn(false);

        ResponseEntity<String> response = userController.updateUser(request, 1L, "Bearer Token");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Usuario no autorizado para realizar esta acción", response.getBody());
    }
}