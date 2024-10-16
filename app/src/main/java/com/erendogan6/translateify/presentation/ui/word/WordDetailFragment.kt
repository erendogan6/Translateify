package com.erendogan6.translateify.presentation.ui.word

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentWordDetailBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentWordDetailBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        word = args.word

        word?.let { word ->
            observeViewModel()
            setupUI(word)
        }
    }

    private fun setupUI(word: Word) {
        binding.tvWordDetail.text = word.english
        binding.tvTranslationDetail.text = word.translation

        binding.btnToggleLearned.text =
            if (word.isLearned) getString(R.string.unlearn_word) else getString(R.string.learn_word)

        binding.btnToggleLearned.setOnClickListener {
            viewModel.toggleLearnedStatus(word)

            setFragmentResult(
                "learnedStatusChanged",
                Bundle().apply {
                    putParcelable("updatedWord", word)
                },
            )

            findNavController().popBackStack()
        }

        binding.btnFetchTranslation.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (!viewModel.fetchIsProcessing) {
                        binding.fetchAnimation.visibility = View.VISIBLE

                        viewModel.fetchTranslation(word.english)

                    } else {
                        Toast
                            .makeText(
                            requireContext(),
                            getString(R.string.ai_islemi_devam),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    binding.fetchAnimation.visibility = View.GONE
                    Timber.e(
                        getString(R.string.ai_islem_hatasi,
                            e.message))
                }
            }
        }

        binding.btnSpeechToText.setOnClickListener {
            lifecycleScope.launch {
                if (!viewModel.fetchIsProcessing) {
                    startSpeechToText()
                } else {
                    Toast
                        .makeText(
                            requireContext(),
                            getString(R.string.konusma_yapilamaz),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }

        binding.btnPronounce.setOnClickListener {
            pronounceWord(word)
        }
    }

    private fun observeViewModel() {
        viewModel.fetchWordImage(word!!.english)
        val markwon = Markwon.create(requireContext())

        lifecycleScope.launch {
            viewModel.renderedHtml.collect { markdownText ->
                markdownText?.let {
                    markwon.setMarkdown(
                        binding.tvTranslationDetail,
                        markdownText,
                    )
                    binding.fetchAnimation.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageUrl.collectLatest { imageUrl ->
                if (imageUrl != null) {
                    loadImageWithGlide(imageUrl)
                }
            }
        }
    }

    private fun loadImageWithGlide(imageUrl: String?) {
        try {
            binding.loadingAnimation.visibility = View.VISIBLE
            binding.imageView.visibility = View.INVISIBLE
            Glide
                .with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(
                    object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.loadingAnimation.visibility = View.GONE
                            binding.imageView.visibility = View.VISIBLE
                            Timber.d("Glide image loaded successfully for URL: $imageUrl")
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.loadingAnimation.visibility = View.GONE
                            binding.imageView.visibility = View.VISIBLE
                            Timber.e(e, "Glide image load failed for URL: $imageUrl")
                            return false
                        }
                    },
                )
                .error(R.drawable.logo)
                .into(binding.imageView)
        } catch (e: Exception) {
            binding.loadingAnimation.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE
            Timber.e(
                e,
                getString(R.string.failed_to_load_image),
            )
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
        _binding?.let { binding ->
            Glide.with(this).clear(binding.imageView)
        }
        _binding = null
    }
}
