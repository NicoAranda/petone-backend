package com.petone.stories.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String user;

    private String avatar;

    @Column(nullable = false, length = 1000)
    private String img;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "publication_id")
    private Long publicationId;
}