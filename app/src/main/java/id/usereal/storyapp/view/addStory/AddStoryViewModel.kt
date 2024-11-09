package id.usereal.storyapp.view.addStory

import android.net.Uri
import androidx.lifecycle.ViewModel
import id.usereal.storyapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    var currentImageUri: Uri? = null

    fun uploadImage(file: File, description: String, token: String) =
        storyRepository.uploadImage(file, description, token)


}