package com.example.self_money

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.self_money.data.entity.Operation
import com.example.self_money.databinding.ActivityOperationsListBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.OperationsListAdapter
import com.example.self_money.ui.ui.viewmodel.OperationsListViewModel
import com.example.self_money.ui.ui.viewmodel.OperationsListViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class OperationsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperationsListBinding
    private lateinit var viewModel: OperationsListViewModel
    private lateinit var adapter: OperationsListAdapter

    private var categoryMap: Map<Long, String> = emptyMap()
    private var accountMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repository = FinanceRepository(applicationContext)
        val factory = OperationsListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OperationsListViewModel::class.java]

        // Сначала загружаем мапы синхронно (дожидаемся)
        loadMapsBlocking()

        setupRecyclerView() // теперь мапы уже заполнены
        setupFilters()
        setupSearch()

        viewModel.operations.observe(this) { operations ->
            adapter.submitList(operations)
        }

        // По умолчанию загружаем операции за последние 30 дней
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val startDate = calendar.time
        viewModel.loadOperationsForPeriod(startDate, endDate)
    }

    // Блокирующая загрузка мап (но не в UI-потоке)
    private fun loadMapsBlocking() {
        lifecycleScope.launch {
            val (categories, accounts) = withContext(Dispatchers.IO) {
                val repository = FinanceRepository(applicationContext)
                Pair(repository.getAllCategories(), repository.getAllAccounts())
            }
            categoryMap = categories.associate { it.id to it.name }
            accountMap = accounts.associate { it.id to it.name }
            // После загрузки мап, если адаптер уже создан, обновляем его
            if (::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OperationsListAdapter(
            onItemClick = { operation ->
                val intent = Intent(this, AddOperationActivity::class.java)
                intent.putExtra("operation_id", operation.id)
                startActivity(intent)
            },
            onItemLongClick = { operation ->
                AlertDialog.Builder(this)
                    .setTitle("Удаление")
                    .setMessage("Удалить операцию?")
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.deleteOperation(operation)
                        Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            },
            getCategoryName = { categoryId -> categoryMap[categoryId] ?: "ID:$categoryId" },
            getAccountName = { accountId -> accountMap[accountId] ?: "ID:$accountId" }
        )
        binding.rvOperations.layoutManager = LinearLayoutManager(this)
        binding.rvOperations.adapter = adapter
    }

    private fun setupFilters() {
        val periods = arrayOf("Последние 7 дней", "Последние 30 дней", "Весь период")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = spinnerAdapter

        binding.btnApplyFilter.setOnClickListener {
            val position = binding.spinnerPeriod.selectedItemPosition
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            when (position) {
                0 -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    viewModel.loadOperationsForPeriod(calendar.time, endDate)
                }
                1 -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -30)
                    viewModel.loadOperationsForPeriod(calendar.time, endDate)
                }
                2 -> {
                    viewModel.loadAllOperations()
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Обновляем список при возврате
        binding.btnApplyFilter.performClick()
        // Перезагружаем мапы (на случай если добавили новую категорию)
        loadMapsBlocking()
    }
}