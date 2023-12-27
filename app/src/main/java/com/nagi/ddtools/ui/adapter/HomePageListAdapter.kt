package com.nagi.ddtools.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.HomePageListviewViewBinding

class HomePageListAdapter(private val dataList: List<HomePageList>) :
    RecyclerView.Adapter<HomePageListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: HomePageListviewViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomePageList) {
            // 必须要有一个加载本地图片的函数，例如使用Glide库
//            Glide.with(binding.homePageListviewImage.context)
//                .load(item.imgUrl)
//                .into(binding.homePageListviewImage)

            // 设置文本
            binding.homePageListviewText.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomePageListviewViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "点击了第${position}个", Toast.LENGTH_SHORT)
                .show()
        }
        holder.itemView.setOnLongClickListener {
            {}
            true
        }
    }


    override fun getItemCount(): Int = dataList.size
}
