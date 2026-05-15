package com.example.self_money


import android.app.Application
import com.example.self_money.data.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelfMoneyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInitializer.initialize(this@SelfMoneyApp)
        }
    }
}