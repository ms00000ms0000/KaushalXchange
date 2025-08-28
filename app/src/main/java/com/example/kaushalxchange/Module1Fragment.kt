package com.example.kaushalxchange

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class Module1Fragment : Fragment() {

    private lateinit var notesText: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button
    private lateinit var prefs: SharedPreferences

    private val questions = listOf(
        Question("What is the output of print(2 ** 3)?", listOf("5", "6", "8", "9"), 2),
        Question("Which type holds text in Python?", listOf("int", "float", "str", "bool"), 2)
    )

    private var currentIndex = 0
    private lateinit var userAnswers: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences("kaushalxchange_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_module1_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notesText = view.findViewById(R.id.notesText)
        questionText = view.findViewById(R.id.questionText)
        optionsGroup = view.findViewById(R.id.optionsGroup)
        nextButton = view.findViewById(R.id.nextButton)
        submitButton = view.findViewById(R.id.submitButton)

        userAnswers = IntArray(questions.size) { -1 }

        showQuestion()

        optionsGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) return@setOnCheckedChangeListener
            val idx = group.indexOfChild(group.findViewById(checkedId))
            userAnswers[currentIndex] = idx
        }

        nextButton.setOnClickListener {
            if (userAnswers[currentIndex] == -1) {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion()
            }
        }

        submitButton.setOnClickListener {
            if (userAnswers[currentIndex] == -1) {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // mark chosen/wrong visually
            val selected = userAnswers[currentIndex]
            val correct = questions[currentIndex].correctAnswerIndex
            for (i in 0 until optionsGroup.childCount) {
                val rb = optionsGroup.getChildAt(i) as RadioButton
                if (i == correct) {
                    rb.setBackgroundResource(R.drawable.mcq_correct_background)
                    rb.setTextColor(Color.WHITE)
                } else if (i == selected) {
                    rb.setBackgroundResource(R.drawable.mcq_wrong_background)
                    rb.setTextColor(Color.WHITE)
                } else {
                    rb.setBackgroundResource(R.drawable.mcq_background)
                    rb.setTextColor(Color.BLACK)
                }
                rb.isEnabled = false
            }

            // score for module
            var score = 0
            for (i in questions.indices) {
                if (userAnswers[i] == questions[i].correctAnswerIndex) score++
            }
            Toast.makeText(requireContext(), "Module 1 score: $score / ${questions.size}", Toast.LENGTH_LONG).show()

            prefs.edit().putBoolean("module_1_completed", true).apply()
        }
    }

    private fun showQuestion() {
        val q = questions[currentIndex]
        questionText.text = q.question
        optionsGroup.removeAllViews()
        for (opt in q.options) {
            val rb = RadioButton(requireContext())
            rb.text = opt
            rb.setBackgroundResource(R.drawable.mcq_background)
            rb.setTextColor(Color.BLACK)
            rb.setPadding(12, 16, 12, 16)
            optionsGroup.addView(rb)
        }
        optionsGroup.clearCheck()

        nextButton.visibility = if (currentIndex < questions.size - 1) View.VISIBLE else View.GONE
        submitButton.visibility = if (currentIndex == questions.size - 1) View.VISIBLE else View.GONE
    }
}
