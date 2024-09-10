package com.erendogan6.translateify.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.erendogan6.translateify.R
import com.erendogan6.translateify.presentation.ui.LearnedWordsFragment
import com.erendogan6.translateify.presentation.ui.RandomWordsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, RandomWordsFragment())
                .commit()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_words -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, RandomWordsFragment())
                        .commit()
                    true
                }
                R.id.nav_favorites -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, LearnedWordsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
