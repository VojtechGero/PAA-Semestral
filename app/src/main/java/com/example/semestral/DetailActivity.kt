package com.example.semestral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.semestral.ui.DetailScreen
import com.example.semestral.ui.theme.SemestralTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemestralTheme { DetailScreen(onBack = { finish() }) }
        }
    }
}
