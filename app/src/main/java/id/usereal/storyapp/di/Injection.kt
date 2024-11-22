package id.usereal.storyapp.di

import android.content.Context
import id.usereal.storyapp.data.local.preference.UserPreference
import id.usereal.storyapp.data.local.preference.dataStore
import id.usereal.storyapp.data.local.room.StoryRoomDatabase
import id.usereal.storyapp.data.remote.ApiConfig
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig().getApiService()
        val storyRoomDatabase = StoryRoomDatabase.getInstance(context)
        return StoryRepository.getInstance(storyRoomDatabase ,apiService, pref)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig().getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}