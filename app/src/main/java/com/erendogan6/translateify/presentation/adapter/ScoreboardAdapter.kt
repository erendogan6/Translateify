package com.erendogan6.translateify.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.ItemUserScoreBinding
import com.erendogan6.translateify.domain.model.UserScore

class ScoreboardAdapter : ListAdapter<UserScore, ScoreboardAdapter.ScoreViewHolder>(UserScoreDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScoreViewHolder {
        val binding = ItemUserScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ScoreViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class ScoreViewHolder(
        private val binding: ItemUserScoreBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userScore: UserScore) {
            val scoreText = binding.root.context.getString(R.string.user_score_display, userScore.score)
            binding.tvUserEmail.text = userScore.email
            binding.tvUserScore.text = scoreText
        }
    }
}

class UserScoreDiffCallback : DiffUtil.ItemCallback<UserScore>() {
    override fun areItemsTheSame(
        oldItem: UserScore,
        newItem: UserScore,
    ): Boolean = oldItem.userId == newItem.userId

    override fun areContentsTheSame(
        oldItem: UserScore,
        newItem: UserScore,
    ): Boolean = oldItem == newItem
}
