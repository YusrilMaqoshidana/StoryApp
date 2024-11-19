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
        val token = intent.getStringExtra(EXTRA_TOKEN)
        if (storyId.isNullOrEmpty()) {
            showErrorAndFinish()
            return
        }
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        token?.let { viewModel.getStoryById(it, storyId) }
        setupToolbar()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun setupObservers() {
        viewModel.story.observe(this) { story ->
            with(binding){
                tvDetailName.text = story?.name
                tvDetailDescription.text = story?.description
                Glide.with(this@DetailActivity)
                    .load(story?.photoUrl)
                    .into(ivDetailPhoto)
                dateTextView.text = formatDate(story?.createdAt ?: "null")
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            with(binding){
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                tvDetailName.visibility = if (isLoading) View.GONE else View.VISIBLE
                tvDetailDescription.visibility = if (isLoading) View.GONE else View.VISIBLE
                ivDetailPhoto.visibility = if (isLoading) View.GONE else View.VISIBLE
                dateTextView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
        viewModel.error.observe(this) { error ->
            showSnackbar(error ?: getString(R.string.unknown_error))
        }
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
        const val EXTRA_TOKEN = "token"
    }
}