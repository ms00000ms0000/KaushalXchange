package com.example.kaushalxchange

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Module2Java : Fragment() {

    private lateinit var notesText: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var feedbackText: TextView
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button
    private lateinit var prefs: SharedPreferences

    private val questions = listOf(
        Question("Which keyword is used to create an object in Java?",
            listOf("class", "object", "new", "this"), 2),

        Question("What is a class in Java?",
            listOf("An instance of an object", "A blueprint for objects", "A variable type", "A method library"), 1)


    )

    private var currentQuestionIndex = 0
    private lateinit var userAnswers: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences("kaushalxchange_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_module2_java, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notesText = view.findViewById(R.id.notesText)
        questionText = view.findViewById(R.id.questionText)
        optionsGroup = view.findViewById(R.id.optionsGroup)
        feedbackText = view.findViewById(R.id.feedbackText)
        nextButton = view.findViewById(R.id.nextButton)
        submitButton = view.findViewById(R.id.submitButton)

        userAnswers = IntArray(questions.size) { -1 }

        showQuestion()

        optionsGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) return@setOnCheckedChangeListener
            val idx = group.indexOfChild(group.findViewById(checkedId))
            userAnswers[currentQuestionIndex] = idx
        }

        nextButton.setOnClickListener {
            if (userAnswers[currentQuestionIndex] == -1) {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentQuestionIndex++
            showQuestion()
            feedbackText.text = ""
        }

        submitButton.setOnClickListener {
            if (userAnswers[currentQuestionIndex] == -1) {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selected = userAnswers[currentQuestionIndex]
            val correct = questions[currentQuestionIndex].correctAnswerIndex

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
                    rb.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                rb.isEnabled = false
            }

            feedbackText.text = if (selected == correct) "Correct!" else "Wrong!"
            feedbackText.setTextColor(if (selected == correct) Color.GREEN else Color.RED)

            // mark module completed if last question answered
            var score = 0
            for (i in questions.indices) {
                if (userAnswers[i] == questions[i].correctAnswerIndex) score++
            }
            Toast.makeText(requireContext(), "Module 2 score: $score / ${questions.size}", Toast.LENGTH_LONG).show()
            prefs.edit().putBoolean("module_2_completed", true).apply()
        }
    }

    private fun showQuestion() {
        val q = questions[currentQuestionIndex]
        questionText.text = q.question
        optionsGroup.removeAllViews()
        for (opt in q.options) {
            val rb = RadioButton(requireContext())
            rb.text = opt
            rb.setBackgroundResource(R.drawable.mcq_background)
            rb.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            rb.setPadding(12, 16, 12, 16)
            optionsGroup.addView(rb)
        }
        optionsGroup.clearCheck()

        nextButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.GONE
        submitButton.visibility = if (currentQuestionIndex == questions.size - 1) View.VISIBLE else View.GONE
    }
}
