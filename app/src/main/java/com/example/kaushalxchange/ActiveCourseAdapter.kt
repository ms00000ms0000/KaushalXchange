package com.example.kaushalxchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ActiveCourse(
    val studentName: String,
    val skills_want_to_learn: List<String>,
    val skills_can_teach: List<String>
)

class ActiveCourseAdapter(
    private var items: MutableList<ActiveCourse>,
    private val onQuitClick: (ActiveCourse) -> Unit,
    private val onStartClick: (ActiveCourse) -> Unit
) : RecyclerView.Adapter<ActiveCourseAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.course_person_name)
        val learnTv: TextView = view.findViewById(R.id.course_skill_learn)
        val offerTv: TextView = view.findViewById(R.id.course_skills_offer)
        val startBtn: Button = view.findViewById(R.id.btn_start)
        val quitBtn: Button = view.findViewById(R.id.btn_quit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_active_course, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.nameTv.text = item.studentName
        holder.learnTv.text = "Skills want to learn: ${item.skills_want_to_learn.joinToString(", ")}"
        holder.offerTv.text = "Skills can teach: ${if (item.skills_can_teach.isEmpty()) "—" else item.skills_can_teach.joinToString(", ")}"

        holder.startBtn.setOnClickListener { onStartClick(item) }
        holder.quitBtn.setOnClickListener { onQuitClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: MutableList<ActiveCourse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
