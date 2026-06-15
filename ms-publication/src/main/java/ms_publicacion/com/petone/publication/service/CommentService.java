package ms_publicacion.com.petone.publication.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ms_publicacion.com.petone.publication.model.Comment;
import ms_publicacion.com.petone.publication.model.Publication;
import ms_publicacion.com.petone.publication.repository.CommentRepository;
import ms_publicacion.com.petone.publication.repository.PublicationRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PublicationRepository publicationRepository;

    public Comment addComment(Long publicacionId, Long usuarioId, String contenido, String usuarioNombre) {
        // Validar que la publicación existe
        Publication publication = publicationRepository.findById(publicacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada"));

        // Validar contenido no vacío
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contenido del comentario es obligatorio");
        }

        Comment comment = new Comment();
        comment.setPublicacion(publication);
        comment.setUsuarioId(usuarioId);
        comment.setContenido(contenido.trim());
        comment.setFechaCreacion(new Date());
        comment.setUsuarioNombre(usuarioNombre);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPublicacion(Long publicacionId) {
        // Validar que la publicación existe
        if (!publicationRepository.existsById(publicacionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada");
        }

        return commentRepository.findByPublicacionId(publicacionId)
                .stream()
                .sorted((c1, c2) -> c2.getFechaCreacion().compareTo(c1.getFechaCreacion()))
                .collect(Collectors.toList());
    }

    public void deleteComment(Long comentarioId) {
        Comment comment = commentRepository.findById(comentarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        commentRepository.delete(comment);
    }
}
