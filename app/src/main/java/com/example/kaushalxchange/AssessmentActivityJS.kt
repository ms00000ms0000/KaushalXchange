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

class AssessmentActivityJS : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterJS
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_js)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        // Corrected: QuestionAdapterJS takes only (questions, userAnswers)
        questionAdapter = QuestionAdapterJS(questions, userAnswers)
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
        val skillName = "JavaScript"

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
            Question("Which company developed JavaScript?", listOf("Netscape", "Microsoft", "Google", "Oracle"), 0),
            Question("Which keyword is used to declare a variable in JavaScript?", listOf("var", "int", "String", "declare"), 0),
            Question("Which symbol is used for comments in JavaScript?", listOf("//", "/*", "#", "--"), 0),
            Question("Which method is used to display alerts in the browser?", listOf("alert()", "msg()", "prompt()", "confirm()"), 0),
            Question("Which operator is used for strict equality?", listOf("==", "===", "=", "!=="), 1),
            Question("Which function is used to parse a string into an integer?", listOf("parseInt()", "parse()", "int()", "parseFloat()"), 0),
            Question("Which keyword declares a constant in JavaScript?", listOf("const", "var", "let", "static"), 0),
            Question("Which method adds an element to the end of an array?", listOf("push()", "pop()", "shift()", "unshift()"), 0),
            Question("Which method removes the last element of an array?", listOf("remove()", "pop()", "delete()", "splice()"), 1),
            Question("Which function is used to print to the console?", listOf("console.log()", "print()", "log()", "echo()"), 0),
            Question("Which loop executes at least once?", listOf("for", "while", "do...while", "foreach"), 2),
            Question("Which keyword defines a class?", listOf("function", "class", "object", "define"), 1),
            Question("Which symbol is used for arrow functions?", listOf("=>", "->", "::", "<-"), 0),
            Question("Which method converts JSON string into an object?", listOf("JSON.parse()", "JSON.stringify()", "toObject()", "parseJSON()"), 0),
            Question("Which method converts object into a JSON string?", listOf("JSON.parse()", "JSON.stringify()", "toString()", "convert()"), 1),
            Question("Which keyword is used for error handling?", listOf("try...catch", "if...else", "throw", "catch"), 0),
            Question("Which object represents the global scope in browsers?", listOf("document", "window", "global", "this"), 1),
            Question("Which function is used to set a delay?", listOf("setDelay()", "setTimeout()", "delay()", "timeout()"), 1),
            Question("Which function is used to repeat execution?", listOf("setTimeout()", "setInterval()", "loop()", "repeat()"), 1),
            Question("Which event occurs when a button is clicked?", listOf("onhover", "onclick", "onpress", "onchange"), 1),
            Question("Which event occurs when a page finishes loading?", listOf("onload", "onstart", "ready", "finish"), 0),
            Question("Which operator is used for string concatenation?", listOf("+", "&", ".", ","), 0),
            Question("Which method joins array elements into a string?", listOf("concat()", "join()", "toString()", "combine()"), 1),
            Question("Which statement terminates a loop?", listOf("return", "exit", "break", "stop"), 2),
            Question("Which keyword is used to import modules?", listOf("require", "import", "include", "load"), 1)
        )
    }
}
