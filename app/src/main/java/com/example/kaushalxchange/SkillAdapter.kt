package com.example.kaushalxchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Skill(val name: String, val imageRes: Int)

class SkillAdapter(private val skills: List<Skill>) :
    RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillIcon: ImageView = itemView.findViewById(R.id.skillIcon)
        val skillName: TextView = itemView.findViewById(R.id.skillNameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skills[position]
        holder.skillName.text = skill.name
        holder.skillIcon.setImageResource(skill.imageRes)
    }

    override fun getItemCount(): Int = skills.size
}
