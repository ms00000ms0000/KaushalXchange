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

class TestC : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterC
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(50) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_c)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterC(questions, userAnswers, ::isSubmitted)
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
        val skillName = "C"

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
            Question("Who is known as father of C?", listOf("Dennis Ritchie", "Bjarne Stroustrup", "James Gosling", "Ken Thompson"), 0),
            Question("Which function is the entry point of a C program?", listOf("start()", "init()", "main()", "begin()"), 2),
            Question("Which header contains printf?", listOf("stdlib.h", "stdio.h", "string.h", "math.h"), 1),
            Question("Which symbol denotes pointer declaration?", listOf("&", "*", "%", "#"), 1),
            Question("What is index of first element in array?", listOf("1", "0", "-1", "depends"), 1),
            Question("Which loop executes at least once?", listOf("for", "while", "do-while", "foreach"), 2),
            Question("Which keyword is used to stop a loop?", listOf("stop", "break", "exit", "end"), 1),
            Question("Which is used for single character storage?", listOf("int", "char", "float", "double"), 1),
            Question("Which is correct array declaration?", listOf("int a[];", "int a;", "array a;", "list a;"), 0),
            Question("Which operator gives address of a variable?", listOf("&", "*", "addr", "#"), 0),
            Question("Which is used for dynamic allocation in C?", listOf("new", "malloc", "alloc", "create"), 1),
            Question("Which header for malloc?", listOf("stdlib.h", "stdio.h", "malloc.h", "memory.h"), 0),
            Question("Which returns length of string in C?", listOf("strlen()", "length()", "strsize()", "size()"), 0),
            Question("Which is correct comment syntax in C?", listOf("# comment", "// comment", "/* comment */", "<!-- -->"), 2),
            Question("Which operator is modulus?", listOf("%", "/", "*", "^"), 0),
            Question("Which keyword for constant in C?", listOf("constant", "const", "final", "static"), 1),
            Question("Which function reads formatted input?", listOf("scanf()", "read()", "input()", "get()"), 0),
            Question("Which is escape sequence for newline?", listOf("\\n", "\\t", "\\r", "/n"), 0),
            Question("Which type holds true/false in C?", listOf("bool", "boolean", "_Bool", "int"), 2),
            Question("Which header for string functions?", listOf("strings.h", "string.h", "str.h", "stdio.h"), 1),

            // 20 Moderate
            Question("What does sizeof(char) typically return?", listOf("1", "2", "4", "8"), 0),
            Question("Which statement is used to jump to label?", listOf("goto", "jump", "break", "continue"), 0),
            Question("Which operator dereferences a pointer?", listOf("&", "*", "^", "#"), 1),
            Question("Which storage class persists between function calls?", listOf("auto", "static", "register", "extern"), 1),
            Question("Which library is used for math functions?", listOf("math.h", "stdio.h", "stdlib.h", "string.h"), 0),
            Question("What is function prototype?", listOf("implementation", "declaration", "macro", "typedef"), 1),
            Question("Which format specifier for integer in printf?", listOf("%d", "%f", "%s", "%c"), 0),
            Question("How to declare array of 10 integers?", listOf("int a[10];", "int a;", "array a[10];", "int[10] a;"), 0),
            Question("What happens when using free() on pointer?", listOf("allocates memory", "deallocates memory", "copies memory", "errors"), 1),
            Question("Which is true about pointers and arrays?", listOf("They are identical", "Array decays to pointer in expressions", "Pointer always owned", "Array is pointer type"), 1),
            Question("Which returns pointer to allocated memory or NULL on failure?", listOf("malloc", "calloc", "realloc", "alloc"), 0),
            Question("Which function concatenates strings in C?", listOf("strcat()", "strjoin()", "append()", "concat()"), 0),
            Question("Which keyword defines type alias?", listOf("alias", "typedef", "using", "type"), 1),
            Question("Which operator has highest precedence?", listOf("=", "+", "()", "*"), 2), // note: parentheses highest; choose index 2 as placeholder - but to be consistent choose "()", index2
            Question("Which function copies memory blocks?", listOf("memcpy()", "copy()", "memmove()", "move()"), 0),
            Question("What does 'extern' do?", listOf("declare external linkage", "define function", "allocates memory", "none"), 0),
            Question("Which storage class used for fast access registers?", listOf("register", "static", "auto", "extern"), 0),
            Question("Which conversion specifier for char?", listOf("%c", "%s", "%d", "%f"), 0),
            Question("Which function compares strings?", listOf("strcmp()", "compare()", "strcomp()", "equals()"), 0),
            Question("Which memory is freed using free(ptr)?", listOf("stack memory", "heap memory", "static memory", "register memory"), 1),

            // 10 Advanced
            Question("What is undefined behavior in C?", listOf("Defined by standard", "Behavior not defined by standard", "Throws exception", "Returns zero"), 1),
            Question("How do you declare pointer to function returning int?", listOf("int *f()", "int (*f)()", "int f()*", "(*int) f()"), 1),
            Question("Which function changes size of previously allocated memory?", listOf("resize()", "realloc()", "malloc()", "calloc()"), 1),
            Question("What is difference between malloc and calloc?", listOf("calloc allocates zeroed memory", "malloc zeroes memory", "malloc faster and zeroed", "no difference"), 0),
            Question("What does buffer overflow exploit target?", listOf("Stack/Heap corruptions", "Only IO", "Only CPU", "Network"), 0),
            Question("Which header for setjmp/longjmp?", listOf("setjmp.h", "jmpbuf.h", "setjmp.h is wrong", "setjmp.h"), 0),
            Question("Which technique avoids memory leaks?", listOf("freeing allocated memory", "never using malloc", "using global memory", "increasing stack"), 0),
            Question("How to pass pointer to function to change original pointer?", listOf("pass pointer", "pass pointer-to-pointer", "global variable", "return pointer only"), 1),
            Question("Which cast for void* to int* is required in C++ but not in C?", listOf("explicit cast", "no cast", "reinterpret_cast", "static_cast"), 0),
            Question("Which of following is thread-unsafe in C standard library?", listOf("strtok()", "strcpy()", "memcpy()", "strlen()"), 0)
        )
    }
}
