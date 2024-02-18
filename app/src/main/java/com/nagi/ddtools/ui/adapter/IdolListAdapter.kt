package com.nagi.ddtools.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.databinding.ListIdolViewBinding
import com.nagi.ddtools.utils.UiUtils.dpToPx

class IdolListAdapter(
    private val dataList: MutableList<IdolList>,
    private val onClickListener: ((Int, IdolList) -> Unit)? = null
) : RecyclerView.Adapter<IdolListAdapter.ViewHolder>() {
    fun updateData(newData: List<IdolList>) {
        val diffCallback = IdolListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListIdolViewBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], onClickListener)
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolder(
        private val binding: ListIdolViewBinding,
        private val onClickListener: ((Int, IdolList) -> Unit)? = null
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: IdolList,
            onClickListener: ((Int, IdolList) -> Unit)? = null,
        ) {
            binding.idolName.text = item.name
            binding.idolGroup.text = item.groupName
            binding.idolImg.setImageUrl(item.imageUrl)
            setTag(binding, item.tag)
            itemView.setOnClickListener {
                if (onClickListener != null) onClickListener(adapterPosition, item)
                else {
                    // TODO:
                }
            }
        }

        private fun setTag(binding: ListIdolViewBinding, tag: IdolTag?) {
            tag?.let {
                val background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = binding.root.context.dpToPx(4f)
                    color = ColorStateList.valueOf(Color.parseColor(tag.backColor))
                }
                binding.idolTag.text = tag.text
                binding.idolTag.background = background
                binding.idolTag.setTextColor(Color.parseColor(tag.textColor))
            }
        }
    }

    class IdolListDiffCallback(
        private val oldList: List<IdolList>,
        private val newList: List<IdolList>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}