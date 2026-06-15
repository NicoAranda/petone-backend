package com.petone.users.ms_users.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.petone.users.ms_users.model.User;
import com.petone.users.ms_users.service.UserService;

public class UserControllerTest {

    private UserService service;
    private UserController controller;

    @BeforeEach
    void setup() {
        service = Mockito.mock(UserService.class);
        controller = new UserController(service);
    }

    @Test
    void listar_usuarios_devuelve_ok() {
        User u = User.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@example.com").password("secret").rol("USER").rut("11111111-1").telefono("987654321").build();
        List<User> list = List.of(u);
        when(service.viewAllUsers()).thenReturn(list);

        ResponseEntity<List<User>> resp = controller.listarUsuarios();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Juan", resp.getBody().get(0).getNombre());
    }

    @Test
    void mostrar_usuario_por_id_devuelve_ok() {
        User u = User.builder().id(2L).nombre("Ana").apellido("Lopez").email("ana@example.com").password("secret").rol("USER").rut("22222222-2").telefono("912345678").build();
        when(service.viewUserById(2L)).thenReturn(u);

        ResponseEntity<User> resp = controller.mostrarUsuario(2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Ana", resp.getBody().getNombre());
    }

    @Test
    void actualizar_usuario_devuelve_ok() {
        User input = User.builder().nombre("Pablo").apellido("Gomez").email("pablo@example.com").password("pass").rol("USER").rut("33333333-3").telefono("900900900").build();
        User updated = User.builder().id(3L).nombre("Pablo").apellido("Gomez").email("pablo@example.com").password("pass").rol("USER").rut("33333333-3").telefono("900900900").build();
        when(service.updateUserById(3L, input)).thenReturn(updated);

        ResponseEntity<User> resp = controller.actualizarUsuario(3L, input);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(3L, resp.getBody().getId().longValue());
    }

    @Test
    void eliminar_usuario_devuelve_no_content() {
        doNothing().when(service).deleteUser(4L);

        ResponseEntity<Void> resp = controller.eliminarUsuario(4L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    void registrar_devuelve_created() {
        User input = User.builder().nombre("Luis").apellido("Gomez").email("luis@example.com").password("pass123").rol("CLIENTE").rut("44444444-4").telefono("900900900").build();
        User saved = User.builder().id(5L).nombre("Luis").apellido("Gomez").email("luis@example.com").password("pass123").rol("CLIENTE").rut("44444444-4").telefono("900900900").build();
        when(service.addUser(input)).thenReturn(saved);

        ResponseEntity<User> resp = controller.registrar(input);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(5L, resp.getBody().getId().longValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    void login_devuelve_token() {
        User dto = User.builder().email("ana@example.com").password("secret").build();
        when(service.login(dto)).thenReturn("jwt.token.value");

        ResponseEntity<?> resp = controller.login(dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Map<String, String> body = (Map<String, String>) resp.getBody();
        assertEquals("jwt.token.value", body.get("token"));
    }
}
