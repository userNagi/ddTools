package com.nagi.ddtools.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.databinding.ListActivityViewBinding
import com.nagi.ddtools.utils.UiUtils.openUrl

class ActivityListAdapter(private val dataList: MutableList<ActivityList>) :
    RecyclerView.Adapter<ActivityListAdapter.ViewHolder>() {

    fun updateData(newData: List<ActivityList>) {
        val diffCallback = ActivityListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListActivityViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    class ViewHolder(private val binding: ListActivityViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ActivityList) {
            binding.activityLocation.text = data.location
            binding.activityLocationDesc.text = data.location_desc
            binding.activityName.text = data.name
            binding.activityTime.text = data.duration_time
            binding.activityMoney.text = data.price
            binding.activityBuy.setOnClickListener {
                binding.root.context.openUrl(data.buy_url)
            }
            binding.activityWeibo.setOnClickListener {
                binding.root.context.openUrl(data.weibo_url)
            }
            itemView.setOnClickListener {

            }
        }
    }
    class ActivityListDiffCallback(
        private val oldList: List<ActivityList>,
        private val newList: List<ActivityList>
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