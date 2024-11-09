package id.usereal.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import id.usereal.storyapp.data.model.UserModel
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun getAllStory(token: String) = storyRepository.getAllStory(token)


}