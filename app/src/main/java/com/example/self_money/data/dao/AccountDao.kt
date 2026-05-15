package com.example.self_money.data.dao


import androidx.room.*
import com.example.self_money.data.entity.Account

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT * FROM accounts ORDER BY name")
    suspend fun getAll(): List<Account>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): Account?
}