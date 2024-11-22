package id.usereal.storyapp.view.login

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
import id.usereal.storyapp.data.model.UserModel
import id.usereal.storyapp.databinding.ActivityLoginBinding
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.main.MainActivity
import id.usereal.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()
        // Implementasi Animasi
        with(binding) {
            slideInFromLeft(tvTitle, 1000)
            slideInFromLeft(tvDescription, 1400)
            fadeInAnimation(emailLayout, 2000)
            fadeInAnimation(passwordLayout, 2000)
            fadeInAnimation(btnLogin, 1400)
        }
    }

    // Animasi untuk elemen-elemen UI
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
            edLoginEmail.setParentLayout(emailLayout)
            edLoginPassword.setParentLayout(passwordLayout)
            btnRegister.setOnClickListener {
                moveToRegister()
            }
            btnLogin.setOnClickListener {
                setupLogin()
            }
        }
    }

    private fun setupLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        viewModel.login(email, password).observeForever {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(getString(R.string.login_succes))
                    val user = it.data.loginResult
                    viewModel.saveSession(UserModel(user?.name ?: "", user?.token ?: "", true))
                    moveToMain()
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(it.error)
                }

            }
        }

    }

    private fun moveToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}