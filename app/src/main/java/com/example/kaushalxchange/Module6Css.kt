package com.example.kaushalxchange

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class Module6Css : Fragment() {

    private lateinit var notesText: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var feedbackText: TextView
    private lateinit var nextButton: Button
    private lateinit var submitButton: Button
    private lateinit var startAssignmentBtn: Button
    private lateinit var prefs: SharedPreferences

    private val questions = listOf(
        Question("Which Tailwind prefix applies utilities for medium screen sizes", listOf("md", "lg", "sm", "xl"), 0),
        Question("Which Tailwind feature allows building custom colors and spacing", listOf("inline CSS", "style tag", "tailwind config file", "media query"), 2),
        Question("Which Tailwind utility can be used to create animations", listOf("animate bounce", "animation run", "transition bounce", "animate run"), 0),
        )

    private var currentQuestionIndex = 0
    private lateinit var userAnswers: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences("kaushalxchange_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_module6_css, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notesText = view.findViewById(R.id.notesText)
        questionText = view.findViewById(R.id.questionText)
        optionsGroup = view.findViewById(R.id.optionsGroup)
        feedbackText = view.findViewById(R.id.feedbackText)
        nextButton = view.findViewById(R.id.nextButton)
        submitButton = view.findViewById(R.id.submitButton)
        startAssignmentBtn = view.findViewById(R.id.startAssignmentBtn)

        userAnswers = IntArray(questions.size) { -1 }

        // Show Start Assignment if module6 already completed or all modules completed
        val module6Done = prefs.getBoolean("module_6_completed", false)
        var allDone = true
        for (i in 1..6) {
            if (!prefs.getBoolean("module_${i}_completed", false)) { allDone = false; break }
        }
        if (module6Done || allDone) startAssignmentBtn.visibility = View.VISIBLE

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
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion()
                feedbackText.text = ""
            }
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
                if (i == correct) { rb.setBackgroundResource(R.drawable.mcq_correct_background); rb.setTextColor(Color.WHITE) }
                else if (i == selected) { rb.setBackgroundResource(R.drawable.mcq_wrong_background); rb.setTextColor(Color.WHITE) }
                else { rb.setBackgroundResource(R.drawable.mcq_background); rb.setTextColor(Color.BLACK) }
                rb.isEnabled = false
            }

            var score = 0
            for (i in questions.indices) if (userAnswers[i] == questions[i].correctAnswerIndex) score++
            Toast.makeText(requireContext(), "Module 6 score: $score / ${questions.size}", Toast.LENGTH_LONG).show()
            prefs.edit().putBoolean("module_6_completed", true).apply()

            // Show start assessment button after module 6 submission
            startAssignmentBtn.visibility = View.VISIBLE
        }

        startAssignmentBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentActivityCss::class.java))
        }
    }

    private fun showQuestion() {
        val q = questions[currentQuestionIndex]
        questionText.text = q.question
        optionsGroup.removeAllViews()
        for (opt in q.options) {
            val rb = RadioButton(requireContext())
            rb.text = opt; rb.setBackgroundResource(R.drawable.mcq_background); rb.setTextColor(Color.BLACK)
            rb.setPadding(12,16,12,16)
            optionsGroup.addView(rb)
        }
        optionsGroup.clearCheck()
        nextButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.GONE
        submitButton.visibility = if (currentQuestionIndex == questions.size - 1) View.VISIBLE else View.GONE
    }
}
