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

class TestJS : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterJS
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(50) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_js)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)

        //  Same as AssessmentActivityJS: only pass (questions, userAnswers)
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
        countDownTimer = object : CountDownTimer(60 * 60 * 1000L, 1000) {
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
            Question("Which keyword declares a variable with block scope?", listOf("var", "let", "const", "both let and const"), 1),
            Question("Which value represents absence of value?", listOf("null", "undefined", "both", "NaN"), 2),
            Question("Which operator tests strict equality (value and type)?", listOf("==", "===", "!=", "!=="), 1),
            Question("Which method logs to console?", listOf("print()", "console.log()", "log()", "echo()"), 1),
            Question("Which array method adds element to end?", listOf("pop()", "push()", "shift()", "unshift()"), 1),
            Question("Which method removes last element of array?", listOf("push()", "pop()", "remove()", "delete()"), 1),
            Question("Which creates a function expression?", listOf("function a(){}", "let a = function(){}", "func a(){}", "fn a(){}"), 1),
            Question("Which keywords declare constant?", listOf("const", "let", "var", "static"), 0),
            Question("Which is block-scoped?", listOf("var", "let", "global", "function"), 1),
            Question("Which method converts JSON string to object?", listOf("JSON.stringify()", "JSON.parse()", "JSON.toObject()", "JSON.objectify()"), 1),
            Question("Which method converts object to JSON string?", listOf("JSON.parse()", "JSON.stringify()", "toString()", "stringify()"), 1),
            Question("Which function schedules a task once after delay?", listOf("setInterval()", "setTimeout()", "delay()", "schedule()"), 1),
            Question("Which event is fired when DOM is fully loaded?", listOf("DOMContentLoaded", "load", "ready", "onload"), 0),
            Question("Which returns number true as 1 when coerced?", listOf("true", "1", "''", "None"), 0),
            Question("Which of these is arrow function syntax?", listOf("function => {}", "() => {}", "=> function {}", "lambda => {}"), 1),
            Question("Which method finds first matching element for selector?", listOf("getElementById", "querySelector", "querySelectorAll", "getElementsByClassName"), 1),
            Question("Which operator for exponentiation in JS?", listOf("^", "**", "pow", "exp"), 1),
            Question("Which method transforms array elements using callback?", listOf("map()", "forEach()", "filter()", "reduce()"), 0),
            Question("Which array method filters elements by condition?", listOf("map()", "filter()", "reduce()", "find()"), 1),
            Question("Which returns index of first element satisfying callback?", listOf("find()", "filter()", "findIndex()", "indexOf()"), 2),

            // 20 Moderate
            Question("What is closure in JavaScript?", listOf("Function only", "Function with preserved outer variables", "Global variable", "Class"), 1),
            Question("Which promises method handles success?", listOf("catch()", "then()", "finally()", "resolve()"), 1),
            Question("What is event bubbling?", listOf("Events propagate from root to target", "Events propagate from target up to ancestors", "Events are only on target", "Events do not propagate"), 1),
            Question("Which keyword prevents default event action?", listOf("stopPropagation()", "preventDefault()", "cancel()", "stop()"), 1),
            Question("Which method deep clones objects minimally?", listOf("Object.assign()", "JSON.parse(JSON.stringify(obj))", "clone()", "structuredClone()"), 3),
            Question("Which method chains async operations without callback pyramid?", listOf("callbacks", "promises", "sync", "setTimeout"), 1),
            Question("Which storage persists even after browser restart?", listOf("SessionStorage", "LocalStorage", "Cookies", "Cache"), 1),
            Question("Which API fetches resources over network returning Promise?", listOf("AJAX", "fetch()", "XMLHttpRequest", "get()"), 1),
            Question("Which keyword marks function as async?", listOf("await", "async", "defer", "promis"), 1),
            Question("What does 'this' refer to in arrow functions?", listOf("Global object", "Lexical this from enclosing scope", "Function's own this", "Undefined"), 1),
            Question("Which method flattens nested arrays?", listOf("flat()", "flatten()", "concat()", "merge()"), 0),
            Question("Which method reduces array to single value?", listOf("reduce()", "map()", "filter()", "forEach()"), 0),
            Question("Which returns true for all elements satisfying predicate?", listOf("some()", "every()", "all()", "filter()"), 1),
            Question("Which creates shallow copy of array?", listOf("slice()", "splice()", "copy()", "concat()"), 0),
            Question("Which event delegation improves performance when many children?", listOf("Attach listener on each child", "Attach listener on parent and use event.target", "Use polling", "Use timers"), 1),
            Question("Which operator does short-circuit logical OR return value of?", listOf("Boolean true/false", "First truthy operand or last operand", "Always boolean", "First falsy operand"), 1),
            Question("Which method sets HTML content of element?", listOf("innerText", "innerHTML", "textContent", "outerHTML"), 1),
            Question("Which method attaches handler to event with optional capture?", listOf("on()", "addEventListener()", "attachEvent()", "bindEvent()"), 1),
            Question("Which is correct to check NaN?", listOf("x == NaN", "isNaN(x)", "Number.isNaN(x)", "both isNaN and Number.isNaN depending on needs"), 3),
            Question("Which object stores key-value pairs with any type keys?", listOf("Object", "Map", "Array", "Set"), 1),

            // 10 Advanced
            Question("Which returns lexical environment for modules in modern JS?", listOf("window", "module", "this", "top-level import/export context"), 3),
            Question("Which schedule defers task to microtask queue?", listOf("setTimeout", "Promise.resolve().then()", "setImmediate", "requestAnimationFrame"), 1),
            Question("Which enables cooperative concurrency using async/await behind scenes?", listOf("Threads", "Event loop", "Multiprocessing", "Blocking I/O"), 1),
            Question("Which method best for handling large binary data streams in browser?", listOf("FileReader", "Streams API", "XMLHttpRequest", "fetch() only"), 1),
            Question("Which function serializes structured clone including cyclical refs?", listOf("JSON.stringify", "structuredClone", "postMessage", "cloneDeep"), 1),
            Question("Which technique avoids layout thrashing in DOM manipulations?", listOf("Read after write multiple times", "Batch reads then writes", "Inline styles repeatedly", "Force reflow"), 1),
            Question("Which API helps to observe element visibility for lazy loading?", listOf("IntersectionObserver", "VisibilityObserver", "MutationObserver", "ResizeObserver"), 0),
            Question("Which method creates worker threads in browser?", listOf("Worker()", "Thread()", "createWorker()", "new Worker()"), 3),
            Question("Which provides event loop phases in Node.js?", listOf("Timers, pending callbacks, idle, poll, check, close callbacks", "Only timers", "Only callbacks", "Only poll"), 0),
            Question("Which module system used in modern browsers?", listOf("CommonJS", "AMD", "ES Modules", "UMD"), 2)
        )
    }
}
