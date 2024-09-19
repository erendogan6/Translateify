package com.erendogan6.translateify.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.ItemCategoryBinding
import com.erendogan6.translateify.domain.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val maxSelection: Int,
    private val onSelectionChanged: (List<String>) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private val selectedCategories = mutableListOf<String>()

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryName.text = category.name
            binding.categoryIcon.setImageResource(category.iconResId)

            binding.root.setOnClickListener {
                toggleSelection(category)
            }

            binding.cardView.setCardBackgroundColor(
                if (selectedCategories.contains(category.name)) {
                    ContextCompat.getColor(binding.root.context, R.color.selected_card_color)
                } else {
                    ContextCompat.getColor(binding.root.context, R.color.white)
                },
            )
        }

        private fun toggleSelection(category: Category) {
            if (selectedCategories.contains(category.name)) {
                selectedCategories.remove(category.name)
            } else {
                if (selectedCategories.size < maxSelection) {
                    selectedCategories.add(category.name)
                } else {
                    Toast.makeText(binding.root.context, "Maximum $maxSelection categories can be selected.", Toast.LENGTH_SHORT).show()
                }
            }
            onSelectionChanged(selectedCategories)
            notifyItemChanged(adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int,
    ) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
