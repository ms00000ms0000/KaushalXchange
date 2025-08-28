package com.example.kaushalxchange

import android.content.DialogInterface
import android.content.SharedPreferences // <-- added
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
    private var isSubmitted = false   // <-- added flag
    private lateinit var sharedPreferences: SharedPreferences // <-- added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_css)

        // <-- added SharedPreferences
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
                questionAdapter.notifyDataSetChanged() // refresh UI with colors
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
            if (userAnswers[i] == questions[i].correctAnswerIndex) {
                score++
            }
        }



        // <-- New logic for My Skills and Skill I Can Teach
        val editor = sharedPreferences.edit()
        val skillName = "CSS" // later you can make dynamic per skill

        if (score >= 15) {
            addSkillToList("MySkills", skillName)
        }
        if (score >= 20) {
            addSkillToList("SkillsICanTeach", skillName)
        }
        editor.apply()

        val result = if (score >= 15) "Passed" else "Failed"
        AlertDialog.Builder(this)
            .setTitle("Assessment Complete")
            .setMessage("Score: $score / 25\nResult: $result")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }

    // <-- helper function to add skills in SharedPreferences list
    private fun addSkillToList(key: String, skill: String) {
        val skillsSet = sharedPreferences.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        skillsSet.add(skill)
        sharedPreferences.edit().putStringSet(key, skillsSet).apply()
    }

    private fun loadQuestions(): List<Question> {
        return listOf(
            Question("Which CSS selector targets all elements of a specific type like p or h1", listOf("id selector", "class selector", "element selector", "universal selector"), 2),
            Question("Which CSS property controls the space between the content and the border", listOf("margin", "padding", "outline", "spacing"), 1),
            Question("Which property sets the width of an element in CSS", listOf("width", "size", "length", "layout"), 0),
            Question("Which CSS property is used to set the background color of an element", listOf("bgcolor", "background color", "color", "background"), 1),
            Question("Which CSS position property places an element relative to its first positioned ancestor", listOf("static", "relative", "absolute", "fixed"), 2),
            Question("Which CSS display property is used to arrange elements in block and inline styles", listOf("flex", "grid", "inline block", "display"), 3),
            Question("Which property is used to change the font style in CSS", listOf("font family", "text style", "font style", "typography"), 2),
            Question("Which unit in CSS is relative to the root element font size", listOf("px", "em", "rem", "vh"), 2),
            Question("Which CSS property adds shadow around an element box", listOf("box shadow", "text shadow", "border shadow", "outline shadow"), 0),
            Question("Which CSS property is used to transform elements like rotate and scale", listOf("translate", "transform", "transition", "animation"), 1),
            Question("Which Tailwind CSS class is used to make text italic", listOf("text italic", "italic", "font italic", "style italic"), 1),
            Question("Which Tailwind utility class sets the font size to extra large", listOf("font lg", "text xl", "size xl", "xl text"), 1),
            Question("Which Tailwind class is used to create a grid layout with four columns", listOf("grid cols 2", "grid cols 3", "grid cols 4", "grid four"), 2),
            Question("Which Tailwind class is used to justify content at the end in flexbox", listOf("justify end", "content end", "flex end", "align end"), 0),
            Question("Which Tailwind class is used to align items vertically at the center", listOf("items start", "items center", "align middle", "center items"), 1),
            Question("Which Tailwind class applies a red background color", listOf("bg red", "background red", "bg red 500", "red background"), 2),
            Question("Which Tailwind class is used to round the corners of an element", listOf("rounded", "corner", "radius", "border curve"), 0),
            Question("Which Tailwind prefix is used for small screen responsive design", listOf("sm", "md", "lg", "xl"), 0),
            Question("Which Tailwind prefix is used to apply utilities for large screen sizes", listOf("sm", "md", "lg", "xl"), 2),
            Question("Which Tailwind configuration file allows customization of themes", listOf("tailwind css", "tailwind config js", "tailwind json", "config css"), 1),
            Question("Which Tailwind class adds transition effects to elements", listOf("animate", "transition", "ease", "transform"), 1),
            Question("Which Tailwind animation utility makes an element bounce", listOf("animate bounce", "animate jump", "animate move", "bounce run"), 0),
            Question("Which Tailwind utility applies spacing between flex items", listOf("gap", "space x", "space y", "spacing"), 1),
            Question("Which Tailwind utility hides an element on the page", listOf("hidden", "invisible", "display none", "none"), 0),
            Question("Which Tailwind class sets text alignment to right", listOf("text right", "align right", "font right", "right text"), 0),
        )
    }
}
