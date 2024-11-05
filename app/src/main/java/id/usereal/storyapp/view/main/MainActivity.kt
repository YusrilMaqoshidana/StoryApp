package id.usereal.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.usereal.storyapp.R
import id.usereal.storyapp.databinding.ActivityMainBinding
import id.usereal.storyapp.view.addStrory.AddStoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.strory)

        binding.fab.setOnClickListener {
            moveToAddStory()
        }
    }

    fun moveToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }


}
