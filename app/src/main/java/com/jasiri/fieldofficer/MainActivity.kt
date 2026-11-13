package com.jasiri.fieldofficer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jasiri.fieldofficer.core.designsystem.FieldOfficerTheme
import com.jasiri.fieldofficer.navigation.FieldOfficerNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FieldOfficerTheme {
                FieldOfficerNavGraph()
            }
        }
    }
}
