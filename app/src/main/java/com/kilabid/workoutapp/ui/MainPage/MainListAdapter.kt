package com.kilabid.workoutapp.ui.MainPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.data.MainMenuData

class MainListAdapter(
    private val listMenu: ArrayList<MainMenuData>,
    private val onClick: (MainMenuData) -> Unit,
) : RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.iv_menu)
        val tvName: TextView = itemView.findViewById(R.id.tv_menu)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        fun bind(menu: MainMenuData) {
            itemView.setOnClickListener {
                onClick(menu)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.main_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainListAdapter.ViewHolder, position: Int) {
        holder.tvName.text = listMenu[position].name
        holder.imgPhoto.setImageResource(listMenu[position].icon)
        holder.tvDesc.text = listMenu[position].desc
        val menu = listMenu[position]
        holder.bind(menu)
    }

    override fun getItemCount(): Int {
        return listMenu.size
    }

}