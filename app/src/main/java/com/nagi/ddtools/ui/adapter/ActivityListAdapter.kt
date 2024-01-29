package com.nagi.ddtools.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.databinding.ListActivityViewBinding
import com.nagi.ddtools.ui.toolpage.tools.activitysearch.details.ActivityDetailsActivity
import com.nagi.ddtools.utils.DataUtils
import com.nagi.ddtools.utils.MapUtils
import com.nagi.ddtools.utils.UiUtils.openPage
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
            val date = DataUtils.parseDate(data.durationDate)
            binding.apply {
                @SuppressLint("SetTextI18n")
                activityTimeDesc.text = "${date[1]}月${date[0]}日"
                activityLocation.text = data.location
                activityName.text = data.name
                activityTime.text = data.durationTime
                activityMoney.text = data.price
                activityBuy.setOnClickListener {
                    root.context.openUrl(data.buyUrl)
                }
                activityWeibo.setOnClickListener {
                    root.context.openUrl(data.weiboUrl)
                }
                activityLocationDesc.apply {
                    text = data.locationDesc
                    setOnClickListener { MapUtils.chooseLocation(context, data.locationDesc) }
                }
            }
            itemView.setOnClickListener {
                openPage(binding.root.context as Activity,
                    ActivityDetailsActivity::class.java,
                    false,
                    Bundle().apply { putInt("id",data.id) }
                )
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