package com.erendogan6.translateify.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.erendogan6.translateify.databinding.ActivityLoginBinding
import com.erendogan6.translateify.presentation.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Geri butonuna tıklanma olayını işleme
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        // Giriş yap butonuna tıklanma olayını işleme
        binding.signInButton.setOnClickListener {
            val email =
                binding.emailInput.text
                    .toString()
                    .trim()
            val password =
                binding.passwordInput.text
                    .toString()
                    .trim()

            // Giriş yapma fonksiyonunu çağır
            if (validateInputs(email, password)) {
                signInUser(email, password)
            }
        }
    }

    // Kullanıcı giriş yapma fonksiyonu
    private fun signInUser(
        email: String,
        password: String,
    ) {
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Başarılı giriş, MainActivity'ye yönlendir
                    redirectToMainActivity()
                } else {
                    // Hata mesajını göster
                    Toast.makeText(this, task.exception?.message ?: "Giriş yapılamadı", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Girdi doğrulama
    private fun validateInputs(
        email: String,
        password: String,
    ): Boolean =
        when {
            email.isEmpty() -> {
                binding.emailInputLayout.error = "Email boş olamaz"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInputLayout.error = "Geçerli bir email girin"
                false
            }
            password.isEmpty() -> {
                binding.passwordInputLayout.error = "Parola boş olamaz"
                false
            }
            password.length < 6 -> {
                binding.passwordInputLayout.error = "Parola en az 6 karakter olmalı"
                false
            }
            else -> {
                // Hataları temizle
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                true
            }
        }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // LoginActivity'yi bitir, geri gidilmesini engelle
    }
}
