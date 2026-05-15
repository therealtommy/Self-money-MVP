package com.example.self_money

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.self_money.databinding.ActivityOperationsListBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.OperationsListAdapter
import com.example.self_money.ui.ui.viewmodel.OperationsListViewModel
import com.example.self_money.ui.ui.viewmodel.OperationsListViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class OperationsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperationsListBinding
    private lateinit var viewModel: OperationsListViewModel
    private lateinit var adapter: OperationsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperationsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repository = FinanceRepository(applicationContext)
        val factory = OperationsListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OperationsListViewModel::class.java]

        setupRecyclerView()
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

    private fun setupRecyclerView() {
        adapter = OperationsListAdapter(
            onItemClick = { operation ->
                // Редактирование – можно открыть AddOperationActivity в режиме редактирования
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
            }
        )
        binding.rvOperations.layoutManager = LinearLayoutManager(this)
        binding.rvOperations.adapter = adapter
    }

    private fun setupFilters() {
        val periods = arrayOf("Последние 7 дней", "Последние 30 дней", "Весь период")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = adapter

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
        // Обновляем список, если вернулись с редактирования
        binding.btnApplyFilter.performClick()
    }
}