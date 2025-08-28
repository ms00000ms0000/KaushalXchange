package com.example.kaushalxchange

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionAdapterJS(
    private val questionList: List<Question>,
    private val userAnswers: IntArray
) : RecyclerView.Adapter<QuestionAdapterJS.QuestionViewHolder>() {

    var showCorrectAnswers: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.assessment_questions_js, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionList[position]
        holder.questionText.text = question.question
        holder.optionsGroup.removeAllViews()

        question.options.forEachIndexed { index, optionText ->
            val radioButton = RadioButton(holder.itemView.context).apply {
                text = optionText
                id = index
                setTextColor(Color.BLACK)
                isChecked = userAnswers[position] == index

                if (showCorrectAnswers) {
                    isEnabled = false
                    if (index == question.correctAnswerIndex) {
                        setTextColor(Color.GREEN)
                    } else if (userAnswers[position] == index) {
                        setTextColor(Color.RED)
                    }
                }
            }
            holder.optionsGroup.addView(radioButton)
        }

        if (!showCorrectAnswers) {
            holder.optionsGroup.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    userAnswers[position] = checkedId
                }
            }
        }
    }

    override fun getItemCount(): Int = questionList.size

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionText)
        val optionsGroup: RadioGroup = view.findViewById(R.id.optionsGroup)
    }
}
