package com.example.self_money.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "operations")
data class Operation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "income" или "expense"
    val categoryId: Long,
    val accountId: Long,
    val date: Date,
    val comment: String = ""
)