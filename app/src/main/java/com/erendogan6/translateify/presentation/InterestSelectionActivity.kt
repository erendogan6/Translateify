package com.erendogan6.translateify.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erendogan6.translateify.databinding.ActivityInterestSelectionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InterestSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestSelectionBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInterestSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val difficultyLevels = listOf("Kolay", "Orta", "Zor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.difficultySpinner.adapter = adapter

        binding.saveInterestButton.setOnClickListener {
            saveUserPreferences()
        }
    }

    private fun saveUserPreferences() {
        val selectedInterests = mutableListOf<String>()
        if (binding.checkboxNature.isChecked) selectedInterests.add("Doğa ve Çevre")
        if (binding.checkboxScience.isChecked) selectedInterests.add("Bilim")
        if (binding.checkboxHealth.isChecked) selectedInterests.add("Sağlık ve Vücut")
        if (binding.checkboxTechnology.isChecked) selectedInterests.add("Teknoloji")
        if (binding.checkboxFood.isChecked) selectedInterests.add("Yiyecek ve İçecek")
        if (binding.checkboxTravel.isChecked) selectedInterests.add("Gezginler ve Turistler")
        if (binding.checkboxEmotions.isChecked) selectedInterests.add("Duygular ve İfadeler")
        if (binding.checkboxDailyLife.isChecked) selectedInterests.add("Günlük Yaşam ve İletişim")

        val difficulty = binding.difficultySpinner.selectedItem.toString()

        val user = auth.currentUser
        val userId = user?.uid ?: return
        val email = user.email ?: "Unknown"
        val registrationDate = getCurrentDate()

        val userPreferences =
            hashMapOf(
                "email" to email,
                "registrationDate" to registrationDate,
                "interests" to selectedInterests,
                "difficulty" to difficulty,
            )

        firestore
            .collection("users")
            .document(userId)
            .set(userPreferences)
            .addOnSuccessListener {
                Toast.makeText(this, "Tercihler başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Aktiviteyi kapat
            }.addOnFailureListener {
                Toast.makeText(this, "Tercihler kaydedilemedi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
