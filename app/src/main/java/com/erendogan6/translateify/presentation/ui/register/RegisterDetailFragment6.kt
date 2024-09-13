package com.erendogan6.translateify.presentation.ui.register

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.databinding.FragmentRegisterDetail6Binding

class RegisterDetailFragment6 : Fragment() {
    private var _binding: FragmentRegisterDetail6Binding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                navigateToNextFragment()
            } else {
                Toast.makeText(requireContext(), "Bildirim izni reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {
        private const val CHANNEL_ID = "translateify_channel"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterDetail6Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannelIfNeeded()

        binding.continueButton.setOnClickListener {
            handleNotificationPermission()
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.skipButton.setOnClickListener {
            navigateToNextFragment()
        }
    }

    private fun handleNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                navigateToNextFragment()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            navigateToNextFragment()
        }
    }

    private fun createNotificationChannelIfNeeded() {
        val name = "Translateify Channel"
        val descriptionText = "Translateify Bildirim Kanalı"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

        val notificationManager: NotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun navigateToNextFragment() {
        val action = RegisterDetailFragment6Directions.actionRegisterDetailFragment6ToRegisterDetailFragment7()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
