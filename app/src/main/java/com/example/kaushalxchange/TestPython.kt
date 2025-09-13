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

class TestPython : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(50) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_python)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapter(questions, userAnswers, ::isSubmitted)
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

        // add to AcquiredSkills if passed
        val editor = sharedPreferences.edit()
        val skillName = "Python"

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
            Question("What is the output of print(2 + 3)?", listOf("23", "5", "Error", "2+3"), 1),
            Question("Which symbol starts a comment in Python?", listOf("//", "#", "/*", "--"), 1),
            Question("Which keyword defines a function in Python?", listOf("function", "def", "fun", "define"), 1),
            Question("What data type is produced by 3 / 1 in Python 3?", listOf("int", "float", "str", "bool"), 1),
            Question("Which of these is a list literal?", listOf("{1,2}", "(1,2)", "[1,2]", "<1,2>"), 2),
            Question("How do you get the length of a string s?", listOf("len(s)", "size(s)", "s.len()", "length(s)"), 0),
            Question("Which statement is used for conditional branching?", listOf("for", "while", "if", "switch"), 2),
            Question("What does '==' do?", listOf("assignment", "comparison", "comment", "function"), 1),
            Question("Which keyword creates a loop that iterates over sequence?", listOf("for", "loop", "repeat", "foreach"), 0),
            Question("What is the boolean value of an empty list []?", listOf("True", "False", "Error", "None"), 1),
            Question("Which is the correct way to open a file for reading?", listOf("open('f','w')", "open('f','r')", "open.read('f')", "read('f')"), 1),
            Question("How do you convert a string '12' to integer?", listOf("int('12')", "parseInt('12')", "toInt('12')", "Integer('12')"), 0),
            Question("Which operator is exponentiation in Python?", listOf("^", "**", "pow", "//"), 1),
            Question("Which keyword skips current loop iteration?", listOf("stop", "break", "continue", "skip"), 2),
            Question("How do you make a tuple with one element x?", listOf("(x)", "(x,)", "[x]", "{x}"), 1),
            Question("Which method adds item to list at end?", listOf("add()", "append()", "push()", "insert()"), 1),
            Question("Which keyword exits a function and returns a value?", listOf("exit", "return", "break", "stop"), 1),
            Question("What does .split() do on a string?", listOf("split into chars", "split into list by separator", "remove spaces", "convert to list of ints"), 1),
            Question("Which built-in creates a range of numbers?", listOf("range()", "list()", "interval()", "sequence()"), 0),
            Question("Which module provides regular expressions?", listOf("regexp", "re", "regex", "pyre"), 1),

            // 20 Moderate
            Question("What is list comprehension?", listOf("A loop", "A way to create lists concisely", "A function", "An exception"), 1),
            Question("How to catch all exceptions in Python?", listOf("except Exception:", "catch Exception:", "except:", "handle Exception"), 2),
            Question("Which method removes and returns last item from list?", listOf("remove()", "del()", "pop()", "discard()"), 2),
            Question("What does the 'with' statement provide when opening files?", listOf("faster IO", "auto-close on exit", "multithreading", "encryption"), 1),
            Question("Which is a valid set literal?", listOf("{1,2,3}", "[1,2,3]", "(1,2,3)", "<1,2>"), 0),
            Question("What is a decorator in Python?", listOf("A wrapper to modify functions", "A data type", "A loop", "An exception"), 0),
            Question("How to define an anonymous function?", listOf("func x:", "lambda x: x+1", "def lambda:", "anon x->x"), 1),
            Question("Which method returns index of an item in list?", listOf("idx()", "index()", "find()", "search()"), 1),
            Question("What is the output of: bool('False')?", listOf("False", "True", "Error", "None"), 1),
            Question("How to add key:value to dict d?", listOf("d.add(k,v)", "d[k]=v", "d.push(k,v)", "d.insert(k,v)"), 1),
            Question("What does enumerate(seq) return?", listOf("values only", "index,value pairs", "keys", "iterator of keys"), 1),
            Question("Which yields values lazily and can be iterated once?", listOf("list", "tuple", "generator", "set"), 2),
            Question("Which builtin sorts a list in place?", listOf("sorted()", "sort()", "order()", "arrange()"), 1),
            Question("What is correct to import only sqrt from math?", listOf("import math.sqrt", "from math import sqrt", "import sqrt from math", "include math.sqrt"), 1),
            Question("Which of these is true about Python strings?", listOf("Immutable", "Mutable", "Always UTF-8", "Contain only ASCII"), 0),
            Question("What is purpose of __init__ in class?", listOf("Destructor", "Constructor initializer", "Stringify", "Main function"), 1),
            Question("Which statement raises a custom exception?", listOf("throw MyError", "raise MyError()", "error MyError", "except MyError"), 1),
            Question("How to get keys of dict d as list?", listOf("list(d.keys())", "d.keys()", "d.getkeys()", "keys(d)"), 0),
            Question("Which function returns current local namespace as dict?", listOf("locals()", "globals()", "namespace()", "env()"), 0),
            Question("What will filter(func, seq) return?", listOf("List", "Iterator of filtered items", "Count", "Boolean"), 1),

            // 10 Advanced
            Question("Which method returns the mro of a class?", listOf("mro()", "getmro()", "__mro__", "class.mro"), 0),
            Question("Which tool helps type checking in Python?", listOf("mypy", "pycheck", "pytype", "typepy"), 0),
            Question("What does GIL stand for in CPython?", listOf("Global Interpreter Lock", "General Interpreter Layer", "Global Isolation Lock", "Generic Interpreter Line"), 0),
            Question("Which is true about async/await?", listOf("Blocks thread", "Enables cooperative concurrency", "Only for IO-bound CPU heavy", "Replaces threads"), 1),
            Question("Which method is used to run coroutine in Python 3.7+", listOf("async.run()", "asyncio.run()", "run_async()", "await.run()"), 1),
            Question("Which descriptor protocol method retrieves attribute value?", listOf("__get__", "__set__", "__delete__", "__call__"), 0),
            Question("What does functools.lru_cache do?", listOf("Invalidates cache", "Memoizes function results", "Parallelizes function", "Schedules job"), 1),
            Question("Which built-in ensures deterministic hashing for debug?", listOf("PYTHONHASHSEED", "PYDEBUG", "PYHASH", "HASHDEBUG"), 0),
            Question("What is purpose of __slots__ in class?", listOf("Allow dynamic attributes", "Limit allowed attributes and save memory", "Make class static", "Provide slots for multi-threading"), 1),
            Question("Which mechanism avoids blocking in async IO loop?", listOf("spawn threads", "event loop and callbacks", "multiprocessing", "signals"), 1)
        )
    }
}
