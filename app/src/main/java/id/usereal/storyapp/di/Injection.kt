package id.usereal.storyapp.di

import android.content.Context
import id.usereal.storyapp.data.local.UserPreference
import id.usereal.storyapp.data.local.dataStore
import id.usereal.storyapp.data.remote.ApiConfig
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(): StoryRepository {
        return StoryRepository.getInstance()
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val token = user.token
        val apiService = getApiService(token)
        return UserRepository.getInstance(pref, apiService)
    }
}