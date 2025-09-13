package com.example.kaushalxchange

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AssessmentActivityCss : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterCss
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_css)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterCss(questions, userAnswers) { isSubmitted }
        recyclerView.adapter = questionAdapter

        startTimer()

        submitBtn.setOnClickListener {
            if (!isSubmitted) {
                isSubmitted = true
                evaluateAnswers()
                questionAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(30 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("Time Left: %02d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                if (!isSubmitted) {
                    isSubmitted = true
                    evaluateAnswers()
                    questionAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }

    private fun evaluateAnswers() {
        countDownTimer.cancel()
        var score = 0
        for (i in questions.indices) {
            if (userAnswers[i] == questions[i].correctAnswerIndex) score++
        }

        val editor = sharedPreferences.edit()
        val skillName = "CSS"

        if (score >= 1) addSkillToList("MySkills", skillName)
        if (score >= 2) addSkillToList("SkillsICanTeach", skillName)
        editor.apply()

        val result = if (score >= 1) "Passed" else "Failed"
        AlertDialog.Builder(this)
            .setTitle("Assessment Complete")
            .setMessage("Score: $score / 25\nResult: $result")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }

    private fun addSkillToList(key: String, skill: String) {
        val skillsSet = sharedPreferences.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        skillsSet.add(skill)
        sharedPreferences.edit().putStringSet(key, skillsSet).apply()
    }

    private fun loadQuestions(): List<Question> {
        return listOf(
            Question("What does CSS stand for?", listOf("Creative Style Sheets", "Cascading Style Sheets", "Computer Style Sheets", "Colorful Style Sheets"), 1),
            Question("Which property is used to change text color?", listOf("text-color", "font-color", "color", "background-color"), 2),
            Question("Which property controls text size?", listOf("font-size", "text-style", "text-size", "font-style"), 0),
            Question("How do you select an element with id 'header'?", listOf("#header", ".header", "header", "*header"), 0),
            Question("How do you select elements with class 'menu'?", listOf("#menu", ".menu", "menu", "*menu"), 1),
            Question("Which CSS property controls background color?", listOf("bgcolor", "color", "background-color", "background-style"), 2),
            Question("Which CSS property controls bold text?", listOf("font-weight", "text-bold", "bold", "font-style"), 0),
            Question("Which value of position makes element relative to viewport?", listOf("absolute", "fixed", "relative", "sticky"), 1),
            Question("Which CSS property adds shadow to text?", listOf("box-shadow", "text-shadow", "font-shadow", "shadow"), 1),
            Question("Which unit is relative to the root element font size?", listOf("px", "em", "rem", "%"), 2),
            Question("Which display value hides an element but keeps its space?", listOf("hidden", "display:none", "visibility:hidden", "opacity:0"), 2),
            Question("Which CSS property controls space inside an element?", listOf("margin", "padding", "border", "spacing"), 1),
            Question("Which property makes text italic?", listOf("text-style", "font-italic", "font-style", "style"), 2),
            Question("Which property underlines text?", listOf("underline", "text-decoration", "decoration", "text-style"), 1),
            Question("Which CSS layout uses flexible boxes?", listOf("grid", "block", "flexbox", "table"), 2),
            Question("Which property controls the order of flex items?", listOf("z-index", "order", "flex-order", "align"), 1),
            Question("Which CSS layout divides into rows and columns?", listOf("grid", "flex", "table", "block"), 0),
            Question("Which property merges grid columns?", listOf("grid-span", "grid-column", "col-span", "span-column"), 1),
            Question("Which CSS property controls transparency?", listOf("opacity", "alpha", "visibility", "transparent"), 0),
            Question("Which property is used to make rounded corners?", listOf("corner", "border", "border-radius", "radius"), 2),
            Question("Which pseudo-class applies when hovering?", listOf(":focus", ":hover", ":active", ":visited"), 1),
            Question("Which pseudo-class applies when link is visited?", listOf(":hover", ":focus", ":visited", ":active"), 2),
            Question("Which property sets animation duration?", listOf("animation-time", "duration", "animation-duration", "animate"), 2),
            Question("Which property is used for transition speed?", listOf("transition-time", "duration", "transition-duration", "animate"), 2),
            Question("Which property makes an element float left or right?", listOf("float", "align", "position", "display"), 0)
        )
    }
}
