package com.erendogan6.translateify.presentation.ui.register

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail6Binding

class RegisterDetailFragment6 : Fragment() {
    private var _binding: FragmentRegisterDetail6Binding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                navigateToNextFragment()
            } else {
                Toast
                    .makeText(
                        requireContext(),
                        getString(R.string.notification_permission_denied),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail6Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

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
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigateToNextFragment()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationale()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            navigateToNextFragment()
        }
    }

    private fun showPermissionRationale() {
        AlertDialog
            .Builder(requireContext())
            .setTitle(getString(R.string.notification_permission_required))
            .setMessage(getString(R.string.notification_permission_rationale))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }.setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
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
