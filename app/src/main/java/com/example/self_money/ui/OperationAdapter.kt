package com.example.self_money.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.self_money.data.entity.Operation
import com.example.self_money.databinding.ItemOperationBinding
import java.text.SimpleDateFormat
import java.util.*

class OperationAdapter(
    private val operations: List<Operation>,
    private val getCategoryName: (Long) -> String   // добавляем лямбду
) : RecyclerView.Adapter<OperationAdapter.OperationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding = ItemOperationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        holder.bind(operations[position])
    }

    override fun getItemCount() = operations.size

    inner class OperationViewHolder(private val binding: ItemOperationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(operation: Operation) {
            // Получаем имя категории через лямбду
            binding.tvCategory.text = getCategoryName(operation.categoryId)

            // Сумма и знак
            val sign = if (operation.type == "expense") "-" else "+"
            binding.tvAmount.text = "$sign ${operation.amount} ₽"
            binding.tvAmount.setTextColor(
                if (operation.type == "expense")
                    binding.root.context.getColor(android.R.color.holo_red_dark)
                else
                    binding.root.context.getColor(android.R.color.holo_green_dark)
            )

            // Комментарий
            binding.tvComment.text = operation.comment.takeIf { it.isNotEmpty() } ?: "Без комментария"
        }
    }
}