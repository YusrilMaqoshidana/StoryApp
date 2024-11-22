package id.usereal.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import id.usereal.storyapp.di.Injection.provideStoryRepository
import id.usereal.storyapp.di.Injection.provideUserRepository
import id.usereal.storyapp.view.addStory.AddStoryViewModel
import id.usereal.storyapp.view.detail.DetailViewModel
import id.usereal.storyapp.view.login.LoginViewModel
import id.usereal.storyapp.view.main.MainViewModel
import id.usereal.storyapp.view.maps.MapsViewModel
import id.usereal.storyapp.view.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                return@synchronized INSTANCE ?: ViewModelFactory(
                    userRepository = provideUserRepository(context),
                    storyRepository = provideStoryRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}