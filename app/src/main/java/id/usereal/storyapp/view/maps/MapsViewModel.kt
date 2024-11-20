package id.usereal.storyapp.view.maps

import androidx.lifecycle.ViewModel
import id.usereal.storyapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryWithLocation(token: String) = storyRepository.getStoryWithLocation(token)
}