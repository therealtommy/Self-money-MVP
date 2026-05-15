package com.example.self_money

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.self_money.databinding.ActivityAnalyticsBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.viewmodel.AnalyticsViewModel
import com.example.self_money.ui.viewmodel.AnalyticsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repository = FinanceRepository(applicationContext)
        val factory = AnalyticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        setupObservers()

        // Загружаем данные за текущий месяц
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time
        viewModel.loadAnalytics(startDate, endDate)
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            binding.tvTotalIncome.text = String.format("%.2f ₽", income)
        }
        viewModel.totalExpense.observe(this) { expense ->
            binding.tvTotalExpense.text = String.format("%.2f ₽", expense)
        }
        viewModel.advice.observe(this) { advice ->
            binding.tvAdvice.text = advice
        }
        viewModel.expensesByCategory.observe(this) { expensesMap ->
            displayCategories(expensesMap)
        }
    }

    private fun displayCategories(expensesMap: Map<String, Double>) {
        binding.llCategoriesList.removeAllViews()
        if (expensesMap.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Нет расходов за этот период"
            binding.llCategoriesList.addView(tv)
            return
        }
        // Сортируем по убыванию суммы
        val sorted = expensesMap.toList().sortedByDescending { (_, value) -> value }
        for ((categoryId, amount) in sorted) {
            val categoryName = getCategoryNameById(categoryId.toLong())
            val tv = TextView(this)
            tv.text = String.format("%s: %.2f ₽", categoryName, amount)
            tv.setPadding(0, 8, 0, 8)
            binding.llCategoriesList.addView(tv)
        }
    }

    private fun getCategoryNameById(categoryId: Long): String {
        // Можно сделать запрос к репозиторию, но для простоты пока вернём ID
        // Для правильной работы нужно передать мапу категорий из репозитория.
        // Это можно улучшить, добавив поле categoryMap в ViewModel.
        // Пока упростим: покажем ID, но затем вы можете доработать.
        return "Категория $categoryId"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}