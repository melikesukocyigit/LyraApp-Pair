package com.turkcell.lyraapp.ui.auth.completeprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun CompleteProfileRoute(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompleteProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CompleteProfileEffect.NavigateToHome -> onNavigateToHome()
                is CompleteProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CompleteProfileScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
fun CompleteProfileScreen(
    state: CompleteProfileUiState,
    onIntent: (CompleteProfileIntent) -> Unit,
    onNavigateBack: () -> Unit,
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
        ) {
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = LyraIcons.ArrowBack,
                        contentDescription = "Geri",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Text(
                    text = "3 / 3",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Bilgilerini tamamla",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Hos geldin! Profilini olusturmak icin birkac bilgiye ihtiyacimiz var.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { onIntent(CompleteProfileIntent.FirstNameChanged(it)) },
                    label = { Text("Ad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { onIntent(CompleteProfileIntent.LastNameChanged(it)) },
                    label = { Text("Soyad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Dogum tarihi",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.day,
                    onValueChange = { onIntent(CompleteProfileIntent.DayChanged(it)) },
                    label = { Text("Gun") },
                    placeholder = { Text("GG") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.month,
                    onValueChange = { onIntent(CompleteProfileIntent.MonthChanged(it)) },
                    label = { Text("Ay") },
                    placeholder = { Text("AA") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.year,
                    onValueChange = { onIntent(CompleteProfileIntent.YearChanged(it)) },
                    label = { Text("Yil") },
                    placeholder = { Text("YYYY") },
                    modifier = Modifier.weight(1.6f),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (state.isSubmitEnabled && !state.isLoading)
                            Brush.horizontalGradient(listOf(Color(0xFFFFCCBC), Color(0xFFF48FB1)))
                        else
                            Brush.horizontalGradient(listOf(Color(0xFF4A4A4A), Color(0xFF3A3A3A)))
                    )
                    .then(
                        if (state.isSubmitEnabled && !state.isLoading)
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onIntent(CompleteProfileIntent.Submit) },
                            )
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tamamla",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = LyraIcons.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
