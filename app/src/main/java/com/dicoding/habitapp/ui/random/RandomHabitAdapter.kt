package com.dicoding.habitapp.ui.random

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit

class RandomHabitAdapter(
    private val onClick: (Habit?) -> Unit
) : RecyclerView.Adapter<RandomHabitAdapter.PagerViewHolder>() {

    private val habitMap = LinkedHashMap<PageType, Habit>()

    fun submitData(key: PageType, habit: Habit?) {
        habit?.let {
            habitMap[key] = it
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagerViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.pager_item, parent, false))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val key = getIndexKey(position)
        key?.let { pageKey ->
            val pageData = habitMap[pageKey]
            pageData?.let { data ->
                holder.bind(pageKey, data)
            }
        }
    }

    override fun getItemCount() = habitMap.size

    private fun getIndexKey(position: Int): PageType? {
        val keys = habitMap.keys.toList()
        return if (position >= 0 && position < keys.size) {
            keys[position]
        } else {
            null
        }
    }

    enum class PageType {
        HIGH, MEDIUM, LOW
    }

    inner class PagerViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        //TODO 14 : Create view and bind data to item view
        private val habitTitle : TextView = itemView.findViewById(R.id.pager_tv_title)
        private val habitStartTime : TextView = itemView.findViewById(R.id.pager_tv_start_time)
        private val habitMinutes : TextView = itemView.findViewById(R.id.pager_tv_minutes)
        private val habitPriorityLevel : ImageView = itemView.findViewById(R.id.item_priority_level)
        private val btnOpenCountdown : Button = itemView.findViewById(R.id.btn_open_count_down)


        fun bind(pageType: PageType, pageData: Habit) {
            habitTitle.text = pageData.title
            habitStartTime.text = pageData.startTime
            habitMinutes.text = pageData.minutesFocus.toString()

            if (pageType == PageType.HIGH){
                habitPriorityLevel.setImageResource(R.drawable.ic_priority_high)
            } else if (pageType == PageType.MEDIUM) {
                habitPriorityLevel.setImageResource(R.drawable.ic_priority_medium)
            } else {
                habitPriorityLevel.setImageResource(R.drawable.ic_priority_low)
            }
            btnOpenCountdown.setOnClickListener{onClick(pageData)}
        }
    }
}
