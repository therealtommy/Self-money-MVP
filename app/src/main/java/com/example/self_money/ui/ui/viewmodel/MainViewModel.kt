package com.example.self_money.ui.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.self_money.data.entity.Operation
import com.example.self_money.repository.FinanceRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    private val _recentOperations = MutableLiveData<List<Operation>>()
    val recentOperations: LiveData<List<Operation>> = _recentOperations

    fun loadData() {
        viewModelScope.launch {
            _balance.value = repository.getTotalBalance()
            _recentOperations.value = repository.getLastFiveOperations()
        }
    }
}