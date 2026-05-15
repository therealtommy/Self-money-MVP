package com.example.self_money

class FinancialAdvisor {

    fun generateAdvice(
        totalIncome: Double,
        totalExpense: Double,
        expensesByCategory: Map<String, Double>,
        categoryNames: Map<Long, String>
    ): String {
        val adviceList = mutableListOf<String>()

        val balance = totalIncome - totalExpense
        when {
            balance < 0 -> adviceList.add("⚠️ Вы тратите больше, чем зарабатываете! Попробуйте сократить расходы или увеличить доход.")
            balance < totalIncome * 0.1 -> adviceList.add("📉 Хорошо, но стоит откладывать хотя бы 10% от дохода. Сейчас вы экономите лишь ${String.format("%.0f", balance)} ₽.")
            else -> adviceList.add("✅ Отлично! Вы умеете копить. Продолжайте в том же духе.")
        }
        val categoryIdToName = categoryNames
        val foodCategoryId = categoryNames.entries.find { it.value.equals("Еда", ignoreCase = true) }?.key
        val cafeCategoryId = categoryNames.entries.find { it.value.equals("Кафе", ignoreCase = true) }?.key
        val transportCategoryId = categoryNames.entries.find { it.value.equals("Транспорт", ignoreCase = true) }?.key

        foodCategoryId?.let { id ->
            val foodExpense = expensesByCategory[id.toString()] ?: 0.0
            if (foodExpense > totalExpense * 0.3) {
                adviceList.add("🍽️ На еду уходит более 30% бюджета. Попробуйте планировать меню и готовить дома.")
            }
        }

        cafeCategoryId?.let { id ->
            val cafeExpense = expensesByCategory[id.toString()] ?: 0.0
            if (cafeExpense > totalExpense * 0.15) {
                adviceList.add("☕ Расходы на кафе и рестораны высоки. Возможно, стоит сократить число походов.")
            }
        }

        transportCategoryId?.let { id ->
            val transportExpense = expensesByCategory[id.toString()] ?: 0.0
            if (transportExpense > 3000) {
                adviceList.add("🚌 Траты на транспорт превышают 3000 ₽. Рассмотрите общественный транспорт или велосипед.")
            }
        }

        if (adviceList.size == 1 && adviceList[0].startsWith("✅")) {
            adviceList.add("🎯 Поставьте финансовую цель на следующий месяц!")
        }

        return adviceList.joinToString(separator = "\n\n")
    }
}