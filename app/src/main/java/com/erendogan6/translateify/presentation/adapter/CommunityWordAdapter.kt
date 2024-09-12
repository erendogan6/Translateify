package com.erendogan6.translateify.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erendogan6.translateify.databinding.ItemCommunityWordBinding
import com.erendogan6.translateify.domain.model.CommunityWord

class CommunityWordAdapter : ListAdapter<CommunityWord, CommunityWordAdapter.CommunityWordViewHolder>(CommunityWordDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommunityWordViewHolder {
        val binding = ItemCommunityWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityWordViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CommunityWordViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class CommunityWordViewHolder(
        private val binding: ItemCommunityWordBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: CommunityWord) {
            binding.tvWord.text = word.word
            binding.tvUsername.text = word.username
        }
    }
}

class CommunityWordDiffCallback : DiffUtil.ItemCallback<CommunityWord>() {
    override fun areItemsTheSame(
        oldItem: CommunityWord,
        newItem: CommunityWord,
    ): Boolean = oldItem.word == newItem.word

    override fun areContentsTheSame(
        oldItem: CommunityWord,
        newItem: CommunityWord,
    ): Boolean = oldItem == newItem
}
