package com.erendogan6.translateify.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentWordDetailBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class WordDetailFragment :
    Fragment(R.layout.fragment_word_detail),
    TextToSpeech.OnInitListener {
    private var _binding: FragmentWordDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RandomWordsViewModel by viewModels()
    private var word: Word? = null
    private var tts: TextToSpeech? = null
    private val args: WordDetailFragmentArgs by navArgs()
    private val speechRecognizer: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(requireContext()) }

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                matches?.let {
                    val spokenWord = it[0]
                    binding.tvWordDetail.text = spokenWord
                    Toast.makeText(requireContext(), getString(R.string.detected_word, spokenWord), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.speech_not_recognized), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        word = args.word

        word?.let { word ->
            binding.tvWordDetail.text = word.english
            binding.tvTranslationDetail.text = word.translation

            binding.btnToggleLearned.text =
                if (word.isLearned) getString(R.string.unlearn_word) else getString(R.string.learn_word)

            binding.btnFetchTranslation.setOnClickListener {
                viewModel.fetchTranslation(word.english)
            }

            lifecycleScope.launch {
                viewModel.translation.collect { translation ->
                    translation?.let {
                        val markdownText = it
                        val parser = Parser.builder().build()
                        val document = parser.parse(markdownText)
                        val renderer = HtmlRenderer.builder().build()
                        val html = renderer.render(document)

                        binding.tvTranslationDetail.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
                    }
                }
            }

            binding.btnToggleLearned.setOnClickListener {
                viewModel.toggleLearnedStatus(word)
                findNavController().popBackStack()
            }

            binding.btnPronounce.setOnClickListener {
                pronounceWord(word)
            }
            binding.btnSpeechToText.setOnClickListener {
                startSpeechToText()
            }
        }
    }

    private fun pronounceWord(word: Word) {
        tts?.speak(word.english, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    private fun startSpeechToText() {
        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_prompt))
            }
        speechRecognizerLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
        speechRecognizer.destroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
