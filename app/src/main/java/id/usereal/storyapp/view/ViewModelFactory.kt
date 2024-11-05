package id.usereal.storyapp.view

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.usereal.storyapp.di.Injection.provideStoryRepository
import id.usereal.storyapp.di.Injection.provideUserRepository
import id.usereal.storyapp.view.main.MainViewModel

object ViewModelFactory {
    fun getInstance(context: Context) = viewModelFactory {
        val storyRepository = provideStoryRepository(context)
        val userRepository = provideUserRepository(context)
        initializer {
            MainViewModel(storyRepository, userRepository)
        }


    }
}