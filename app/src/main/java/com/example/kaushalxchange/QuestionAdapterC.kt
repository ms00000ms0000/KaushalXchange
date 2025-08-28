package com.example.kaushalxchange

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionAdapterC(
    private val questionList: List<Question>,
    private val userAnswers: IntArray,
    private val isSubmitted: () -> Boolean
) : RecyclerView.Adapter<QuestionAdapterC.QuestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.assessment_questions_c, parent, false)
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
                isChecked = userAnswers[position] == index

                if (isSubmitted()) {
                    isEnabled = false
                    when {
                        index == question.correctAnswerIndex -> setTextColor(Color.GREEN)
                        userAnswers[position] == index && index != question.correctAnswerIndex -> setTextColor(Color.RED)
                        else -> setTextColor(Color.BLACK)
                    }
                } else {
                    setTextColor(Color.BLACK)
                }
            }
            holder.optionsGroup.addView(radioButton)
        }

        holder.optionsGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!isSubmitted() && checkedId != -1) {
                userAnswers[position] = checkedId
            }
        }
    }

    override fun getItemCount(): Int = questionList.size

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionText)
        val optionsGroup: RadioGroup = view.findViewById(R.id.optionsGroup)
    }
}
