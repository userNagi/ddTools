package com.nagi.ddtools.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.databinding.ListActivityInIdolDetailsBinding
import com.nagi.ddtools.databinding.ListActivityViewBinding
import com.nagi.ddtools.ui.toolpage.tools.activitysearch.details.ActivityDetailsActivity
import com.nagi.ddtools.utils.DataUtils
import com.nagi.ddtools.utils.MapUtils
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.openUrl

class ActivityListAdapter(
    private val dataList: MutableList<ActivityList>,
    private var detailsId: Int = 0
) :
    RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder>() {
    var isShowTime = true

    fun updateData(newData: List<ActivityList>, id: Int = 0) {
        detailsId = id
        val diffCallback = ActivityListDiffCallback(dataList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataList.clear()
        dataList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding =
            if (detailsId == 0) ListActivityViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            else ListActivityInIdolDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ActivityViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        if (dataList.size == 0) return
        holder.bind(dataList[position], detailsId, isShowTime)
    }

    class ActivityViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ActivityList, groupId: Int, isShowTime: Boolean = true) {
            val date = DataUtils.parseDate(data.durationDate)
            if (binding is ListActivityViewBinding) {
                binding.apply {
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
            }
            if (binding is ListActivityInIdolDetailsBinding) {
                binding.apply {
                    activityTimeDesc.text = "${date[1]}月${date[0]}日"
                    activityName.text = data.name
                    if (isShowTime) activityTimeTable.text =
                        data.participatingGroup?.let { getTimeTable(it, groupId) }
                    activityLocationDesc.apply {
                        text = data.locationDesc
                        setOnClickListener { MapUtils.chooseLocation(context, data.locationDesc) }
                    }
                }
            }
            itemView.setOnClickListener {
                openPage(binding.root.context as Activity,
                    ActivityDetailsActivity::class.java,
                    false,
                    Bundle().apply { putInt("id", data.id) }
                )
            }
        }

        private fun getTimeTable(data: String, groupId: Int): String {
            val groupTimeData = DataUtils.getGroupTime(getThisPart(data, groupId))
            if (groupTimeData.size == 4) {
                groupTimeData.removeAt(0)
                val timeRange = "${DataUtils.trimSecondsFromTime(groupTimeData[0])}-${
                    DataUtils.trimSecondsFromTime(groupTimeData[1])
                }"
                val parts = groupTimeData[2].split(":")
                val partsMinutes = parts[0].toInt() * 60 + parts[1].toInt()
                val durationString = "(${partsMinutes}min)"
                return "$timeRange$durationString"
            }
            return ""
        }

        private fun getThisPart(data: String, groupId: Int): String =
            data.split(",")
                .find { it.contains(groupId.toString()) }
                .orEmpty()
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