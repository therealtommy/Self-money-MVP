package com.example.self_money

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.self_money.databinding.ActivityMainBinding
import com.example.self_money.repository.FinanceRepository
import com.example.self_money.ui.OperationAdapter
import com.example.self_money.ui.ui.viewmodel.MainViewModel
import com.example.self_money.ui.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = FinanceRepository(applicationContext)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupRecyclerView()
        observeData()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddOperationActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        binding.rvRecentOperations.layoutManager = LinearLayoutManager(this)
    }

    private fun observeData() {
        viewModel.balance.observe(this) { balance ->
            binding.tvBalance.text = String.format("%.2f ₽", balance)
        }
        viewModel.recentOperations.observe(this) { operations ->
            if (operations.isNotEmpty()) {
                val adapter = OperationAdapter(operations)
                binding.rvRecentOperations.adapter = adapter
            }
        }
        viewModel.loadData()
    }
}