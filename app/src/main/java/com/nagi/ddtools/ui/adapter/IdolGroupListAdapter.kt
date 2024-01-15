package com.nagi.ddtools.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonParser
import com.nagi.ddtools.R
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ListIdolGroupViewBinding
import com.nagi.ddtools.utils.UiUtils.openUrl

class IdolGroupListAdapter(
    private var dataList: MutableList<IdolGroupList>
) : RecyclerView.Adapter<IdolGroupListAdapter.ViewHolder>() {

    fun updateData(newData: List<IdolGroupList>) {
        val diffCallback = IdolGroupListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val binding: ListIdolGroupViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IdolGroupList) {
            binding.idolGroupImg.setImageResource(R.color.lty)
            binding.idolGroupName.text = item.name
            binding.idolGroupLocation.text = item.location
            if (item.group_desc.isEmpty()) {
                binding.idolGroupInfo.visibility = View.GONE
            } else {
                binding.idolGroupInfo.visibility = View.VISIBLE
                binding.idolGroupInfo.text = item.group_desc
            }
            itemView.setOnClickListener {
                binding.idolGroupInfo.text = item.group_desc
            }
            if (item.ext.isNotEmpty()) {
                val extAsJson = JsonParser.parseString(item.ext).asJsonObject
                if (extAsJson.has("weibo")) {
                    if (extAsJson["weibo"].asString.isNotEmpty()){
                        binding.jumpWeibo.visibility = View.VISIBLE
                        binding.jumpWeibo.setOnClickListener { openUrl(extAsJson["weibo"].asString, binding.root.context) }
                    }
                }
                if (extAsJson.has("bili")) {
                    if (extAsJson["bili"].asString.isNotEmpty()){
                        binding.jumpBili.visibility = View.VISIBLE
                        binding.jumpBili.setOnClickListener { openUrl(extAsJson["bili"].asString, binding.root.context) }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListIdolGroupViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
    class IdolGroupListDiffCallback(
        private val oldList: List<IdolGroupList>,
        private val newList: List<IdolGroupList>
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