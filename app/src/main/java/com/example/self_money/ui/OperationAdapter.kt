package com.example.self_money.ui


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.self_money.data.entity.Operation
import com.example.self_money.databinding.ItemOperationBinding
import java.text.SimpleDateFormat
import java.util.*

class OperationAdapter(private val operations: List<Operation>) :
    RecyclerView.Adapter<OperationAdapter.OperationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding = ItemOperationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val operation = operations[position]
        holder.bind(operation)
    }

    override fun getItemCount() = operations.size

    inner class OperationViewHolder(private val binding: ItemOperationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(operation: Operation) {
            binding.tvCategory.text = "Категория ${operation.categoryId}" // упрощённо
            binding.tvAmount.text = "${if (operation.type == "expense") "-" else "+"} ${operation.amount} ₽"
            binding.tvAmount.setTextColor(
                if (operation.type == "expense")
                    binding.root.context.getColor(android.R.color.holo_red_dark)
                else
                    binding.root.context.getColor(android.R.color.holo_green_dark)
            )
            binding.tvComment.text = operation.comment.takeIf { it.isNotEmpty() } ?: "Без комментария"
            // Дату можно отобразить, но опустим для краткости
        }
    }
}