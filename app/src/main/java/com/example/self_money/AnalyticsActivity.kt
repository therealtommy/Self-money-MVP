package com.example.self_money

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.self_money.databinding.ActivityAnalyticsBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.viewmodel.AnalyticsViewModel
import com.example.self_money.ui.viewmodel.AnalyticsViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var repository: FinanceRepository
    private var categoryMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = FinanceRepository(applicationContext)
        val factory = AnalyticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        setupObservers()
        loadCategoryMap()

        // Загружаем данные за текущий месяц
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time
        viewModel.loadAnalytics(startDate, endDate)
    }

    private fun loadCategoryMap() {
        lifecycleScope.launch {
            val categories = repository.getAllCategories()
            categoryMap = categories.associate { it.id to it.name }
            // После загрузки мапы обновляем отображение категорий (если данные уже пришли)
            updateCategoriesDisplay()
        }
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            binding.tvTotalIncome.text = String.format("%.2f ₽", income)
        }
        viewModel.totalExpense.observe(this) { expense ->
            binding.tvTotalExpense.text = String.format("%.2f ₽", expense)
        }
        viewModel.expensesByCategory.observe(this) { expensesMap ->
            // Сохраняем данные, обновляем UI, если мапа уже загружена
            if (categoryMap.isNotEmpty()) {
                displayCategories(expensesMap)
            }
        }
        viewModel.advice.observe(this) { advice ->
            binding.tvAdvice.text = advice
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
        for ((categoryIdStr, amount) in sorted) {
            val categoryId = categoryIdStr.toLongOrNull() ?: continue
            val categoryName = categoryMap[categoryId] ?: "Категория $categoryId"
            val tv = TextView(this)
            tv.text = String.format("%s: %.2f ₽", categoryName, amount)
            tv.setPadding(0, 8, 0, 8)
            binding.llCategoriesList.addView(tv)
        }
    }

    private fun updateCategoriesDisplay() {
        val expensesMap = viewModel.expensesByCategory.value ?: emptyMap()
        if (expensesMap.isNotEmpty()) {
            displayCategories(expensesMap)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}