package id.usereal.storyapp.di

import android.content.Context
import id.usereal.storyapp.data.local.preference.UserPreference
import id.usereal.storyapp.data.local.preference.dataStore
import id.usereal.storyapp.data.local.room.StoryRoomDatabase
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val token = user.token
        val apiService = getApiService(token)
        val storyRoomDatabase = StoryRoomDatabase.getInstance(context)
        return StoryRepository.getInstance(storyRoomDatabase ,apiService)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val token = user.token
        val apiService = getApiService(token)
        return UserRepository.getInstance(pref, apiService)
    }
}