package com.example.twinzy_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.twinzy_app.navigation.TwinzyNavGraph
import com.example.twinzy_app.ui.theme.DeepVoid
import com.example.twinzy_app.ui.theme.TwinzyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            TwinzyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepVoid
                ) {
                    TwinzyNavGraph()
                }
            }
        }
    }
}