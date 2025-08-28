package com.example.kaushalxchange

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences // <-- added
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AssessmentActivity : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false   // <-- new flag
    private lateinit var sharedPreferences: SharedPreferences // <-- added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)

        // <-- added SharedPreferences
        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapter(questions, userAnswers, ::isSubmitted) // pass flag
        recyclerView.adapter = questionAdapter

        startTimer()

        submitBtn.setOnClickListener {
            evaluateAnswers()
            isSubmitted = true
            questionAdapter.notifyDataSetChanged() // refresh to show colors
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
                evaluateAnswers()
                isSubmitted = true
                questionAdapter.notifyDataSetChanged()
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
        val skillName = "Python" // later you can make dynamic per skill

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
            Question("What is the output of print(type([]))?", listOf("list", "dict", "tuple", "set"), 0),
            Question("Which of these is a mutable type?", listOf("tuple", "string", "list", "int"), 2),
            Question("What is used to define a function in Python?", listOf("func", "def", "define", "lambda"), 1),
            Question("Which symbol is used to comment a line?", listOf("//", "#", "/* */", "--"), 1),
            Question("What is the output of: print(2 ** 3)?", listOf("5", "6", "8", "9"), 2),
            Question("Which keyword is used for loop termination?", listOf("exit", "stop", "break", "terminate"), 2),
            Question("Which file mode opens file for writing only?", listOf("r", "w", "a", "x"), 1),
            Question("What will open('file.txt', 'r') do?", listOf("Write to file", "Delete file", "Read file", "Append to file"), 2),
            Question("Which module is used for regular expressions?", listOf("regex", "re", "pyregex", "express"), 1),
            Question("Which method is used to add item to a list?", listOf("add()", "insert()", "append()", "push()"), 2),
            Question("What is the correct way to create a dictionary?", listOf("{'a':1, 'b':2}", "[1,2,3]", "{1,2,3}", "('a',1)"), 0),
            Question("Which loop is used to iterate over a sequence?", listOf("if", "for", "while", "switch"), 1),
            Question("What is the keyword for exception handling?", listOf("catch", "try", "throw", "error"), 1),
            Question("Which data type is immutable?", listOf("list", "set", "tuple", "dict"), 2),
            Question("How do you declare a variable in Python?", listOf("int x = 5", "x = 5", "declare x = 5", "let x = 5"), 1),
            Question("Which operator is used for floor division?", listOf("/", "//", "%", "**"), 1),
            Question("What will len('hello') return?", listOf("4", "5", "6", "Error"), 1),
            Question("Which keyword creates a class?", listOf("function", "define", "class", "object"), 2),
            Question("Which method is used to read all lines from a file?", listOf("readlines()", "read()", "readall()", "getlines()"), 0),
            Question("What does the range(5) function return?", listOf("[1,2,3,4,5]", "[0,1,2,3,4]", "[0,1,2,3,4,5]", "[5,4,3,2,1]"), 1),
            Question("Which method is used to remove items from a list?", listOf("delete()", "discard()", "pop()", "remove()"), 3),
            Question("How do you open a file in append mode?", listOf("'a'", "'r'", "'w'", "'x'"), 0),
            Question("What is the output of bool(0)?", listOf("True", "False", "None", "0"), 1),
            Question("What keyword is used to define a lambda function?", listOf("lambda", "def", "function", "fun"), 0),
            Question("What is the output of print('2' + '3')?", listOf("5", "23", "2 3", "Error"), 1)
        )
    }
}
