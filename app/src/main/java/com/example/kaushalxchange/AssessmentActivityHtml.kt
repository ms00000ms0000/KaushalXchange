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

class AssessmentActivityHtml : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterHtml
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false   // <-- added flag
    private lateinit var sharedPreferences: SharedPreferences // <-- added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_html)

        // <-- added SharedPreferences
        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterHtml(questions, userAnswers) { isSubmitted }
        recyclerView.adapter = questionAdapter

        startTimer()

        submitBtn.setOnClickListener {
            if (!isSubmitted) {
                isSubmitted = true
                evaluateAnswers()
                questionAdapter.notifyDataSetChanged() // refresh to apply colors
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
        val skillName = "HTML" // later you can make dynamic per skill

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
            Question("What does HTML stand for", listOf("Hyperlinks and Text Markup Language", "Hyper Text Markup Language", "High Text Markup Language", "Hyper Tool Markup Language"), 1),
            Question("Which tag is used to define the main content of an HTML document", listOf("head", "html", "body", "title"), 2),
            Question("Which of the following represents the correct structure of an HTML document", listOf("head before html", "html head body", "body html head", "head body html"), 1),
            Question("Which attribute specifies additional information about an element", listOf("property", "attribute", "value", "id"), 1),
            Question("Which tag is used to create the largest heading in HTML", listOf("h6", "h1", "head", "title"), 1),
            Question("Which tag is used to display preformatted text", listOf("pre", "p", "code", "format"), 0),
            Question("Which tag is used to create a numbered list", listOf("ul", "ol", "li", "dl"), 1),
            Question("Which tag is used to represent a list item", listOf("ol", "li", "ul", "dl"), 1),
            Question("Which attribute is mandatory in the anchor tag", listOf("alt", "href", "src", "target"), 1),
            Question("Which tag is used to display an image", listOf("img", "image", "src", "picture"), 0),
            Question("Which attribute of the image tag is used for alternative text", listOf("title", "alt", "desc", "caption"), 1),
            Question("Which attribute in the anchor tag is used to open link in a new tab", listOf("open", "new", "target", "tab"), 2),
            Question("Which tag is used to define a table row", listOf("th", "td", "tr", "table"), 2),
            Question("Which tag is used to create a table header cell", listOf("td", "tr", "th", "thead"), 2),
            Question("Which input type is used to select only one option from multiple choices", listOf("checkbox", "radio", "select", "dropdown"), 1),
            Question("Which attribute of the form tag specifies where to send the form data", listOf("action", "method", "target", "name"), 0),
            Question("Which tag is used to embed a video in HTML5", listOf("audio", "media", "video", "movie"), 2),
            Question("Which semantic element is used to represent a navigation menu", listOf("header", "footer", "section", "nav"), 3),
            Question("Which semantic element is used to represent independent content like a blog post", listOf("section", "article", "aside", "main"), 1),
            Question("Which tag is used to embed audio in HTML5", listOf("sound", "audio", "music", "media"), 1),
            Question("Which HTML5 element is used for 2D drawing", listOf("canvas", "svg", "draw", "graphic"), 0),
            Question("Which tag is used to embed scalable vector graphics", listOf("vector", "svg", "canvas", "shape"), 1),
            Question("Which HTML5 feature allows storing data in browser permanently", listOf("cookies", "local storage", "session storage", "database"), 1),
            Question("Which HTML5 API is used to detect user location", listOf("geoAPI", "geolocation", "location", "gps"), 1),
            Question("Which HTML5 feature is used for offline web applications", listOf("local storage", "app cache", "cookies", "web worker"), 1)
        )
    }
}
