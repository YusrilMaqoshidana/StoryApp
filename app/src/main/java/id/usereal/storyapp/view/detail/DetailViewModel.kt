package id.usereal.storyapp.view.detail

import androidx.lifecycle.ViewModel
import id.usereal.storyapp.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun detailStory(id: String) = storyRepository.getDetailStory(id)
}