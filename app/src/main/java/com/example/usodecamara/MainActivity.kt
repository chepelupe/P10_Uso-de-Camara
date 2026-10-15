package com.example.usodecamara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.usodecamara.ui.CameraScreen
import com.example.usodecamara.ui.theme.UsoDeCamaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UsoDeCamaraTheme {
                CameraScreen()
            }
        }
    }
}