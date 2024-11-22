package id.usereal.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.model.UserModel
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    val stories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)

}