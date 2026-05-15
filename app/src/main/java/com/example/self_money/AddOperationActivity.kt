package com.example.self_money


import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.self_money.data.AppDatabase
import com.example.self_money.data.entity.Account
import com.example.self_money.data.entity.Category
import com.example.self_money.data.entity.Operation
import com.example.self_money.databinding.ActivityAddOperationBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddOperationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOperationBinding
    private lateinit var db: AppDatabase

    private var selectedCategoryId: Long = -1
    private var selectedAccountId: Long = -1
    private var selectedDate: Date = Date()

    // Списки для спиннеров
    private lateinit var categories: List<Category>
    private lateinit var accounts: List<Account>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = AppDatabase.getInstance(this)

        // Загружаем данные для спиннеров
        loadSpinnerData()

        // Настройка выбора даты
        setupDatePicker()

        // Кнопка сохранения
        binding.btnSave.setOnClickListener {
            saveOperation()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun loadSpinnerData() {
        lifecycleScope.launch {
            // Загружаем категории и счета из базы (в фоне)
            categories = db.categoryDao().getAll()
            accounts = db.accountDao().getAll()

            // Настраиваем адаптеры для AutoCompleteTextView
            val categoryNames = categories.map { it.name }
            val adapterCategory = ArrayAdapter(this@AddOperationActivity, android.R.layout.simple_dropdown_item_1line, categoryNames)
            binding.actvCategory.setAdapter(adapterCategory)

            val accountNames = accounts.map { it.name }
            val adapterAccount = ArrayAdapter(this@AddOperationActivity, android.R.layout.simple_dropdown_item_1line, accountNames)
            binding.actvAccount.setAdapter(adapterAccount)

            // По умолчанию выбираем первый элемент (если есть)
            if (categories.isNotEmpty()) {
                selectedCategoryId = categories[0].id
                binding.actvCategory.setText(categories[0].name, false)
            }
            if (accounts.isNotEmpty()) {
                selectedAccountId = accounts[0].id
                binding.actvAccount.setText(accounts[0].name, false)
            }

            // Слушатели выбора
            binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].id
            }
            binding.actvAccount.setOnItemClickListener { _, _, position, _ ->
                selectedAccountId = accounts[position].id
            }
        }
    }

    private fun setupDatePicker() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        binding.etDate.setText(dateFormat.format(selectedDate))

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = GregorianCalendar(year, month, dayOfMonth).time
                    binding.etDate.setText(dateFormat.format(selectedDate))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun saveOperation() {
        // Получаем сумму
        val amountStr = binding.etAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            binding.etAmount.error = "Введите сумму"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Сумма должна быть положительным числом"
            return
        }

        // Определяем тип (расход/доход)
        val type = if (binding.rbExpense.isChecked) "expense" else "income"

        // Проверяем, что выбранные категория и счёт существуют
        if (selectedCategoryId == -1L) {
            Toast.makeText(this, "Выберите категорию", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedAccountId == -1L) {
            Toast.makeText(this, "Выберите счёт", Toast.LENGTH_SHORT).show()
            return
        }

        val comment = binding.etComment.text.toString()

        val operation = Operation(
            amount = amount,
            type = type,
            categoryId = selectedCategoryId,
            accountId = selectedAccountId,
            date = selectedDate,
            comment = comment
        )

        lifecycleScope.launch {
            db.operationDao().insert(operation)
            runOnUiThread {
                Toast.makeText(this@AddOperationActivity, "Операция сохранена", Toast.LENGTH_SHORT).show()
                finish() // закрываем экран и возвращаемся на главный
            }
        }
    }
}