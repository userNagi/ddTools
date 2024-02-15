package com.nagi.ddtools.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.databinding.ListIdolMediaBinding
import com.nagi.ddtools.utils.FileUtils.getDrawableForMedia
import com.nagi.ddtools.utils.UiUtils.openUrl

class IdolMediaListAdapter(private val data: MutableList<MediaList>) :
    RecyclerView.Adapter<IdolMediaListAdapter.IdolMediaViewHolder>() {

    fun updateDate(newDate: List<MediaList>) {
        val diffCallback = IdolMediaListDiffCallback(data, newDate)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(newDate)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdolMediaViewHolder {
        val binding =
            ListIdolMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IdolMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IdolMediaViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class IdolMediaViewHolder(private val binding: ListIdolMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MediaList) {
            binding.mediaIcon.setImageDrawable(getDrawableForMedia(binding.root.context, data.type))
            binding.mediaName.text = data.name
            itemView.setOnClickListener { binding.root.context.openUrl(data.url) }
        }
    }

    class IdolMediaListDiffCallback(
        private val oldList: List<MediaList>,
        private val newList: List<MediaList>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}