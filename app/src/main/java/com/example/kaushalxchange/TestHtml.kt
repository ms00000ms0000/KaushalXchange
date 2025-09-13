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

class TestHtml : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterHtml
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(50) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_html)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterHtml(questions, userAnswers, ::isSubmitted)
        recyclerView.adapter = questionAdapter

        startTimer()

        submitBtn.setOnClickListener {
            if (!isSubmitted) {
                evaluateAnswers()
                isSubmitted = true
                questionAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(60 * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format("Time Left: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                if (!isSubmitted) {
                    evaluateAnswers()
                    isSubmitted = true
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
        val skillName = "HTML"

        if (score >= 35) {
            val set = sharedPreferences.getStringSet("AcquiredSkills", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            set.add(skillName)
            editor.putStringSet("AcquiredSkills", set)
        }
        editor.apply()

        val result = if (score >= 35) "Passed" else "Failed"
        AlertDialog.Builder(this)
            .setTitle("Test Complete")
            .setMessage("Score: $score / 50\nResult: $result")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }

    private fun loadQuestions(): List<Question> {
        return listOf(
            // 20 Easy
            Question("What does HTML stand for?", listOf("Hyperlinks and Text Markup", "Hyper Text Markup Language", "High Text Markup Language", "Hyper Tool Markup"), 1),
            Question("Which tag encloses the full HTML document?", listOf("<body>", "<head>", "<html>", "<doc>"), 2),
            Question("Which tag defines the main page header?", listOf("<header>", "<head>", "<h1>", "<top>"), 0),
            Question("Which tag creates a paragraph?", listOf("<p>", "<para>", "<text>", "<br>"), 0),
            Question("Which tag creates a link?", listOf("<a>", "<link>", "<href>", "<url>"), 0),
            Question("Which attribute contains the URL in anchor tag?", listOf("src", "href", "link", "url"), 1),
            Question("Which tag adds an image?", listOf("<image>", "<img>", "<picture>", "<src>"), 1),
            Question("Which attribute gives alternate text for images?", listOf("title", "alt", "desc", "caption"), 1),
            Question("Which tag makes numbered lists?", listOf("<ol>", "<ul>", "<li>", "<nl>"), 0),
            Question("Which tag defines table row?", listOf("<td>", "<tr>", "<th>", "<table>"), 1),
            Question("Which tag defines table header cell?", listOf("<td>", "<tr>", "<th>", "<thead>"), 2),
            Question("Which tag embeds video in HTML5?", listOf("<video>", "<media>", "<movie>", "<embed>"), 0),
            Question("Which tag defines document title shown in browser tab?", listOf("<title>", "<head>", "<tab>", "<name>"), 0),
            Question("Which tag is used to add a favicon?", listOf("<link rel='icon'>", "<favicon>", "<icon>", "<image>"), 0),
            Question("Which tag is used for preformatted text?", listOf("<pre>", "<code>", "<samp>", "<var>"), 0),
            Question("Which tag is used for emphasized text?", listOf("<em>", "<i>", "<strong>", "<b>"), 0),
            Question("Which attribute opens link in new tab?", listOf("blank", "target='_blank'", "new='true'", "tab='new'"), 1),
            Question("Which tag defines metadata in head?", listOf("<meta>", "<data>", "<info>", "<link>"), 0),
            Question("Which doctype is correct for HTML5?", listOf("<!DOCTYPE html>", "<!DOCTYPE HTML5>", "<!DOCTYPE html5>", "<!DOCTYPE>"), 0),
            Question("Which tag groups form controls?", listOf("<fieldset>", "<formgroup>", "<group>", "<controls>"), 0),

            // 20 Moderate
            Question("Which tag is used for semantic navigation?", listOf("<nav>", "<menu>", "<navigation>", "<links>"), 0),
            Question("Which HTML element is used to define important text?", listOf("<strong>", "<b>", "<em>", "<i>"), 0),
            Question("Which attribute is required in <img> tag for accessibility?", listOf("src", "alt", "title", "role"), 1),
            Question("Which attribute on <form> specifies request method?", listOf("action", "method", "type", "request"), 1),
            Question("Which element is used for independent, self-contained content?", listOf("<section>", "<article>", "<div>", "<aside>"), 1),
            Question("Which tag used to include inline CSS?", listOf("<style>", "<css>", "<link>", "<script>"), 0),
            Question("Which tag used to include external CSS file?", listOf("<link>", "<style>", "<import>", "<css>"), 0),
            Question("What is semantic HTML?", listOf("Tags with clear meaning", "Old HTML", "CSS only", "JS only"), 0),
            Question("Which attribute adds ARIA role to element?", listOf("role", "aria", "alt", "title"), 0),
            Question("Which element for caption in table?", listOf("<caption>", "<title>", "<head>", "<summary>"), 0),
            Question("Which tag used for embedding iframe content?", listOf("<iframe>", "<frame>", "<embed>", "<object>"), 0),
            Question("Which attribute used to disable a form control?", listOf("disabled", "readonly", "blocked", "inactive"), 0),
            Question("Which input type for email validation in browser?", listOf("text", "email", "mail", "input-email"), 1),
            Question("Which attribute in <a> sets relation to current document?", listOf("rel", "type", "target", "href"), 0),
            Question("Which tag used to define figure and caption?", listOf("<figure>", "<figurecaption>", "<imgcap>", "<cap>"), 0),
            Question("Which attribute used to lazy-load images in modern browsers?", listOf("loading='lazy'", "defer='true'", "lazy='true'", "async='lazy'"), 0),
            Question("Which http method does form default to?", listOf("GET", "POST", "PUT", "DELETE"), 0),
            Question("Which element used to embed SVG inline?", listOf("<svg>", "<vector>", "<canvas>", "<shape>"), 0),
            Question("Which element enables accessible labels for inputs?", listOf("<label>", "<caption>", "<legend>", "<name>"), 0),
            Question("Which tag used to show keyboard input?", listOf("<kbd>", "<code>", "<samp>", "<pre>"), 0),

            // 10 Advanced
            Question("What attribute adds CSP nonce on script tags to allow inline scripts?", listOf("nonce", "csp", "allow", "security"), 0),
            Question("Which element used for progressive enhancement of content that can be toggled?", listOf("<details>", "<summary>", "<toggle>", "<info>"), 0),
            Question("Which attribute helps with browser prefetching of resource?", listOf("rel='preload'", "prefetch='true'", "fetch='true'", "link='preload'"), 0),
            Question("What is purpose of srcset on <img>?", listOf("specify multiple image sources for different viewport densities", "lazy load images", "embed images inline", "link images"), 0),
            Question("Which attribute used to provide fallback for <picture> element?", listOf("src", "fallback", "data-src", "poster"), 0),
            Question("Which element used to declare application manifest?", listOf("<link rel='manifest'>", "<manifest>", "<app-manifest>", "<app>"), 0),
            Question("What does ARIA stand for?", listOf("Accessible Rich Internet Applications", "Accessible Rich Interactive Apps", "Accessible React Interface Apps", "Accessible Rich Information Apps"), 0),
            Question("Which attribute sets embedded audio to autoplay muted to satisfy browser policy?", listOf("autoplay muted", "autoplay", "muted", "play='auto'"), 0),
            Question("Which header prevents clickjacking by controlling frame ancestors?", listOf("X-Frame-Options", "Content-Security-Policy: frame-ancestors", "X-Content-Type-Options", "Referrer-Policy"), 1),
            Question("What is semantic purpose of <main> element?", listOf("Primary content of document", "Footer", "Header", "Sidebar"), 0)
        )
    }
}
