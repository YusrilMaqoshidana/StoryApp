package id.usereal.storyapp.data.repository

import id.usereal.storyapp.data.local.UserPreference
import id.usereal.storyapp.data.model.UserModel
import id.usereal.storyapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun userRegister(name: String, email: String, password: String) = apiService.register(name, email, password)
    suspend fun userLogin(email: String, password: String) = apiService.login(email, password)
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user.copy(isLogin = true))
    }
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

}