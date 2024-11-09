package id.usereal.storyapp.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.databinding.ActivityRegisterBinding
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()

        // Implementasi Animasi
        with(binding){
            slideInFromLeft(tvTitle, 1000)
            slideInFromLeft(tvDescription, 1400)
            fadeInAnimation(edRegisterName, 2000)
            fadeInAnimation(emailLayout, 2000)
            fadeInAnimation(passwordLayout, 1000)
            fadeInAnimation(btnRegister, 1400)
        }
    }
    //Animasi untuk elemen-elemen UI
    private fun fadeInAnimation(view: View, duration: Long = 1000) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
    // Animasi untuk elemen-elemen UI
    private fun slideInFromLeft(view: View, duration: Long = 1000) {
        view.translationX = -500f
        view.visibility = View.VISIBLE
        view.animate()
            .translationX(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun setupBinding() {
        with(binding) {
            edRegisterEmail.setParentLayout(emailLayout)
            edRegisterPassword.setParentLayout(passwordLayout)
            btnLogin.setOnClickListener {
                moveToLogin()
            }
            btnRegister.setOnClickListener {
                setupRegister()
            }
        }
    }

    private fun setupRegister() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.registerUser(name, email, password).observeForever {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(getString(R.string.register_succes))
                    moveToLogin()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(it.error)
                }
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
