package com.nagi.ddtools.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.databinding.ListEvaluateTagBinding


class TagListAdapter(private val tags: List<TagsList>) :
    RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding =
            ListEvaluateTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val likes = tags[position].likes
        val tagContent = tags[position].content
        holder.bind(tagContent, likes)
    }

    override fun getItemCount(): Int = tags.size

    class TagViewHolder(private val binding: ListEvaluateTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tagContent: String, likes: Int) {
            binding.textViewTagContent.text = tagContent
            binding.textViewTagLikes.text = formatLikes(likes)
        }

        private fun formatLikes(likes: Int): String {
            return when {
                likes <= 99 -> likes.toString()
                else -> "99+"
            }
        }
    }
}
