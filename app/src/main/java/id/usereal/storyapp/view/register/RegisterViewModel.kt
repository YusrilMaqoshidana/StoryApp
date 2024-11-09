package id.usereal.storyapp.view.register

import androidx.lifecycle.ViewModel
import id.usereal.storyapp.data.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String) = repository.userRegister(name, email, password)
}
