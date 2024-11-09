package id.usereal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.view.MainAdapter
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.addStory.AddStoryActivity
import id.usereal.storyapp.view.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mainAdapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.story)
        checkSession()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun checkSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                observeStory(user.token)
                binding.fab.setOnClickListener {
                    moveToAddStory(user.token)
                }
            }
        }
    }

    private fun observeStory(token: String) {
        viewModel.getAllStory(token).observe(this) { story ->
            when (story) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    val data = story.data
                    if (data.isEmpty()){
                        binding.textView.visibility = android.view.View.VISIBLE
                    } else {
                        mainAdapter = MainAdapter(this, token)
                        mainAdapter.submitList(data)
                        binding.rvStory.adapter = mainAdapter
                        showSnackbar(getString(R.string.stories_fetched_successfully))
                        binding.textView.visibility = android.view.View.GONE
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    showSnackbar(getString(R.string.error_fetching_stories, story.error))
                }
            }
        }
    }

    private fun moveToAddStory(token: String) {
        val intent = Intent(this, AddStoryActivity::class.java).apply {
            putExtra(AddStoryActivity.EXTRA_TOKEN, token)
        }
        startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
