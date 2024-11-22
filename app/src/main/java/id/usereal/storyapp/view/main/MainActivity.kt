package id.usereal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import id.usereal.storyapp.R
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.view.LoadingStateAdapter
import id.usereal.storyapp.view.MainAdapter
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.addStory.AddStoryActivity
import id.usereal.storyapp.view.login.LoginActivity
import id.usereal.storyapp.view.maps.MapsActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeSession()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.story)
    }


    private fun observeSession() {

        viewModel.getSession().observe(this) { user ->
            when (user.isLogin && user.token.isNotEmpty()) {
                false -> {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                true -> {
                    setupAdapter(user.token)
                    binding.fab.setOnClickListener {
                        moveToAddStory()
                    }
                }
            }
        }
    }

    private fun setupAdapter(token: String) {
        adapter = MainAdapter(this, token)
        viewModel.stories.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.tvNoData.isVisible = loadState.source.refresh is LoadState.NotLoading &&
                    adapter.itemCount == 0
        }
    }

    private fun moveToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun moveToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }

            R.id.maps -> {
                moveToMaps()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}