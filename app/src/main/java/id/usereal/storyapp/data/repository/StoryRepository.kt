package id.usereal.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.data.local.remote_mediator.StoryRemoteMediator
import id.usereal.storyapp.data.local.room.StoryRoomDatabase
import id.usereal.storyapp.data.model.FileUploadResponse
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import id.usereal.storyapp.data.remote.ApiService
import id.usereal.storyapp.utils.EspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class StoryRepository(
    private val apiService: ApiService,
    private val storyRoomDatabase: StoryRoomDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyRoomDatabase, apiService),
            pagingSourceFactory = { storyRoomDatabase.storyDao().getAllStory() }
        ).liveData
    }


    fun getStoryWithLocation(token: String): LiveData<UiState<List<ListStoryItem>>> = liveData {
        emit(UiState.Loading)
        try {
            val response = getApiService(token).getStoriesWithLocation()
            val stories = response.listStory
            Log.d("TestData", "Data: $stories")
            emit(UiState.Success(stories))
        } catch (e: Exception) {
            emit(UiState.Error(e.message.toString()))
        }
    }

    fun uploadImage(imageFile: File, description: String, token: String, lat: String? = null, lon: String? = null) = liveData {
        emit(UiState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val latBody = lat?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonBody = lon?.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile,
        )
        EspressoIdlingResource.increment()
        try {
            val successResponse = getApiService(token).uploadImage(multipartBody, requestBody, latBody, lonBody)
            emit(UiState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(errorResponse.message?.let { UiState.Error(it) })
        } finally {
            EspressoIdlingResource.decrement()
        }

    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyRoomDatabase: StoryRoomDatabase,
            apiService: ApiService
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, storyRoomDatabase)
        }.also { instance = it }
    }
}