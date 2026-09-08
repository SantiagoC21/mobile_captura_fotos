package com.facialdataset.frontend
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.facialdataset.frontend.ui.navigation.AppNavigation
import com.tuapp.facial.ui.theme.FacialDatasetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FacialDatasetTheme {
                AppNavigation()
            }
        }
    }
}