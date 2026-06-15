package ms_publicacion.com.petone.publication.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ms_publicacion.com.petone.publication.config.JwtService;
import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.service.PublicationService;

public class PublicationControllerTest {

    private PublicationService service;
    private PublicationController controller;
    JwtService jwtService = Mockito.mock(JwtService.class);

    @BeforeEach
    void setup() {
        service = Mockito.mock(PublicationService.class);
        controller = new PublicationController(service, jwtService);
    }

    @Test
    void listar_publicaciones_devuelve_ok() {
        Publication p = new Publication(
                1L,
                6L,
                "Firulais",
                "Santiago",
                "Perro",
                "Macho",
                "ACTIVA",
                "Buen perro",
                new Date(),
                Arrays.asList("/img/1.jpg"));
        List<Publication> list = List.of(p);
        when(service.viewAll()).thenReturn(list);

        ResponseEntity<List<Publication>> resp = controller.listar();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Firulais", resp.getBody().get(0).getNombre());
    }

    @Test
    void obtener_publicacion_por_id_devuelve_ok() {
        Publication p = new Publication(2L, 6L, "Rex", "Valparaiso", "Perro", "Macho", "ACTIVA", "Amigable", new Date(),
                Arrays.asList("/img/dog.jpg"));
        when(service.viewById(2L)).thenReturn(p);

        ResponseEntity<Publication> resp = controller.obtener(2L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Rex", resp.getBody().getNombre());
    }

    @Test
    void actualizar_publicacion_devuelve_ok() {
        Publication input = new Publication(null, 6L, "Camila", "Santiago", "Gato", "Hembra", "ACTIVA", "Actualizada",
                new Date(), Arrays.asList());
        Publication updated = new Publication(3L, 6L, "Camila", "Santiago", "Gato", "Hembra", "ACTIVA", "Actualizada",
                new Date(), Arrays.asList());
        when(service.updateById(3L, input)).thenReturn(updated);

        ResponseEntity<Publication> resp = controller.actualizar(3L, input);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(3L, resp.getBody().getId().longValue());
    }

    @Test
    void eliminar_publicacion_devuelve_no_content() {
        doNothing().when(service).delete(4L);

        ResponseEntity<Void> resp = controller.eliminar(4L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    void crear_publicacion_devuelve_created() {
        Publication input = new Publication(null, 6L, "Firulais", "Santiago", "Perro", "Macho", "ACTIVA", "Buen perro", new Date(), Arrays.asList());
        Publication saved = new Publication(10L, 6L, "Firulais", "Santiago", "Perro", "Macho", "ACTIVA", "Buen perro", new Date(), Arrays.asList());
        when(service.addPublication(input)).thenReturn(saved);

        ResponseEntity<Publication> resp = controller.crear(input);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(10L, resp.getBody().getId().longValue());
    }

    @Test
    void crear_con_imagenes_devuelve_created_o_unauthorized() {
        jakarta.servlet.http.HttpServletRequest req = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer testtoken");
        when(jwtService.extractUserId("testtoken")).thenReturn(6L);

        Publication saved = new Publication(11L, 6L, "Firulais", "Santiago", "Perro", "Macho", "ACTIVA", "Buen perro", new Date(), Arrays.asList("/img/1.jpg"));
        when(service.crearConImagenes(Mockito.any(Publication.class), Mockito.anyList())).thenReturn(saved);

        ResponseEntity<Publication> resp = controller.crearConImagenes(req, "Firulais", "Santiago", "Perro", "Macho", "ACTIVA", "Buen perro", List.of());
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(11L, resp.getBody().getId().longValue());

        // Caso sin header -> unauthorized
        jakarta.servlet.http.HttpServletRequest req2 = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        when(req2.getHeader("Authorization")).thenReturn(null);
        ResponseEntity<Publication> resp2 = controller.crearConImagenes(req2, "n", "u", "e", "s", "e", "d", List.of());
        assertEquals(HttpStatus.UNAUTHORIZED, resp2.getStatusCode());
    }
}
