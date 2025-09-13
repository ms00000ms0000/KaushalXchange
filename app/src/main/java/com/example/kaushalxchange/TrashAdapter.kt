package com.example.kaushalxchange

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class TrashAdapter(
    private val context: Context,
    private val items: List<ActiveCourse>,
    private val onRestoreClick: (ActiveCourse) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_trash_course, parent, false)
        val item = items[position]

        val nameTv = view.findViewById<TextView>(R.id.trash_course_name)
        val learnTv = view.findViewById<TextView>(R.id.trash_course_learn)
        val offerTv = view.findViewById<TextView>(R.id.trash_course_offer)
        val restoreBtn = view.findViewById<Button>(R.id.btn_restore)

        nameTv.text = item.studentName
        learnTv.text = "Wants to Learn: ${item.skills_want_to_learn.joinToString(", ")}"
        offerTv.text = "Can Teach: ${if (item.skills_can_teach.isEmpty()) "—" else item.skills_can_teach.joinToString(", ")}"

        restoreBtn.setOnClickListener { onRestoreClick(item) }

        return view
    }
}
