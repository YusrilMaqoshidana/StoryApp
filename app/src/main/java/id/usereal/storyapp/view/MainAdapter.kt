package id.usereal.storyapp.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.databinding.ItemCardStoryBinding
import id.usereal.storyapp.view.detail.DetailActivity

class MainAdapter(
    private val context: Context,
) : PagingDataAdapter<ListStoryItem, MainAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: ItemCardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                tvItemName.text = story.name
                tvItemDescription.text = story.description

                Glide.with(context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivItemPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra(EXTRA_STORY_ID, story.id)
                        Log.d("TokenTesting", "Data story id: ${story.id}")
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemCardStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "story_id"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
