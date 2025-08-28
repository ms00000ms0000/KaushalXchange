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

class AssessmentActivityC : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterC
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(25) { -1 }
    private var isSubmitted = false   // flag to prevent multiple submissions
    private lateinit var sharedPreferences: SharedPreferences // <-- added


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_c)

        // <-- added SharedPreferences
        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterC(questions, userAnswers) { isSubmitted }
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


        // <-- New logic for My Skills and Skill I Can Teach
        val editor = sharedPreferences.edit()
        val skillName = "C" // later you can make dynamic per skill

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
            Question("Who is known as the father of the C programming language?", listOf("James Gosling", "Dennis Ritchie", "Bjarne Stroustrup", "Ken Thompson"), 1),
            Question("Which function is the entry point of every C program?", listOf("start()", "main()", "init()", "program()"), 1),
            Question("Which header file is required for using printf() in C?", listOf("stdlib.h", "stdio.h", "string.h", "conio.h"), 1),
            Question("Which data type is used to store single characters in C?", listOf("int", "char", "float", "string"), 1),
            Question("Which keyword is used to define a constant variable in C?", listOf("const", "final", "static", "define"), 0),
            Question("What is the default return type of a function in C if not specified?", listOf("int", "float", "void", "double"), 0),
            Question("Which operator is used to access the value stored at an address in C?", listOf("&", "*", "#", "%"), 1),
            Question("Which operator has the highest precedence in C?", listOf("++", "*", "()", "="), 2),
            Question("Which operator is known as the ternary operator in C?", listOf("?:", "&&", "||", "::"), 0),
            Question("Which loop in C is guaranteed to execute at least once?", listOf("for", "while", "do-while", "foreach"), 2),
            Question("Which statement is used to terminate a loop in C?", listOf("exit", "stop", "break", "end"), 2),
            Question("Which control statement is used to skip the current iteration of a loop in C?", listOf("break", "continue", "goto", "switch"), 1),
            Question("Which keyword is used to return a value from a function in C?", listOf("break", "return", "continue", "exit"), 1),
            Question("What is essential to stop infinite recursion in C?", listOf("Recursive case", "Base condition", "Loop counter", "Return type"), 1),
            Question("What is the index of the first element in a C array?", listOf("0", "1", "-1", "Depends on compiler"), 0),
            Question("Which symbol is used to access the value stored at a pointer address in C?", listOf("&", "*", "#", "%"), 1),
            Question("Which function is used to calculate the length of a string in C?", listOf("strlen()", "strcat()", "strcmp()", "strcpy()"), 0),
            Question("Which operator is used to access members of a structure in C?", listOf(".", "->", ":", "::"), 0),
            Question("How is memory allocated for members of a union in C?", listOf("Each member gets separate memory", "All members share the same memory", "Memory is allocated twice", "It depends on compiler"), 1),
            Question("Which keyword is used to define a structure in C?", listOf("struct", "union", "class", "record"), 0),
            Question("Which keyword is used to declare a pointer in C?", listOf("*", "&", "#", "%"), 0),
            Question("Which header file must be included to use string handling functions like strcpy()?", listOf("math.h", "stdio.h", "string.h", "stdlib.h"), 2),
            Question("Which keyword is used to exit from a loop in C?", listOf("stop", "end", "break", "quit"), 2),
            Question("Which function is commonly used to read a single character from input in C?", listOf("getchar()", "scanf()", "gets()", "fgets()"), 0),
            Question("Which storage class in C retains variable value between multiple function calls?", listOf("auto", "static", "register", "extern"), 1)
        )
    }
}
