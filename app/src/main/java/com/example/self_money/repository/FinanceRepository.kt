package com.example.self_money.repository


import android.content.Context
import com.example.self_money.data.AppDatabase
import com.example.self_money.data.entity.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FinanceRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val operationDao = db.operationDao()
    private val accountDao = db.accountDao()
    private val categoryDao = db.categoryDao()

    suspend fun insertOperation(operation: Operation) = operationDao.insert(operation)

    suspend fun updateOperation(operation: Operation) = operationDao.update(operation)

    suspend fun deleteOperation(operation: Operation) = operationDao.delete(operation)

    suspend fun getOperationsBetweenDates(start: Date, end: Date): List<Operation> =
        operationDao.getBetweenDates(start, end)

    suspend fun getLastFiveOperations(): List<Operation> = operationDao.getLastFive()

    suspend fun getTotalBalance(): Double {
        val accounts = accountDao.getAll()
        var balance = accounts.sumOf { it.initialBalance }
        val operations = operationDao.getBetweenDates(Date(0), Date())
        balance += operations.filter { it.type == "income" }.sumOf { it.amount }
        balance -= operations.filter { it.type == "expense" }.sumOf { it.amount }
        return balance
    }

    fun getTotalBalanceFlow(): Flow<Double> = flow {
        while (true) {
            emit(getTotalBalance())
            kotlinx.coroutines.delay(1000) // обновление раз в секунду (можно убрать для MVP)
        }
    }
}