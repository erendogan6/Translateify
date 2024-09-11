package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentQuizBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment(R.layout.fragment_quiz) {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    private val questions =
        listOf(
            Question("What is the meaning of 'apple'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'banana'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'carrot'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 1),
            Question("What is the meaning of 'red'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 2),
            Question("What is the meaning of 'dog'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 3),
            Question("What is the meaning of 'cat'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 3),
            Question("What is the meaning of 'blue'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 2),
            Question("What is the meaning of 'green'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 2),
            Question("What is the meaning of 'yellow'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 2),
            Question("What is the meaning of 'orange'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'grape'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'pear'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'peach'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
            Question("What is the meaning of 'plum'?", listOf("A fruit", "A vegetable", "A color", "An animal"), 0),
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        loadQuestion()

        binding.btnSubmit.setOnClickListener {
            val selectedOptionId = binding.radioGroupAnswers.checkedRadioButtonId
            if (selectedOptionId != -1) {
                val selectedOption = view.findViewById<RadioButton>(selectedOptionId)
                checkAnswer(selectedOption.text.toString())
                moveToNextQuestion()
            } else {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadQuestion() {
        val currentQuestion = questions[currentQuestionIndex]
        binding.tvQuestion.text = currentQuestion.text
        binding.rbOption1.text = currentQuestion.options[0]
        binding.rbOption2.text = currentQuestion.options[1]
        binding.rbOption3.text = currentQuestion.options[2]
        binding.rbOption4.text = currentQuestion.options[3]
    }

    private fun checkAnswer(selectedOption: String) {
        val correctOption = questions[currentQuestionIndex].options[questions[currentQuestionIndex].correctAnswerIndex]
        if (selectedOption == correctOption) {
            correctAnswers++
            Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Wrong. The correct answer is $correctOption", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            loadQuestion()
        } else {
            Toast.makeText(requireContext(), "Quiz Completed! Correct answers: $correctAnswers/${questions.size}", Toast.LENGTH_LONG).show()
            // Quiz bitince sonuçları göstermek için bir method veya yönlendirme yapılabilir
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
)
