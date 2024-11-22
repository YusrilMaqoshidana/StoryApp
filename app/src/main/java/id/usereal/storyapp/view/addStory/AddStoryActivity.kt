@file:Suppress("KotlinConstantConditions")

package id.usereal.storyapp.view.addStory

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.databinding.ActivityAddStoryBinding
import id.usereal.storyapp.utils.reduceFileImage
import id.usereal.storyapp.utils.uriToFile
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.main.MainActivity

@Suppress("KotlinConstantConditions")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        when {
            cameraGranted && (fineLocationGranted || coarseLocationGranted) -> {
                setupInitialLocationAndUI()
                getString(R.string.permission_request_granted).showSnackbar()
            }
            !cameraGranted -> {
                getString(R.string.camera_permission_denied).showSnackbar()
                finish()
            }
            !fineLocationGranted && !coarseLocationGranted -> {
                getString(R.string.location_permission_denied).showSnackbar()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!hasAllPermissions()) {
            requestAllPermissions()
        } else {
            setupInitialLocationAndUI()
        }
        setupToolbar()

        with(binding) {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCamera() }
            buttonAdd.setOnClickListener { uploadImage()  }
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    getLastLocation()
                    Log.d("TestCheckbox", "Centang")
                } else {
                    viewModel.currentLocation = null
                    Log.d("TestCheckbox", "Tidak Centang")
                }
            }
            // Implementasi Animasi
            fadeInAnimation(buttonAdd)
            fadeInAnimation(btnCamera)
            fadeInAnimation(btnGallery)
            fadeInAnimation(edAddDescription)
        }
    }

    // Toolbar setup
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
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

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        viewModel.currentImageUri = getImageUri(this)
        viewModel.currentImageUri?.let {
            launcherIntentCamera.launch(it)
        } ?: getString(R.string.unable_to_create_image_uri).showSnackbar()
    }

    private fun uploadImage() {
        viewModel.currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            val lat = viewModel.currentLocation?.latitude?.toString()
            val lon = viewModel.currentLocation?.longitude?.toString()

            if (description.isEmpty()) {
                binding.edAddDescription.error = getString(R.string.error_description_null)
            } else {
                viewModel.uploadImage(imageFile, description, lat, lon).observe(this) { result ->
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getImageUri(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, getString(R.string.new_image))
            put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.image_from_camera))
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: throw IllegalStateException(getString(R.string.failed_to_create_new_image_uri))
    }

    private fun hasAllPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            REQUIRED_CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestAllPermissions() {
        multiplePermissionsLauncher.launch(
            arrayOf(
                REQUIRED_CAMERA_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun setupInitialLocationAndUI() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        showImage()
        getLastLocation()
    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.currentLocation = location
                if (location != null) {
                    getString(R.string.location_acquired).showSnackbar()
                } else {
                    getString(R.string.location_not_found).showSnackbar()
                }
            }
        } catch (e: SecurityException) {
            handleLocationPermissionError()
        }
    }

    private fun handleLocationPermissionError() {
        getString(R.string.location_permission_denied).showSnackbar()
        binding.checkBox.isChecked = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUIRED_CAMERA_PERMISSION = Manifest.permission.CAMERA
    }
}