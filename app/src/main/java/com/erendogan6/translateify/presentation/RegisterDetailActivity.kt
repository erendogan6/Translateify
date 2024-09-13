package com.erendogan6.translateify.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.erendogan6.translateify.databinding.ActivityRegisterDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
