package com.erendogan6.translateify.data.remote

import com.erendogan6.translateify.R
import com.erendogan6.translateify.utils.ResourcesProvider
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService
    @Inject
    constructor(
        private val resourcesProvider: ResourcesProvider,
    ) {
        private val systemInstruction: String
            get() = resourcesProvider.getString(R.string.translation_instruction)

        val model: GenerativeModel by lazy {
            GenerativeModel(
                "gemini-1.5-flash",
                apiKey = Firebase.remoteConfig.getString("GEMINI_API_KEY"),
                generationConfig =
                    generationConfig {
                        temperature =
                            2.0f
                        topK =
                            50
                        topP =
                            0.9f
                        maxOutputTokens =
                            8192
                        responseMimeType =
                            "text/plain"
                    },
                systemInstruction = content { text(systemInstruction) },
            )
        }

        suspend fun getTranslation(word: String): String =
            withContext(Dispatchers.IO) {
                try {
                    val chatHistory =
                        listOf(
                            content("user") {
                                text(
                                    """
                                    You are an English learning assistant designed to help a Turkish person learn English vocabulary. Please provide the following information for the English word "$word":
                                    
                                    1. **Translation:** Provide the most common Turkish translation of the word.
                                    2. **Pronunciation:** Describe how to pronounce the word in simple phonetic terms.
                                    3. **Example Sentences:** Give 1-2 example sentences using the word in context, along with their translations in Turkish.
                                    4. **Additional Tips:** Any additional notes about the word's usage, such as common phrases, synonyms, or grammatical notes.
                                    
                                    Please format your response in a structured and easy-to-read format.
                                    """.trimIndent(),
                                )
                            },
                        )
                    val chat = model.startChat(chatHistory)
                    val response = chat.sendMessage(systemInstruction)

                    response.candidates
                        .firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.asTextOrNull() ?: resourcesProvider.getString(R.string.general_error_message)
                } catch (e: SerializationException) {
                    resourcesProvider.getString(R.string.serialization_error_message)
                } catch (e: Exception) {
                    println(e.localizedMessage)
                    resourcesProvider.getString(R.string.error_fetching_translation)
                }
            }
    }
