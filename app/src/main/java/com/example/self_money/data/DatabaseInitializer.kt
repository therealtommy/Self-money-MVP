package com.example.self_money.data


import android.content.Context
import com.example.self_money.data.entity.Account
import com.example.self_money.data.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {
    suspend fun initialize(context: Context) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            // Проверяем, есть ли уже категории
            val categories = db.categoryDao().getAll()
            if (categories.isEmpty()) {
                // Добавляем стандартные категории расходов
                val expenseCategories = listOf(
                    Category(name = "Еда", type = "expense"),
                    Category(name = "Транспорт", type = "expense"),
                    Category(name = "Кафе", type = "expense"),
                    Category(name = "Развлечения", type = "expense"),
                    Category(name = "Покупки", type = "expense"),
                    Category(name = "Здоровье", type = "expense"),
                    Category(name = "Связь", type = "expense"),
                    Category(name = "Квартплата", type = "expense")
                )
                expenseCategories.forEach { db.categoryDao().insert(it) }

                // Добавляем стандартные категории доходов
                val incomeCategories = listOf(
                    Category(name = "Зарплата", type = "income"),
                    Category(name = "Фриланс", type = "income"),
                    Category(name = "Подарки", type = "income"),
                    Category(name = "Проценты", type = "income")
                )
                incomeCategories.forEach { db.categoryDao().insert(it) }
            }

            // Проверяем, есть ли счета
            val accounts = db.accountDao().getAll()
            if (accounts.isEmpty()) {
                val defaultAccount = Account(name = "Наличные", initialBalance = 0.0)
                db.accountDao().insert(defaultAccount)
            }
        }
    }
}