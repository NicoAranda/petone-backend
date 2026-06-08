package com.petone.stories.service;

import java.util.List;

import com.petone.stories.model.Story;

public interface StoryService {
    Story createStoryFromPublication(Story story);
    List<Story> getActiveStories();
    void deleteStory(Long id);
}
