package id.usereal.storyapp.view.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.usereal.storyapp.R
import id.usereal.storyapp.databinding.ActivityLoginBinding
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            moveToRegister()
        }

    }

    private fun moveToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}