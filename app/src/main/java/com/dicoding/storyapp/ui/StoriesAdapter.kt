package com.dicoding.storyapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.remote.response.ListStoryItem

class StoriesAdapter(private val listener: OnItemClickListener) : PagingDataAdapter<ListStoryItem, StoriesAdapter.StoryViewHolder>(ListStoryItemDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(item: ListStoryItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bind(it, listener)
        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: ListStoryItem, listener: OnItemClickListener) {
            nameTextView.text = story.name
            descriptionTextView.text = story.description
            story.photoUrl?.let {
                Glide.with(itemView.context)
                    .load(it)
                    .into(photoImageView)
            }

            itemView.setOnClickListener {
                listener.onItemClick(story)
            }
        }
    }

    private class ListStoryItemDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}



