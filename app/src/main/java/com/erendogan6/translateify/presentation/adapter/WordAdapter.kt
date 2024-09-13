package com.erendogan6.translateify.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.ItemWordBinding
import com.erendogan6.translateify.domain.model.Word

class WordAdapter(
    private val onItemClick: (Word) -> Unit,
    private val onLearnedClick: (Word) -> Unit,
) : ListAdapter<Word, WordAdapter.WordViewHolder>(WordDiffCallback()) {
    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WordViewHolder {
        val binding = ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WordViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class WordViewHolder(
        private val binding: ItemWordBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.tvWord.text = word.english
            binding.tvTranslation.text = word.translation

            // Öğrenme durumuna göre ikonun renklerini ayarlayın
            if (word.isLearned) {
                binding.ivCheckMark.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.colorGreen)),
                )
            } else {
                binding.ivCheckMark.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.colorError)),
                )
            }

            binding.buttonLearn.setOnClickListener { onItemClick(word) }
            binding.ivCheckMark.setOnClickListener {
                onLearnedClick(word)
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(
            oldItem: Word,
            newItem: Word,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Word,
            newItem: Word,
        ): Boolean = oldItem == newItem
    }
}
