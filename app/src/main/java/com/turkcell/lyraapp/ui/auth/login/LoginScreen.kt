package com.turkcell.lyraapp.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.lyraapp.ui.icons.LyraIcons

@Composable
fun LoginRoute(
    onNavigateToOtp: (phoneNumber: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToOtp -> onNavigateToOtp(effect.phoneNumber)
                is LoginEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LoginScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(Modifier.weight(0.3f))

            BrandLogo()

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Tekrar hos geldin",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Telefon numaranu gir, sana dogrulama kodu gonderelim.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onIntent(LoginIntent.PhoneNumberChanged(it)) },
                label = { Text("Telefon numarasi") },
                placeholder = { Text("5XX XXX XX XX") },
                prefix = { Text("+90 ") },
                leadingIcon = { Icon(LyraIcons.Smartphone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onIntent(LoginIntent.Submit) },
                enabled = state.isLoginEnabled && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2A2A),
                    contentColor = Color.White,
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Devam et", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = LyraIcons.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.weight(0.7f))
        }
    }
}

@Composable
private fun BrandLogo() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFFFFCCBC), Color(0xFFF48FB1)))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = LyraIcons.Waveform,
            contentDescription = null,
            tint = Color(0xFF3E2723),
            modifier = Modifier.size(32.dp),
        )
    }
}
