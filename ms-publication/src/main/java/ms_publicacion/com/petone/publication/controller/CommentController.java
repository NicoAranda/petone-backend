package ms_publicacion.com.petone.publication.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ms_publicacion.com.petone.publication.config.JwtService;
import ms_publicacion.com.petone.publication.model.Comment;
import ms_publicacion.com.petone.publication.service.CommentService;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @GetMapping("/{publicacionId}/comentarios")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long publicacionId) {
        List<Comment> comments = commentService.getCommentsByPublicacion(publicacionId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{publicacionId}/comentarios")
    public ResponseEntity<?> createComment(
            @PathVariable Long publicacionId,
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {

        // Validar token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado"));
        }

        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no identificado"));
        }

        String contenido = requestBody.get("contenido");
        if (contenido == null || contenido.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El contenido es obligatorio"));
        }

        // SIEMPRE obtener el nombre completo desde el servicio de usuarios
        String fullName = obtenerNombreUsuario(userId, token);

        Comment comment = commentService.addComment(publicacionId, userId, contenido, fullName);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @DeleteMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long comentarioId) {
        commentService.deleteComment(comentarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el nombre y apellido del usuario desde el servicio de usuarios
     */
    private String obtenerNombreUsuario(Long userId, String token) {
        // Primero, intentar obtener desde la API de usuarios
        try {
            String apiUrl = "http://localhost:8081/api/usuarios/" + userId;
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("Content-Type", "application/json");
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> usuario = response.getBody();
                Object nombreObj = usuario.get("nombre");
                Object apellidoObj = usuario.get("apellido");
                
                String nombre = nombreObj != null ? nombreObj.toString().trim() : "";
                String apellido = apellidoObj != null ? apellidoObj.toString().trim() : "";
                
                if (!nombre.isEmpty()) {
                    String fullName = (nombre + " " + apellido).trim();
                    System.out.println("✓ Nombre obtenido del servicio de usuarios: " + fullName);
                    return fullName;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠ Error consultando API de usuarios: " + e.getMessage());
        }

        System.out.println("⚠ No se pudo obtener nombre desde API, retornando 'Usuario Desconocido'");
        return "Usuario Desconocido";
    }
}
