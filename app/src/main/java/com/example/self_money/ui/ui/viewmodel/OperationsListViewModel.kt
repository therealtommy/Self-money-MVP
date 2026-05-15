package com.example.self_money.ui.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.self_money.data.entity.Operation
import com.example.self_money.repository.FinanceRepository
import kotlinx.coroutines.launch
import java.util.*

class OperationsListViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _operations = MutableLiveData<List<Operation>>()
    val operations: LiveData<List<Operation>> = _operations

    private var currentStartDate: Date? = null
    private var currentEndDate: Date? = null
    private var currentSearchQuery: String = ""

    fun loadAllOperations() {
        viewModelScope.launch {
            val allOps = repository.getOperationsBetweenDates(Date(0), Date())
            _operations.postValue(allOps)
        }
    }

    fun loadOperationsForPeriod(startDate: Date, endDate: Date) {
        currentStartDate = startDate
        currentEndDate = endDate
        viewModelScope.launch {
            val ops = repository.getOperationsBetweenDates(startDate, endDate)
            applySearchFilter(ops)
        }
    }

    fun setSearchQuery(query: String) {
        currentSearchQuery = query
        // Применяем поиск к уже загруженным операциям (или перезагружаем)
        if (currentStartDate != null && currentEndDate != null) {
            loadOperationsForPeriod(currentStartDate!!, currentEndDate!!)
        } else {
            loadAllOperations()
        }
    }

    private fun applySearchFilter(ops: List<Operation>) {
        val filtered = if (currentSearchQuery.isNotBlank()) {
            ops.filter { it.comment.contains(currentSearchQuery, ignoreCase = true) }
        } else {
            ops
        }
        _operations.postValue(filtered)
    }

    fun deleteOperation(operation: Operation) {
        viewModelScope.launch {
            repository.deleteOperation(operation)
            // После удаления перезагружаем текущий список
            if (currentStartDate != null && currentEndDate != null) {
                loadOperationsForPeriod(currentStartDate!!, currentEndDate!!)
            } else {
                loadAllOperations()
            }
        }
    }
}