package id.usereal.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.data.local.UserPreference
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository

class MainViewModel(private val storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {
    fun getAllStory(token : String) = storyRepository.getAllStory(token)
}