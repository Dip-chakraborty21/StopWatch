package com.momoui.stopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LapAdapter(private val lapList: List<String>) :
    RecyclerView.Adapter<LapAdapter.LapViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return LapViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        holder.bind(lapList[position])
    }

    override fun getItemCount(): Int {
        return lapList.size
    }

    class LapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(lap: String) {
            textView.text = lap
        }
    }
}
