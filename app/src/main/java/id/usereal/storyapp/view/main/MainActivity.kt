package id.usereal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import id.derysudrajat.easyadapter.EasyAdapter
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.databinding.ItemCardStoryBinding
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.addStrory.AddStoryActivity
import id.usereal.storyapp.view.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.story)

        checkSession()

        binding.fab.setOnClickListener {
            moveToAddStory()
        }
    }

    private fun checkSession() {
        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                observeStory(user.token)
            }
        }
    }

    private fun observeStory(token: String) {
        mainViewModel.getAllStory(token).observe(this) { story ->
            when (story) {
                is UiState.Loading -> {
                    Log.d("MainActivity", "Loading...")
                }
                is UiState.Success -> {
                    Log.d("MainActivity", "Success: ${story.data}")
                    val data = story.data
                    binding.rvStory.apply {
                        setHasFixedSize(true)
                        itemAnimator = DefaultItemAnimator()
                        adapter = EasyAdapter(data, ItemCardStoryBinding::inflate) { binding, item ->
                            binding.tvItemName.text = item.name
                            binding.descriptionTextView.text = item.description
                        }
                    }
                }
                is UiState.Error -> {
                    Log.d("MainActivity", "Error: ${story.error}")
                }
            }
        }
    }

    private fun moveToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }
}

