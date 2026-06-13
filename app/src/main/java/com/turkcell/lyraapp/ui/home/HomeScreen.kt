package com.turkcell.lyraapp.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.lyraapp.data.home.PlaylistForYou
import com.turkcell.lyraapp.data.home.QuickPick
import com.turkcell.lyraapp.data.home.RecentlyPlayed
import com.turkcell.lyraapp.ui.icons.LyraIcons

@Composable
fun HomeRoute(
    onNavigateToLogin: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToLogin -> onNavigateToLogin()
                is HomeEffect.ShowError -> {
                }
            }
        }
    }


    HomeScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (state.isDarkMode) Color(0xFF121212) else Color(0xFFF8F8F8)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.height(24.dp))
                    HomeHeader(
                        greeting = state.greeting,
                        userInitials = state.userInitials,
                        isDarkMode = state.isDarkMode,
                        onLogout = { onIntent(HomeIntent.Logout) },
                        onToggleTheme = { onIntent(HomeIntent.ToggleTheme) }
                    )

                    QuickPicksGrid(items = state.quickPicks, isDarkMode = state.isDarkMode)

                    SectionHeader(title = "Son çalınanlar", isDarkMode = state.isDarkMode)
                    RecentlyPlayedList(
                        items = state.recentlyPlayed,
                        isDarkMode = state.isDarkMode
                    )

                    SectionHeader(title = "Senin için çalma listeleri", isDarkMode = state.isDarkMode)
                    PlaylistsList(items = state.playlistsForYou, isDarkMode = state.isDarkMode)

                    Spacer(Modifier.height(100.dp))
                }

                MiniPlayer(
                    title = "Neon Sokaklar",
                    artist = "Şehir Işıkları",
                    isDarkMode = state.isDarkMode,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    userInitials: String,
    isDarkMode: Boolean,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = greeting,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode) LyraIcons.LightMode else LyraIcons.DarkMode,
                        contentDescription = "Tema Değiştir",
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF48FB1))
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInitials,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Text(
            text = "Ne dinlemek istersin?",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDarkMode) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QuickPicksGrid(items: List<QuickPick>, isDarkMode: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in items.indices step 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickPickCard(item = items[i], isDarkMode = isDarkMode, modifier = Modifier.weight(1f))
                if (i + 1 < items.size) {
                    QuickPickCard(item = items[i + 1], isDarkMode = isDarkMode, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickPickCard(item: QuickPick, isDarkMode: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DynamicMusicCardBackground(
            title = item.title,
            startColor = Color(item.artworkStartColor),
            endColor = Color(item.artworkEndColor),
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = item.title,
            color = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp),
            maxLines = 1,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionHeader(title: String, isDarkMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = if (isDarkMode) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tümü",
            color = Color(0xFFF48FB1),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RecentlyPlayedList(
    items: List<RecentlyPlayed>,
    isDarkMode: Boolean
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            Column(modifier = Modifier.width(160.dp)) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    DynamicMusicCardBackground(
                        title = item.title,
                        startColor = Color(item.artworkStartColor),
                        endColor = Color(item.artworkEndColor),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.title,
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = item.subtitle,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PlaylistsList(items: List<PlaylistForYou>, isDarkMode: Boolean) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            Column(modifier = Modifier.width(160.dp)) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    DynamicMusicCardBackground(
                        title = item.title,
                        startColor = Color(item.artworkStartColor),
                        endColor = Color(item.artworkEndColor),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.title,
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DynamicMusicCardBackground(
    title: String,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Brush.verticalGradient(listOf(startColor, endColor))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.dp.toPx()

            // Şarkı ismine göre sahte desenler
            when {
                title.contains("Gece") || title.contains("Neon") || title.contains("Derin") || title.contains("Yıldız") -> {
                    for (i in 1..6) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f * i),
                            radius = (size.minDimension / 5) * i,
                            center = Offset(size.width * 0.2f, size.height * 0.3f),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
                title.contains("Sabah") || title.contains("Odaklan") || title.contains("Sakin") -> {
                    val path = Path().apply {
                        moveTo(size.width * 0.2f, size.height * 0.2f)
                        lineTo(size.width * 0.5f, size.height * 0.1f)
                        lineTo(size.width * 0.4f, size.height * 0.4f)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.1f),
                        style = Stroke(width = strokeWidth)
                    )
                }
                title.contains("Yaz") || title.contains("Enerji") -> {
                    for (x in 0..5) {
                        for (y in 0..5) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = 2.dp.toPx(),
                                center = Offset(size.width * 0.15f * x, size.height * 0.15f * y)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniPlayer(title: String, artist: String, isDarkMode: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2A2121) else Color(0xFFEEEEEE)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFFD98E4A), Color(0xFF8A5526)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LyraIcons.Waveform4,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = artist,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = LyraIcons.Favorite,
                contentDescription = null,
                tint = Color(0xFFF48FB1),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector = LyraIcons.Pause,
                contentDescription = null,
                tint = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector = LyraIcons.SkipNext,
                contentDescription = null,
                tint = if (isDarkMode) Color.White else Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}