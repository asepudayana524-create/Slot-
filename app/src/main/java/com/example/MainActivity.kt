package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SimDatabase
import com.example.data.SimRepository
import com.example.data.SimViewModel
import com.example.data.SimViewModelFactory
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val context = LocalContext.current
        val database = SimDatabase.getDatabase(context.applicationContext)
        val repository = SimRepository(database.simDao())
        val viewModel: SimViewModel = viewModel(
          factory = SimViewModelFactory(
            application = context.applicationContext as Application,
            repository = repository
          )
        )
        MainAppScreen(viewModel = viewModel)
      }
    }
  }
}

