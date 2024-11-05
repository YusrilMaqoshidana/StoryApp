package id.usereal.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.model.Story
import id.usereal.storyapp.data.model.StoryResponse
import id.usereal.storyapp.data.remote.ApiConfig
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import id.usereal.storyapp.data.remote.ApiService

class StoryRepository {

    fun getAllStory(token: String): LiveData<UiState<List<ListStoryItem>>> = liveData {
        emit(UiState.Loading)
        try {
            val response = getApiService(token).getStories()
            val stories = response.listStory
            emit(UiState.Success(stories))
        } catch (e: Exception) {
            emit(UiState.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository()
            }.also { instance = it }
    }
}