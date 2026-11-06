package com.example.semestral

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.semestral.ui.DetailViewModel
import com.example.semestral.ui.WatchlistApp
import com.example.semestral.ui.theme.SemestralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SemestralTheme {
                WatchlistApp(onOpenShow = { id ->
                    startActivity(Intent(this, DetailActivity::class.java)
                        .putExtra(DetailViewModel.SHOW_ID, id))
                })
            }
        }
    }
}
