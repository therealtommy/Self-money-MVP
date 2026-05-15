package com.example.self_money

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.self_money.databinding.ActivityMainBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.OperationAdapter
import com.example.self_money.ui.ui.viewmodel.MainViewModel
import com.example.self_money.ui.ui.viewmodel.MainViewModelFactory
import com.example.self_money.data.entity.Operation
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: FinanceRepository   // поле класса
    private var categoryMap: Map<Long, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ИНИЦИАЛИЗИРУЕМ поле repository
        repository = FinanceRepository(applicationContext)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupRecyclerView()
        observeData()
        loadCategoryMap()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddOperationActivity::class.java))
        }
        binding.btnViewAll.setOnClickListener {
            startActivity(Intent(this, OperationsListActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        binding.rvRecentOperations.layoutManager = LinearLayoutManager(this)
    }

    private fun loadCategoryMap() {
        lifecycleScope.launch {
            val categories = repository.getAllCategories()
            categoryMap = categories.associate { it.id to it.name }
            // После загрузки обновляем адаптер, если операции уже есть
            viewModel.recentOperations.value?.let { operations ->
                updateRecentOperations(operations)
            }
        }
    }

    private fun updateRecentOperations(operations: List<Operation>) {
        val adapter = OperationAdapter(operations) { categoryId ->
            categoryMap[categoryId] ?: "Категория $categoryId"
        }
        binding.rvRecentOperations.adapter = adapter
    }

    private fun observeData() {
        viewModel.balance.observe(this) { balance ->
            binding.tvBalance.text = String.format("%.2f ₽", balance)
        }
        viewModel.recentOperations.observe(this) { operations ->
            if (operations.isNotEmpty()) {
                updateRecentOperations(operations)
            }
        }
        viewModel.loadData()
    }
}