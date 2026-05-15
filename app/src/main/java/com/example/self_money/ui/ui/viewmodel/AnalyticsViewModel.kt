package com.example.self_money.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.self_money.FinancialAdvisor
import com.example.self_money.repository.FinanceRepository
import kotlinx.coroutines.launch
import java.util.*

class AnalyticsViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _expensesByCategory = MutableLiveData<Map<String, Double>>()
    val expensesByCategory: LiveData<Map<String, Double>> = _expensesByCategory

    private val _advice = MutableLiveData<String>()
    val advice: LiveData<String> = _advice

    fun loadAnalytics(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            val operations = repository.getOperationsBetweenDates(startDate, endDate)

            val income = operations.filter { it.type == "income" }.sumOf { it.amount }
            val expense = operations.filter { it.type == "expense" }.sumOf { it.amount }

            _totalIncome.postValue(income)
            _totalExpense.postValue(expense)

            // Группировка расходов по категориям (по ID категории)
            val expenseByCatId = operations
                .filter { it.type == "expense" }
                .groupBy { it.categoryId }
                .mapValues { it.value.sumOf { op -> op.amount } }
                .mapKeys { it.key.toString() }

            _expensesByCategory.postValue(expenseByCatId)

            val categories = repository.getAllCategories()
            val categoryNameMap = categories.associate { it.id to it.name }

            val advisor = FinancialAdvisor()
            val adviceText = advisor.generateAdvice(income, expense, expenseByCatId, categoryNameMap)
            _advice.postValue(adviceText)
        }
    }
}