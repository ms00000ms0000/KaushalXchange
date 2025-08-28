package com.example.kaushalxchange

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val about = """
            
             Kaushal XChange is a platform where users can exchange skills in a unique, collaborative, and interactive way. Unlike traditional learning platforms that focus only on teaching or only on learning, Kaushal XChange empowers users to both learn and teach at the same time. The idea is simple yet powerful: if you know a skill and want to learn another, you can connect with someone who has the skill you want to acquire and who, in turn, wants to learn the skill you already possess. This creates a two-way exchange of knowledge that is engaging, practical, and highly rewarding.

             At Kaushal XChange, we believe that learning should not be a one-way process where one person is always the student and another is always the teacher. Instead, every individual has something valuable to share and something new to learn. For example, if User A knows Skill X and wants to learn Skill Y, and User B knows Skill Y but wants to learn Skill X, our platform connects them so that they can teach and learn from each other. This peer-to-peer learning system ensures that knowledge flows both ways, making the learning experience richer and more impactful.

             To maintain the quality and reliability of the skills being exchanged, Kaushal XChange has a verification system for all users who wish to teach. When a new user registers and expresses interest in teaching a skill, we first provide structured learning modules and conduct multiple assessments to ensure that the user genuinely has expertise in that area. Only after successfully passing these evaluations does the user gain the ability to teach others. This process ensures that learners always receive guidance from verified and competent peers.

             Another key feature of Kaushal XChange is its AI-driven skill matching system. Our intelligent matching engine analyzes the skills you have and the skills you want to learn, then pairs you with the most suitable partners. This eliminates the hassle of searching endlessly for the right person to learn from and ensures efficient and relevant connections. Once a match is found, users can immediately begin their interactive learning journey.

             The learning experience on Kaushal XChange is designed to be dynamic and immersive. We provide a real-time interaction interface similar to Google Meet, where users can see, talk, and share knowledge directly. Learners can ask questions instantly, clarify doubts, and even demonstrate their understanding during the session. To further enhance learning outcomes, once the process is complete, we conduct a final assessment to verify whether the user has successfully acquired the skill.

             Kaushal XChange is not just about learning and teaching—it is also about building a strong network of like-minded individuals. Users can expand their personal and professional networks by connecting with peers who share similar interests. Our chat section adds another dimension of interaction, allowing users to exchange messages, ask follow-up questions, and continue discussions even after live sessions—provided the other person permits chat access.

             Through this combination of skill exchange, AI-driven matchmaking, live interaction, and assessments, Kaushal XChange creates a vibrant ecosystem where learning becomes a shared journey. Our mission is to break down barriers to knowledge, encourage collaborative growth, and create a world where everyone has the chance to both share their expertise and acquire new skills.

             At Kaushal XChange, we are redefining how people learn and teach. We envision a future where knowledge is not confined to classrooms or courses but flows freely between individuals across the globe. With us, every user is both a teacher and a learner, and every interaction is a step toward mutual growth.

             
   
        """.trimIndent()

        findViewById<TextView>(R.id.textAboutUs).text = about
    }
}
