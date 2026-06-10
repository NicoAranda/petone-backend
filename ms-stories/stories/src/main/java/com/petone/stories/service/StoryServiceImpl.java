package com.petone.stories.service;

import com.petone.stories.model.Story;
import com.petone.stories.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository repository;

    @Override
    @Transactional
    public Story createStoryFromPublication(Story story) {
        if (story.getCreatedAt() == null) {
            story.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(story);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Story> getActiveStories() {
        LocalDateTime limit = LocalDateTime.now().minusHours(24);
        return repository.findByCreatedAtAfterOrderByCreatedAtDesc(limit);
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("La historia con id " + id + " no existe.");
        }
        repository.deleteById(id);
    }
}