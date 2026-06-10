package com.petone.stories.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petone.stories.model.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long>{
    List<Story> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTimeTime);
}
