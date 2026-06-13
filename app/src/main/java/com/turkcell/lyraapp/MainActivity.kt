package com.turkcell.lyraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.lyraapp.data.auth.AuthRepository
import com.turkcell.lyraapp.data.theme.ThemeRepository // EKLENDİ
import com.turkcell.lyraapp.ui.navigation.LyraNavHost
import com.turkcell.lyraapp.ui.theme.LyraAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val isDarkMode by themeRepository.isDarkMode.collectAsStateWithLifecycle()

            LyraAppTheme(darkTheme = isDarkMode) {
                LyraNavHost(
                    isLoggedIn = authRepository.isUserLoggedIn(),
                    // Tema değişim tetiklemesini artık repository üzerinden yapıyoruz
                    onThemeToggle = {
                        themeRepository.toggleTheme()
                    }
                )
            }
        }
    }
}