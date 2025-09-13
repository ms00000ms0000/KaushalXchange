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

class TestJava : AppCompatActivity() {

    private lateinit var questionAdapter: QuestionAdapterJava
    private lateinit var questions: List<Question>
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val userAnswers = IntArray(50) { -1 }
    private var isSubmitted = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_java)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        questions = loadQuestions()

        val recyclerView = findViewById<RecyclerView>(R.id.questionRecyclerView)
        timerText = findViewById(R.id.timerText)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        questionAdapter = QuestionAdapterJava(questions, userAnswers, ::isSubmitted)
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
        val skillName = "Java"

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
            Question("Which keyword declares a class in Java?", listOf("class", "struct", "object", "def"), 0),
            Question("Which method signature is entry point for Java application?", listOf("public static void main(String[] args)", "public void main()", "main()", "void main()"), 0),
            Question("Which type holds true/false?", listOf("int", "boolean", "Boolean", "bool"), 1),
            Question("Which operator concatenates strings?", listOf("+", "&&", ".", "concat"), 0),
            Question("Which package contains ArrayList?", listOf("java.util", "java.lang", "java.io", "java.collection"), 0),
            Question("Which keyword prevents subclassing?", listOf("sealed", "final", "static", "private"), 1),
            Question("Which is used to create object?", listOf("construct", "new", "create", "init"), 1),
            Question("Which interface marker indicates serializable?", listOf("Serializable", "Cloneable", "Runnable", "Comparable"), 0),
            Question("Which exception is unchecked?", listOf("IOException", "NullPointerException", "FileNotFoundException", "SQLException"), 1),
            Question("Which is wrapper for int?", listOf("Integer", "Int", "i32", "Number"), 0),
            Question("Which loop checks condition before body?", listOf("do-while", "while", "for-each", "for"), 1),
            Question("Which accesses only in same class?", listOf("public", "protected", "private", "default"), 2),
            Question("Which method in String creates lowercase?", listOf("toLowerCase()", "lower()", "downcase()", "small()"), 0),
            Question("Which keyword used for inheritance?", listOf("implements", "extends", "inherits", "derives"), 1),
            Question("Which import used for input-output classes?", listOf("java.io.*", "java.util.*", "java.net.*", "java.lang.*"), 0),
            Question("Which type stores decimal numbers?", listOf("int", "float", "double", "char"), 2),
            Question("What is output of 5/2 using ints?", listOf("2.5", "2", "3", "Error"), 1),
            Question("Which statement exits loop?", listOf("exit", "break", "stop", "return"), 1),
            Question("Which method compares strings value?", listOf("==", "equals()", "compare()", "same()"), 1),
            Question("Which method starts a thread when called?", listOf("run()", "start()", "execute()", "begin()"), 1),

            // 20 Moderate
            Question("What is JVM?", listOf("Java Vendor Model", "Java Virtual Machine", "Java Variable Model", "Java Version Manager"), 1),
            Question("Which collection preserves insertion order?", listOf("HashSet", "HashMap", "LinkedHashMap", "TreeSet"), 2),
            Question("Which keyword used to define interface?", listOf("class", "interface", "abstract", "implements"), 1),
            Question("What is autoboxing?", listOf("Converting primitive to wrapper automatically", "Boxing in GUI", "Packing arrays", "Serialization"), 0),
            Question("Which method belongs to Object class?", listOf("toString()", "split()", "parse()", "format()"), 0),
            Question("Which access allows subclasses in package access?", listOf("private", "default", "protected", "public"), 2),
            Question("Which exception must be declared or handled?", listOf("RuntimeException", "NullPointerException", "IOException", "ArithmeticException"), 2),
            Question("Which construct ensures finally runs?", listOf("try/catch", "try/finally", "try/catch/finally", "only try"), 2),
            Question("Which class used for thread synchronization block?", listOf("synchronized", "sync", "mutex", "lock"), 0),
            Question("Which function converts String to int?", listOf("Integer.parseInt()", "parseInt()", "toInt()", "String.toInt()"), 0),
            Question("Which collection allows duplicates?", listOf("Set", "List", "Map", "Tree"), 1),
            Question("Which Java version introduced lambda expressions?", listOf("Java 7", "Java 8", "Java 9", "Java 6"), 1),
            Question("Which class is root of all classes?", listOf("Base", "Root", "Object", "Main"), 2),
            Question("Which method to stop thread safely?", listOf("stop()", "interrupt()", "kill()", "terminate()"), 1),
            Question("What is purpose of volatile keyword?", listOf("Performance", "Visibility across threads", "Memory allocation", "Garbage collection"), 1),
            Question("Which collection is synchronized by default?", listOf("ArrayList", "Vector", "LinkedList", "HashMap"), 1),
            Question("Which operator used to compare object references?", listOf("==", "equals()", "===", "match"), 0),
            Question("Which stream is used for binary IO?", listOf("Reader/Writer", "InputStream/OutputStream", "Scanner", "PrintStream"), 1),
            Question("Which method adds element to collection end?", listOf("add()", "put()", "offer()", "append()"), 0),
            Question("Which keyword used to create anonymous inner class?", listOf("new", "class", "anonymous", "inner"), 0),

            // 10 Advanced
            Question("What is type erasure in Java generics?", listOf("Generics removed at runtime", "Generics enforced at runtime", "No effect", "Compile-time error"), 0),
            Question("Which design pattern restricts class instantiation to one object?", listOf("Factory", "Singleton", "Builder", "Adapter"), 1),
            Question("Which stream supports character encoding automatically?", listOf("InputStream", "Reader", "FileInputStream", "OutputStream"), 1),
            Question("What is difference between equals() and == for objects?", listOf("Both same", "equals compares values, == compares references", "== compares values", "equals compares types"), 1),
            Question("Which mechanism allows lazy initialization and thread safety?", listOf("Double-checked locking", "Synchronized method only", "Static initializer", "None"), 0),
            Question("What is a checked exception?", listOf("Exception not checked at compile", "Exception must be declared or handled", "Runtime error", "Fatal error"), 1),
            Question("Which Java memory area stores object instances?", listOf("Stack", "Heap", "Code area", "CPU registers"), 1),
            Question("Which tool is used to analyze heap memory dumps?", listOf("jmap", "jhat/jvisualvm", "javac", "jar"), 1),
            Question("Which method signature in interface before Java 8 cannot have body?", listOf("default methods", "abstract methods", "static methods", "private methods"), 1),
            Question("Which feature introduced in Java 9 relates to modules?", listOf("Project Jigsaw/modules system", "Lambda", "Streams", "Annotations"), 0)
        )
    }
}
