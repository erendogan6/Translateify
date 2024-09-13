package com.erendogan6.translateify.presentation.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.erendogan6.translateify.databinding.FragmentRegisterDetail7Binding
import com.erendogan6.translateify.presentation.MainActivity
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment7 : Fragment() {
    private var _binding: FragmentRegisterDetail7Binding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterDetail7Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        saveDataAndNavigate()

        // Observe the loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe any error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), "Veri yüklenirken hata oluştu: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDataAndNavigate() {
        // Show loading state
        viewModel.setLoading(true) // Use setLoading method from ViewModel

        // Call the save method in ViewModel
        viewModel.saveUserToFirebase(
            onSuccess = {
                Toast.makeText(requireContext(), "Kayıt başarıyla tamamlandı!", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            },
            onFailure = { exception ->
                Toast.makeText(requireContext(), "Kayıt başarısız oldu: ${exception.message}", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
