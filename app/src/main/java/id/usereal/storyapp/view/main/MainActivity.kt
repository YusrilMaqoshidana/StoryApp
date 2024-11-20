package id.usereal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.LoadState
import id.usereal.storyapp.R
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.view.LoadingStateAdapter
import id.usereal.storyapp.view.MainAdapter
import id.usereal.storyapp.view.ViewModelFactory
import id.usereal.storyapp.view.addStory.AddStoryActivity
import id.usereal.storyapp.view.login.LoginActivity
import id.usereal.storyapp.view.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity started")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.story)
        checkSession()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: Menu created")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: Selected item - ${item.title}")
        return when (item.itemId) {
            R.id.logout -> {
                Log.d(TAG, "onOptionsItemSelected: Logout clicked")
                viewModel.logout()
                true
            }
            R.id.maps -> {
                Log.d(TAG, "onOptionsItemSelected: Maps clicked")
                moveToMaps(userToken)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkSession() {
        Log.d(TAG, "checkSession: Checking user session")
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                Log.d(TAG, "checkSession: User not logged in, redirecting to LoginActivity")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                userToken = user.token
                Log.d(TAG, "checkSession: User logged in with token: $userToken")

                // Tampilkan ProgressBar sebelum data di-load
                binding.progressBar.isVisible = true

                observeStory()

                binding.fab.setOnClickListener {
                    Log.d(TAG, "checkSession: Floating Action Button clicked")
                    moveToAddStory(user.token)
                }
            }
        }
    }


    private fun observeStory() {
        Log.d(TAG, "observeStory: Observing story data")
        val adapter = MainAdapter(this, userToken)

        // Menghubungkan adapter dengan RecyclerView
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        // Observasi perubahan data Paging
        viewModel.stories.observe(this) { pagingData ->
            Log.d(TAG, "observeStory: Received new PagingData")
            adapter.submitData(lifecycle, pagingData)
        }

        // Observasi status pemuatan dari PagingData
        adapter.addLoadStateListener { loadState ->
            // Tampilkan ProgressBar saat halaman pertama sedang dimuat
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading

            // Tangani error saat halaman pertama gagal dimuat
            if (loadState.source.refresh is LoadState.Error) {
                val error = (loadState.source.refresh as LoadState.Error).error
                Log.e(TAG, "observeStory: Error loading data: ${error.localizedMessage}")
            }
        }
    }


    private fun moveToAddStory(token: String) {
        Log.d(TAG, "moveToAddStory: Navigating to AddStoryActivity with token: $token")
        val intent = Intent(this, AddStoryActivity::class.java).apply {
            putExtra(AddStoryActivity.EXTRA_TOKEN, token)
        }
        startActivity(intent)
        finish()
    }

    private fun moveToMaps(token: String) {
        Log.d(TAG, "moveToMaps: Navigating to MapsActivity with token: $token")
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra(MapsActivity.EXTRA_TOKEN, token)
        }
        startActivity(intent)
    }

    companion object {
        private const val TAG = "Tester"
    }
}
