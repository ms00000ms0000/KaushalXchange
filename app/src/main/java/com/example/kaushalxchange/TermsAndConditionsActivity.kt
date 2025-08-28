package com.example.kaushalxchange

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TermsAndConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        val terms = """
           
            Welcome to Kaushal XChange. By using our app, you agree to abide by our rules, policies, and guidelines. Please read these Terms and Conditions carefully before accessing or using our services, as they outline the rights, responsibilities, and obligations between you (the user) and Kaushal XChange (the provider of the application). By downloading, installing, or using our application, you acknowledge that you have read, understood, and agree to be bound by these Terms. If you do not agree with any part of these Terms, you must discontinue use of the app immediately.

             1. Eligibility and Account Responsibility

            To use Kaushal XChange, you must be at least 13 years of age or have the consent of a parent or guardian. By registering, you agree to provide accurate, complete, and updated information, including your name, email address, and any other details requested. You are responsible for maintaining the confidentiality of your login credentials and for all activities under your account. If you suspect unauthorized use of your account, you must notify us immediately.

             2. User Obligations

            You agree to use Kaushal XChange only for lawful purposes and in compliance with all applicable local, national, and international laws and regulations. You may not:

             Post, upload, or share content that is offensive, discriminatory, or violates the rights of others.
             Engage in activities that could harm, disable, or disrupt the functioning of the app.
             Attempt to hack, reverse engineer, or gain unauthorized access to our systems or databases.
             Violation of these obligations may result in suspension or permanent termination of your account.

             3. Services and Content

            Kaushal XChange provides a platform to learn, share, and exchange skills. The content available through the app may include educational material, tutorials, discussions, and user-generated content. While we strive to maintain the quality and reliability of the information provided, we do not guarantee the accuracy, completeness, or usefulness of any content. Users are solely responsible for evaluating and applying the knowledge gained from the app.

             4. Intellectual Property

            All trademarks, logos, graphics, designs, and software associated with Kaushal XChange are owned by us or our licensors. Users may not copy, modify, distribute, or exploit any part of the app for commercial purposes without our prior written consent. Any unauthorized use of our intellectual property is strictly prohibited and may result in legal action.

             5. Privacy and Data Protection

            Your privacy is important to us. By using the app, you consent to our collection, use, and storage of your information as described in our Privacy Policy. We may collect personal data such as your name, email, and usage behavior to improve user experience. We take reasonable measures to safeguard your information but cannot guarantee absolute security.

             6. Limitation of Liability

            Kaushal XChange is provided on an "as is" and "as available" basis. We do not guarantee uninterrupted access, error-free operation, or that the app will be free of viruses or harmful components. To the maximum extent permitted by law, Kaushal XChange and its affiliates shall not be held liable for any direct, indirect, incidental, or consequential damages arising from your use or inability to use the app.

             7. Termination of Use

            We reserve the right to suspend or terminate your access to the app at any time, without prior notice, if we believe you have violated these Terms. Upon termination, your right to use the app ceases immediately, and you must uninstall it from your device.

            8. Modifications to Terms

            We may update or modify these Terms and Conditions at any time. Any changes will be effective immediately once posted within the app. It is your responsibility to review these Terms periodically. Continued use of the app after modifications constitutes acceptance of the revised Terms.

            9. Governing Law and Dispute Resolution

            These Terms and Conditions shall be governed by and construed in accordance with the laws of India. Any disputes arising under or in connection with these Terms shall be subject to the exclusive jurisdiction.

            10. Contact Information

            If you have any questions, concerns, or feedback regarding these Terms, you may contact us through the app’s Contact Us section.

            
        """.trimIndent()

        findViewById<TextView>(R.id.textTerms).text = terms
    }
}
