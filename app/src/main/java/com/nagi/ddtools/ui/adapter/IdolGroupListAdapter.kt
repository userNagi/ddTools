package com.nagi.ddtools.ui.adapter

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ListIdolGroupViewBinding
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.details.IdolGroupDetailsActivity
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.openUrl

class IdolGroupListAdapter(
    private var dataList: MutableList<IdolGroupList>
) : RecyclerView.Adapter<IdolGroupListAdapter.ViewHolder>() {
    var swipeToDeleteEnabled = false
    fun updateData(newData: List<IdolGroupList>) {
        val diffCallback = IdolGroupListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(private val binding: ListIdolGroupViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IdolGroupList) {
            binding.idolGroupImg.setImageUrl(item.imgUrl,false)
            binding.idolGroupName.text = item.name
            binding.idolGroupLocation.text = item.location
            binding.idolGroupLocation.text = item.location
            if (item.groupDesc.isEmpty()) {
                binding.idolGroupInfo.visibility = View.GONE
            } else {
                binding.idolGroupInfo.visibility = View.VISIBLE
                binding.idolGroupInfo.text = item.groupDesc
            }
            itemView.setOnClickListener {
                openPage(
                    binding.root.context as Activity,
                    IdolGroupDetailsActivity::class.java,
                    false,
                    Bundle().apply {
                        putInt("id", item.id)
                    })
            }
            if (item.ext.isNotEmpty()) {
                val mediaResult: MutableList<MediaList>
                val extAsJson = JsonParser.parseString(item.ext).asJsonObject
                if (extAsJson.has("media")) {
                    val mediaList = extAsJson.get("media").asJsonArray
                    val itemType = object : TypeToken<List<MediaList>>() {}.type
                    mediaResult = Gson().fromJson(mediaList, itemType)
                    for (media in mediaResult) {
                        if (media.type == "weibo") {
                            binding.jumpWeibo.visibility = View.VISIBLE
                            binding.jumpWeibo.setOnClickListener {
                                binding.root.context.openUrl(
                                    media.url
                                )
                            }
                        }
                        if (media.type == "bili") {
                            binding.jumpWeibo.visibility = View.VISIBLE
                            binding.jumpWeibo.setOnClickListener {
                                binding.root.context.openUrl(
                                    media.url
                                )
                            }
                        }
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