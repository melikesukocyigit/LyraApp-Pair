package com.turkcell.lyraapp.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.lyraapp.ui.icons.LyraIcons

@Composable
fun RegisterRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> onNavigateToLogin()
                is RegisterEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    RegisterScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            IconButton(onClick = onBackClick) {
                Icon(imageVector = LyraIcons.ArrowBack, contentDescription = "Geri")
            }

            Text(
                text = "Hesap oluştur",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            // Tek bir "Name" yerine ViewModel'a uyumlu Ad ve Soyad olarak ayırdık
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { onIntent(RegisterIntent.FirstNameChanged(it)) },
                label = { Text("Ad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = state.lastName,
                onValueChange = { onIntent(RegisterIntent.LastNameChanged(it)) },
                label = { Text("Soyad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))

            // Email alanı Contract'ta olmadığı için kaldırıldı.

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onIntent(RegisterIntent.PhoneNumberChanged(it)) },
                label = { Text("Telefon numarası") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
                label = { Text("Şifre") },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { onIntent(RegisterIntent.TogglePasswordVisibility) }) {
                        Icon(imageVector = LyraIcons.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                label = { Text("Şifre tekrar") },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))

            // Butonun aktif olması için kullanıcının sözleşmeyi kabul etmesi gerekiyordu (Contract'a göre).
            // UI'a sözleşme Checkbox'ı eklendi:
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isTermsAccepted,
                    onCheckedChange = { onIntent(RegisterIntent.TermsAcceptedChanged(it)) }
                )
                Text(
                    text = "Kullanıcı sözleşmesini kabul ediyorum",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onIntent(RegisterIntent.TermsAcceptedChanged(!state.isTermsAccepted)) }
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { onIntent(RegisterIntent.Submit) },
                enabled = state.isRegisterEnabled && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Kayıt ol")
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Zaten hesabın var mı? ")
                Text(
                    text = "Giriş yap",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onIntent(RegisterIntent.LoginClicked) }
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}