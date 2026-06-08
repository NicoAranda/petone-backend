package com.petone.stories.controller;

import com.petone.stories.model.Story;
import com.petone.stories.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/historias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StoryController {

    private final StoryService service;

    @PostMapping
    public ResponseEntity<Story> crearHistoria(@RequestBody Story story) {
        Story nuevaHistoria = service.createStoryFromPublication(story);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaHistoria);
    }

    @GetMapping
    public ResponseEntity<List<Story>> obtenerHistoriasActivas() {
        return ResponseEntity.ok(service.getActiveStories());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistoria(@PathVariable Long id) {
        service.deleteStory(id);
        return ResponseEntity.noContent().build();
    }
}