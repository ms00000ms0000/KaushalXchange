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

class AssessmentActivityJS : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterJS
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false   // <-- added flag
    private lateinit var sharedPreferences: SharedPreferences // <-- added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_js)

        // <-- added SharedPreferences
        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterJS(questions, userAnswers)
        recyclerView.adapter = questionAdapter

        startTimer()
        submitBtn.setOnClickListener {
            if (!isSubmitted) {
                isSubmitted = true
                evaluateAnswers()
                questionAdapter.notifyDataSetChanged() // refresh UI to show colors
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
        val skillName = "JavaScript" // later you can make dynamic per skill

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
            Question("Which keyword is used to declare a variable with block scope in JavaScript?", listOf("var", "let", "const", "both let and const"), 3),
            Question("What is the default value of an uninitialized variable in JavaScript?", listOf("0", "null", "undefined", "false"), 2),
            Question("Which operator is used to check both value and type in JavaScript?", listOf("==", "!=", "===", "!=="), 2),
            Question("What will be the result of '5' + 3 in JavaScript?", listOf("8", "53", "Error", "Undefined"), 1),
            Question("Which statement is used for decision making in JavaScript?", listOf("for", "if", "switch", "return"), 1),
            Question("Which loop guarantees execution at least once?", listOf("for", "while", "do while", "for of"), 2),
            Question("What is a closure in JavaScript?", listOf("Function with parameters", "Function inside a class", "Function that remembers outer scope", "Function with default values"), 2),
            Question("Which type of function is introduced in ES6 to provide shorter syntax?", listOf("Anonymous function", "Arrow function", "Constructor function", "Callback function"), 1),
            Question("What is the scope of a variable declared with let?", listOf("Global", "Block", "Function", "Module"), 1),
            Question("Which keyword is used to return a value from a function?", listOf("yield", "return", "break", "continue"), 1),
            Question("Which array method is used to filter elements based on a condition?", listOf("map", "filter", "reduce", "forEach"), 1),
            Question("What does the reduce method return?", listOf("Array", "Single value", "Boolean", "Object"), 1),
            Question("Which data structure stores unique values in JavaScript?", listOf("Array", "Object", "Set", "Map"), 2),
            Question("What does the spread operator do in JavaScript?", listOf("Expands arrays or objects", "Loops through arrays", "Creates deep copy", "Declares variables"), 0),
            Question("What is the difference between shallow copy and deep copy?", listOf("Shallow copies only references", "Deep copies only primitives", "Shallow copies nested objects", "Both are identical"), 0),
            Question("Which feature allows JavaScript to manage async tasks without blocking?", listOf("Compiler", "Event loop", "Garbage collector", "Interpreter"), 1),
            Question("Which syntax makes asynchronous code easier to read?", listOf("Callback", "Promise", "Async Await", "Generator"), 2),
            Question("What does the then method in a Promise handle?", listOf("Errors", "Success result", "Looping", "Events"), 1),
            Question("Which function schedules a task after a specific delay?", listOf("setInterval", "setTimeout", "clearTimeout", "requestAnimationFrame"), 1),
            Question("What is event bubbling in JavaScript?", listOf("Events move upward from target to ancestors", "Events move downward from root", "Events occur once only", "Events stop after one handler"), 0),
            Question("Which method selects the first element matching a CSS selector?", listOf("getElementById", "querySelector", "querySelectorAll", "getElementsByClassName"), 1),
            Question("Which Web API stores data even after browser restart?", listOf("SessionStorage", "LocalStorage", "Cookies", "Cache"), 1),
            Question("Which property allows adding and removing CSS classes dynamically?", listOf("innerHTML", "classList", "style", "attributes"), 1),
            Question("Which API is commonly used for fetching data from servers?", listOf("AJAX", "Fetch API", "JSON API", "WebSocket"), 1),
            Question("Which method creates a new element in the DOM?", listOf("appendChild", "createElement", "insertBefore", "cloneNode"), 1),
        )
    }
}
