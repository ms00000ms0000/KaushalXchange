package com.example.kaushalxchange


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MatchAdapter(
    private val data: List<UserProfile>,
    private val listener: OnRequestClickListener
) : RecyclerView.Adapter<MatchAdapter.VH>() {


    interface OnRequestClickListener { fun onRequestClick(targetProfile: UserProfile) }


    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.match_name)
        val skills: TextView = view.findViewById(R.id.match_skills)
        val requestBtn: Button = view.findViewById(R.id.request_btn)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return VH(v)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = data[position]
        holder.name.text = p.displayName ?: "Unknown"
        holder.skills.text = p.skills_can_teach.joinToString(", ")
        holder.requestBtn.setOnClickListener { listener.onRequestClick(p) }
    }


    override fun getItemCount(): Int = data.size
}