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

class AssessmentActivityJava : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterJava
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_java)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterJava(questions, userAnswers) { isSubmitted }
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
            if (userAnswers[i] == questions[i].correctAnswerIndex) {
                score++
            }
        }

        val editor = sharedPreferences.edit()
        val skillName = "Java"

        if (score >= 1) {
            addSkillToList("MySkills", skillName)
        }
        if (score >= 2) {
            addSkillToList("SkillsICanTeach", skillName)
        }
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
            Question("Which keyword is used to define a class in Java?", listOf("define", "class", "struct", "new"), 1),
            Question("What is the size of int in Java?", listOf("2 bytes", "4 bytes", "8 bytes", "Depends on system"), 1),
            Question("Which of these is not a Java feature?", listOf("Object-Oriented", "Platform Independent", "Pointers", "Robust"), 2),
            Question("Which company originally developed Java?", listOf("Microsoft", "Sun Microsystems", "Google", "Oracle"), 1),
            Question("Which method is the entry point of a Java program?", listOf("start()", "main()", "init()", "run()"), 1),
            Question("Which package contains the Random class?", listOf("java.util", "java.lang", "java.io", "java.net"), 0),
            Question("Which of these is a wrapper class?", listOf("int", "Integer", "float", "double"), 1),
            Question("Which access modifier makes members visible only within the same package?", listOf("public", "private", "protected", "default"), 3),
            Question("Which keyword is used to inherit a class in Java?", listOf("this", "extends", "super", "implements"), 1),
            Question("Which of the following is not a valid access modifier?", listOf("public", "protected", "friendly", "private"), 2),
            Question("Which collection class allows key-value pair storage?", listOf("ArrayList", "HashMap", "LinkedList", "HashSet"), 1),
            Question("Which of the following is a marker interface?", listOf("Serializable", "Runnable", "Cloneable", "Readable"), 0),
            Question("Which keyword is used to prevent method overriding?", listOf("static", "final", "abstract", "private"), 1),
            Question("Which of these is not an OOP principle?", listOf("Inheritance", "Encapsulation", "Polymorphism", "Compilation"), 3),
            Question("Which of these exceptions is unchecked?", listOf("IOException", "SQLException", "NullPointerException", "FileNotFoundException"), 2),
            Question("Which keyword is used to create an object in Java?", listOf("create", "new", "malloc", "construct"), 1),
            Question("Which of these is not a reserved keyword?", listOf("const", "goto", "strictfp", "unsigned"), 3),
            Question("Which method is called automatically when an object is created?", listOf("main", "finalize", "constructor", "init"), 2),
            Question("Which class is the parent of all Java classes?", listOf("Object", "Class", "Base", "Main"), 0),
            Question("Which of these cannot be used for a variable name in Java?", listOf("total", "sum", "int", "value"), 2),
            Question("Which operator is used for string concatenation in Java?", listOf("+", "&", ".", "concat"), 0),
            Question("Which of these data types has the smallest range?", listOf("byte", "int", "short", "long"), 0),
            Question("Which loop checks the condition after executing the loop body?", listOf("for", "while", "do-while", "foreach"), 2),
            Question("Which statement is used to exit a loop?", listOf("return", "exit", "break", "continue"), 2),
            Question("Which keyword is used to define an interface in Java?", listOf("class", "interface", "struct", "abstract"), 1)
        )
    }
}
