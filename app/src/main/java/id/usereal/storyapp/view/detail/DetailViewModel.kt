package id.usereal.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.usereal.storyapp.data.model.Story
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import kotlinx.coroutines.launch

class DetailViewModel: ViewModel() {
    private val _story = MutableLiveData<Story?>()
    val story: LiveData<Story?> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getStoryById(token: String, storyId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val apiService = getApiService(token)
                val response = apiService.getDetailStory(storyId)
                _story.value = response.story
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}