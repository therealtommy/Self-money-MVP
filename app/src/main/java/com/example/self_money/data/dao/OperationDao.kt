package com.example.self_money.data.dao

import androidx.room.*
import com.example.self_money.data.entity.Operation
import java.util.Date

@Dao
interface OperationDao {
    @Insert
    suspend fun insert(operation: Operation)

    @Update
    suspend fun update(operation: Operation)

    @Delete
    suspend fun delete(operation: Operation)

    @Query("SELECT * FROM operations WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getBetweenDates(start: Date, end: Date): List<Operation>

    @Query("SELECT SUM(amount) FROM operations WHERE type = :type AND date BETWEEN :start AND :end")
    suspend fun getSumByTypeBetweenDates(type: String, start: Date, end: Date): Double?

    @Query("SELECT * FROM operations ORDER BY date DESC LIMIT 5")
    suspend fun getLastFive(): List<Operation>
}