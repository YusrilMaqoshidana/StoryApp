package id.usereal.storyapp.view.detail

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.usereal.storyapp.R
import id.usereal.storyapp.data.UiState
import id.usereal.storyapp.data.model.Story
import id.usereal.storyapp.databinding.ActivityDetailBinding
import id.usereal.storyapp.utils.formatDate

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId.isNullOrEmpty()) {
            showErrorAndFinish()
            return
        }

        setupToolbar()
        initializeViewModel()
        observeStoryDetail(storyId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.add_story)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
    }

    private fun observeStoryDetail(storyId: String) {
        viewModel.detailStory(storyId).observe(this) { state ->
            when (state) {
                is UiState.Loading -> toggleLoading(true)
                is UiState.Success -> displayStoryDetails(state.data!!)
                is UiState.Error -> showSnackbar(state.error)
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        with(binding) {
            val visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            tvDetailName.visibility = visibility
            tvDetailDescription.visibility = visibility
            ivDetailPhoto.visibility = visibility
            dateTextView.visibility = visibility
        }
    }

    private fun displayStoryDetails(story: Story) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            dateTextView.text = formatDate(story.createdAt ?: "null")
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
        }
        toggleLoading(false)
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

    private fun showErrorAndFinish() {
        Toast.makeText(this, getString(R.string.story_id_not_found), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_STORY_ID = "story_id"
    }
}
