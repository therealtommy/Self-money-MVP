package com.example.self_money.data.dao


import androidx.room.*
import com.example.self_money.data.entity.Category
import com.example.self_money.data.entity.Account

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getAll(): List<Category>   // только для категорий

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getByType(type: String): List<Category>
}