package com.example.self_money.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.self_money.data.entity.Operation
import com.example.self_money.databinding.ItemOperationFullBinding
import java.text.SimpleDateFormat
import java.util.*

class OperationsListAdapter(
    private val onItemClick: (Operation) -> Unit,
    private val onItemLongClick: (Operation) -> Unit,
    private val getCategoryName: (Long) -> String,
    private val getAccountName: (Long) -> String
) : ListAdapter<Operation, OperationsListAdapter.OperationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding = ItemOperationFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OperationViewHolder(private val binding: ItemOperationFullBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(operation: Operation) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(operation.date)

            val sign = if (operation.type == "expense") "-" else "+"
            binding.tvAmount.text = "$sign ${operation.amount} ₽"
            binding.tvCategory.text = getCategoryName(operation.categoryId)
            binding.tvAccount.text = getAccountName(operation.accountId)
            binding.tvAmount.setTextColor(
                if (operation.type == "expense")
                    binding.root.context.getColor(android.R.color.holo_red_dark)
                else
                    binding.root.context.getColor(android.R.color.holo_green_dark)
            )

            binding.tvComment.text = operation.comment.takeIf { it.isNotEmpty() } ?: "Без комментария"

            binding.root.setOnClickListener {
                onItemClick(operation)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(operation)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Operation>() {
        override fun areItemsTheSame(oldItem: Operation, newItem: Operation): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Operation, newItem: Operation): Boolean =
            oldItem == newItem
    }
}