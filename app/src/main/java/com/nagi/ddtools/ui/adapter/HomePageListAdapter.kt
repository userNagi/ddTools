package com.nagi.ddtools.ui.adapter

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.database.homePagList.HomePageList
import com.nagi.ddtools.databinding.ListHomePageViewBinding
import com.nagi.ddtools.ui.homepage.details.HomeListDetailsActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.UiUtils
import kotlinx.coroutines.CoroutineScope

class HomePageListAdapter(
    private var dataList: MutableList<HomePageList>,
    private val scope: CoroutineScope,
    private val canClick: Boolean? = false,
    private val onLongClick: (Int, HomePageList) -> Unit
) : RecyclerView.Adapter<HomePageListAdapter.ViewHolder>() {

    fun updateData(newData: List<HomePageList>) {
        val diffCallback = HomePageListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(private val binding: ListHomePageViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: HomePageList,
            onLongClick: (Int, HomePageList) -> Unit,
            scope: CoroutineScope,
            canClick: Boolean? = false
        ) {
            binding.homePageListviewText.text = item.name
            (FileUtils.loadBitmapFromPath(scope, item.imgUrl) { bitmap ->
                bitmap?.let {
                    binding.homePageListviewImage.setImageBitmap(it)
                }
            })
            itemView.setOnClickListener {
                if (canClick == true) UiUtils.openPage(
                    binding.root.context as Activity,
                    HomeListDetailsActivity::class.java,
                    false,
                    Bundle().apply {
                        putInt("id", item.id)
                    }
                )
            }
            itemView.setOnLongClickListener {
                onLongClick(adapterPosition, item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListHomePageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], onLongClick, scope, canClick)
    }

    override fun getItemCount(): Int = dataList.size

    class HomePageListDiffCallback(
        private val oldList: List<HomePageList>,
        private val newList: List<HomePageList>
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

