package id.usereal.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.data.model.FileUploadResponse
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.remote.ApiConfig.getApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

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

    fun uploadImage(imageFile: File, description: String, token: String) = liveData {
        emit(UiState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = getApiService(token).uploadImage(multipartBody, requestBody)
            emit(UiState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(errorResponse.message?.let { UiState.Error(it) })
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