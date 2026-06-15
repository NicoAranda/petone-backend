package ms_publicacion.com.petone.publication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.repository.PublicationRepository;

@ExtendWith(MockitoExtension.class)
public class PublicationServiceTest {

    @Mock
    private PublicationRepository repo;

    @Mock
    private SupabaseStorageService storageService;

    @InjectMocks
    private PublicationService service;

    @Test
    void crearConImagenes_sube_imagenes_y_guarda() throws Exception {
        Publication dto = new Publication();
        dto.setNombre("Firulais");
        dto.setUserId(6L);
        dto.setDescripcion("Buen perro");

        MultipartFile f1 = mock(MultipartFile.class);
        MultipartFile f2 = mock(MultipartFile.class);

        when(storageService.uploadFile(f1)).thenReturn("/img/1.jpg");
        when(storageService.uploadFile(f2)).thenReturn("/img/2.jpg");

        when(repo.save(any(Publication.class))).thenAnswer(invocation -> {
            Publication p = invocation.getArgument(0);
            p.setId(20L);
            return p;
        });

        Publication saved = service.crearConImagenes(dto, List.of(f1, f2));

        assertNotNull(saved);
        assertEquals(20L, saved.getId().longValue());
        assertEquals(2, saved.getFotos().size());
        verify(storageService, times(1)).uploadFile(f1);
        verify(storageService, times(1)).uploadFile(f2);
    }

    @Test
    void addPublication_sets_defaults_and_saves() {
        Publication p = new Publication();
        p.setDescripcion("desc");

        when(repo.save(any(Publication.class))).thenAnswer(invocation -> {
            Publication arg = invocation.getArgument(0);
            arg.setId(30L);
            return arg;
        });

        Publication saved = service.addPublication(p);

        assertNotNull(saved.getFechaPublicacion());
        assertEquals("ACTIVA", saved.getEstado());
        assertEquals(30L, saved.getId().longValue());
    }
}
