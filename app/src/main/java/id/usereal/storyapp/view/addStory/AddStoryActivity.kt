package id.usereal.storyapp.view.addStory

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.databinding.ActivityAddStoryBinding
import id.usereal.storyapp.utils.reduceFileImage
import id.usereal.storyapp.utils.uriToFile
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.detail.DetailActivity

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getString(R.string.permission_request_granted).showSnackbar()
            } else {
                getString(R.string.permission_request_denied).showSnackbar()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        setupToolbar()
        val token = intent.getStringExtra(DetailActivity.EXTRA_TOKEN)
        showImage()
        with(binding) {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCamera() }
            buttonAdd.setOnClickListener { token?.let { it1 -> uploadImage(it1) } }
            // Implementasi Animasi
            fadeInAnimation(buttonAdd)
            fadeInAnimation(btnCamera)
            fadeInAnimation(btnGallery)
            fadeInAnimation(edAddDescription)
        }
    }

    // Animasi untuk elemen-elemen UI
    private fun fadeInAnimation(view: View, duration: Long = 2000) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        viewModel.currentImageUri = getImageUri(this)
        viewModel.currentImageUri?.let {
            launcherIntentCamera.launch(it)
        } ?: getString(R.string.unable_to_create_image_uri).showSnackbar()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            viewModel.currentImageUri = null
            getString(R.string.image_capture_failed).showSnackbar()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            showImage()
        } else {
            getString(R.string.no_media_selected).showSnackbar()
        }
    }

    private fun uploadImage(token: String) {
        viewModel.currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            if (description.isEmpty()) {
                binding.edAddDescription.error = getString(R.string.error_description_null)
            } else {
                viewModel.uploadImage(imageFile, description, token).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is UiState.Loading -> {
                                showLoading(true)
                            }

                            is UiState.Success -> {
                                result.data.message?.showSnackbar()
                                showLoading(false)
                                moveToMain()
                            }

                            is UiState.Error -> {
                                result.error.showSnackbar()
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        } ?: getString(R.string.error_getting_image_uri).showSnackbar()
    }

    private fun showImage() {
        viewModel.currentImageUri?.let {
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun String.showSnackbar() {
        Snackbar.make(binding.root, this, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun moveToMain() {
        finish()
    }

    private fun getImageUri(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Image")
            put(MediaStore.Images.Media.DESCRIPTION, "Image from Camera")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: throw IllegalStateException(getString(R.string.failed_to_create_new_image_uri))
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_TOKEN = "token"
    }
}