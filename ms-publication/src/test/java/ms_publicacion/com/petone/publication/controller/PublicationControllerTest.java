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
}
