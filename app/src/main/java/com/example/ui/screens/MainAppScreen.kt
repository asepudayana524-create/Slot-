package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SimTransaction
import com.example.data.SimViewModel
import com.example.data.SlotSymbol
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: SimViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf("SLOT") } // "SLOT", "WITHDRAW", "HISTORY", "INFO"

    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()
    val winAmount by viewModel.winAmount.collectAsStateWithLifecycle()
    val activeMultiplier by viewModel.activeMultiplier.collectAsStateWithLifecycle()
    val zeusAction by viewModel.zeusAction.collectAsStateWithLifecycle()
    val winNotification by viewModel.winNotification.collectAsStateWithLifecycle()

    val freeSpinsRemaining by viewModel.freeSpinsRemaining.collectAsStateWithLifecycle()
    val isFreeSpinsMode by viewModel.isFreeSpinsMode.collectAsStateWithLifecycle()
    val freeSpinsTotalWin by viewModel.freeSpinsTotalWin.collectAsStateWithLifecycle()
    val freeSpinsAccumMultiplier by viewModel.freeSpinsAccumMultiplier.collectAsStateWithLifecycle()

    val betAmount by viewModel.betAmount.collectAsStateWithLifecycle()
    val doubleChance by viewModel.doubleChance.collectAsStateWithLifecycle()
    val autoSpinsRemaining by viewModel.autoSpinsRemaining.collectAsStateWithLifecycle()

    val showReceipt by viewModel.showReceipt.collectAsStateWithLifecycle()
    val withdrawStatusMessage by viewModel.withdrawStatusMessage.collectAsStateWithLifecycle()

    // Background Gradient for immersive Olympus/Pragmatic feel (Dark Purple/Blue/Gold)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D1B),
            Color(0xFF140F2D),
            Color(0xFF1F103A),
            Color(0xFF0D0D1B)
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F0E20),
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = currentTab == "SLOT",
                    onClick = { currentTab = "SLOT" },
                    icon = { Icon(Icons.Filled.Casino, "Play Slot") },
                    label = { Text("Slot Play") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFFD700),
                        unselectedIconColor = Color.LightGray,
                        selectedTextColor = Color(0xFFFFD700),
                        indicatorColor = Color(0x23FFD700)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "WITHDRAW",
                    onClick = { currentTab = "WITHDRAW" },
                    icon = { Icon(Icons.Filled.AccountBalanceWallet, "Simulasi Withdraw") },
                    label = { Text("Simulasi WD") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        unselectedIconColor = Color.LightGray,
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0x234CAF50)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "HISTORY",
                    onClick = { currentTab = "HISTORY" },
                    icon = { Icon(Icons.Filled.History, "Struk & Riwayat") },
                    label = { Text("Struk") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF29B6F6),
                        unselectedIconColor = Color.LightGray,
                        selectedTextColor = Color(0xFF29B6F6),
                        indicatorColor = Color(0x2329B6F6)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "INFO",
                    onClick = { currentTab = "INFO" },
                    icon = { Icon(Icons.Filled.Info, "Informasi") },
                    label = { Text("Edukasi") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF7043),
                        unselectedIconColor = Color.LightGray,
                        selectedTextColor = Color(0xFFFF7043),
                        indicatorColor = Color(0x23FF7043)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            // Header Balance Bar
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top Custom Header Info
                Surface(
                    color = Color(0xAA0A0915),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KEMENANGAN SIMULASI (Virtual)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    text = viewModel.formatRupiah(balance),
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        // Refill Button if balance running low
                        Button(
                            onClick = { viewModel.refillBalance() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Isi Ulang Saldo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Isi Ulang", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Main screen tab contents
                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        "SLOT" -> SlotGameTab(
                            viewModel = viewModel,
                            reels = reels,
                            isSpinning = isSpinning,
                            winAmount = winAmount,
                            activeMultiplier = activeMultiplier,
                            zeusAction = zeusAction,
                            winNotification = winNotification,
                            freeSpinsRemaining = freeSpinsRemaining,
                            isFreeSpinsMode = isFreeSpinsMode,
                            freeSpinsTotalWin = freeSpinsTotalWin,
                            freeSpinsAccumMultiplier = freeSpinsAccumMultiplier,
                            betAmount = betAmount,
                            doubleChance = doubleChance,
                            autoSpinsRemaining = autoSpinsRemaining
                        )
                        "WITHDRAW" -> WithdrawTab(viewModel = viewModel, balance = balance)
                        "HISTORY" -> HistoryTab(viewModel = viewModel, transactions = transactions)
                        "INFO" -> InfoTab()
                    }
                }
            }

            // Receipt dialog popup when a success transfer occurs
            if (showReceipt != null) {
                Dialog(onDismissRequest = { viewModel.clearReceipt() }) {
                    ReceiptModal(
                        transaction = showReceipt!!,
                        onDismiss = { viewModel.clearReceipt() }
                    )
                }
            }
        }
    }
}

// ==========================================
// SLOT GAME TAB COMPOSE SCREEN
// ==========================================
@Composable
fun SlotGameTab(
    viewModel: SimViewModel,
    reels: List<SlotSymbol>,
    isSpinning: Boolean,
    winAmount: Long,
    activeMultiplier: Int?,
    zeusAction: String?,
    winNotification: String?,
    freeSpinsRemaining: Int,
    isFreeSpinsMode: Boolean,
    freeSpinsTotalWin: Long,
    freeSpinsAccumMultiplier: Int,
    betAmount: Long,
    doubleChance: Boolean,
    autoSpinsRemaining: Int
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Free Spins Banner
        if (isFreeSpinsMode) {
            Surface(
                color = Color(0xAAFF1744),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚡ MODE FREESPIN AKTIF ⚡",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sisa Putaran: $freeSpinsRemaining",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Total Menang: Rp ${viewModel.formatRupiah(freeSpinsTotalWin)}",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    if (freeSpinsAccumMultiplier > 0) {
                        Text(
                            text = "Pengali Akumulasi: 🌟 x$freeSpinsAccumMultiplier 🌟",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Zeus Interactive Zone & Slot Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Light Rays behind Zeus
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFD700).copy(alpha = 0.15f), Color.Transparent)
                            ),
                            radius = size.width / 2.5f
                        )
                    }
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Zeus Floating on the Left Or Center depending
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ZeusCharacter(zeusAction = zeusAction, multiplier = activeMultiplier)
                }

                // Game Logo/Slogan info
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "GATES",
                        color = Color(0xFFFFD700),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "OF OLYMPUS",
                        color = Color(0xFF29B6F6),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                    Surface(
                        color = Color(0xFFD500F9),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "SIMULASI PRAGMATIC",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Win Announcements overlaid on Screen
        AnimatedVisibility(
            visible = winNotification != null,
            enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            if (winNotification != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151035)),
                    border = BorderStroke(2.dp, Color(0xFFFFD700)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = winNotification,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                        if (winAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${viewModel.formatRupiah(winAmount)}",
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }

        // THE SLOT REELS BOARD (3x3 Grid)
        Surface(
            color = Color(0xFF0F0E20),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(3.dp, Color(0xFFFFD700)),
            modifier = Modifier
                .widthIn(max = 340.dp)
                .aspectRatio(1.15f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Background grid decorative slots
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) { row ->
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(3) { col ->
                                val index = row * 3 + col
                                val symbol = reels[index]

                                SlotCell(symbol = symbol, isSpinning = isSpinning)
                            }
                        }
                    }
                }
            }
        }

        // BET ADJUST PANEL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bet size adjuster
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.setBet(betAmount - 5000L) },
                    enabled = !isSpinning && !isFreeSpinsMode,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF2C244C),
                        disabledContainerColor = Color(0xFF181525)
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Remove, "Kurangi Taruhan", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TARUHAN (Bet)", color = Color.Gray, fontSize = 9.sp)
                    Text(
                        "Rp ${viewModel.formatRupiah(betAmount)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.setBet(betAmount + 5000L) },
                    enabled = !isSpinning && !isFreeSpinsMode,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF2C244C),
                        disabledContainerColor = Color(0xFF181525)
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, "Tambah Taruhan", tint = Color.White)
                }
            }

            // DOUBLE CHANCE (DC) SWITCH
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (doubleChance) Color(0x3D00E676) else Color(0x1F2C244C))
                    .clickable(enabled = !isSpinning && !isFreeSpinsMode) { viewModel.toggleDoubleChance() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column {
                    Text("Ganda Peluang", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (doubleChance) "AKTIF (1.25x)" else "NONAKTIF",
                        color = if (doubleChance) Color(0xFF00E676) else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Switch(
                    checked = doubleChance,
                    onCheckedChange = { viewModel.toggleDoubleChance() },
                    enabled = !isSpinning && !isFreeSpinsMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E676),
                        checkedTrackColor = Color(0x9E00E676),
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color(0xFF1E1B31)
                    ),
                    modifier = Modifier.scale(0.7f)
                )
            }
        }

        // SPIN CONTROL BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // BUY FREE SPINS BUTTON
            Button(
                onClick = { viewModel.buyFreeSpins() },
                enabled = !isSpinning && !isFreeSpinsMode,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9100),
                    disabledContainerColor = Color(0xFF251E14)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BELI FREESPIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Rp ${viewModel.formatRupiah(viewModel.getBuyFreeSpinsCost())}", fontSize = 10.sp, color = Color.Black)
                }
            }

            // MAIN PLAY / SPIN BUTTON
            Button(
                onClick = { viewModel.spin() },
                enabled = !isSpinning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    disabledContainerColor = Color(0xAAFFD700)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("submit_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isSpinning) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Casino, contentDescription = "Spin Reels", tint = Color.Black)
                    }
                    Text(
                        text = if (isSpinning) "MENGOCOK..." else "PUTAR SEKARANG",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // AUTO PUTAT (AUTO SPIN) OPTION TRAY
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F0E20))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUTO PUTAR (Otomatis)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                if (autoSpinsRemaining > 0) {
                    Badge(
                        containerColor = Color(0xFFD81B60),
                        contentColor = Color.White
                    ) {
                        Text("Sisa: $autoSpinsRemaining", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                    }
                    Text(
                        "Berhenti",
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.stopAutoSpins() }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(10, 25, 50, 100).forEach { total ->
                    Button(
                        onClick = { viewModel.startAutoSpins(total) },
                        enabled = !isSpinning && !isFreeSpinsMode,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231B42)),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    ) {
                        Text("${total}x", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SLOT CELL COMPONENT (Renders Individual Reel Symbol)
// ==========================================
@Composable
fun RowScope.SlotCell(symbol: SlotSymbol, isSpinning: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isSpinning) 0.82f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(4.dp)
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF13112E))
            .border(
                border = BorderStroke(
                    1.dp,
                    if (symbol.isScatter) Color(0xFFFFD700) else Color(symbol.iconColor).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Floating bounce for symbols
            Text(
                text = symbol.displayLabel,
                fontSize = if (symbol.isScatter) 36.sp else 30.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = symbol.name.substringBefore(" "),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.LightGray.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// ZEUS FLOATING CHARACTER COMPONENT
// ==========================================
@Composable
fun ZeusCharacter(zeusAction: String?, multiplier: Int?) {
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = floatAnim.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                when (zeusAction) {
                                    "LIGHTNING" -> Color(0xFF00E5FF)
                                    "LAUGH" -> Color(0xFFFFD700)
                                    else -> Color(0xFF2C244C)
                                },
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Zeus Avatar representation using Emojis and visual feedback
                Text(
                    text = when (zeusAction) {
                        "LIGHTNING" -> "⚡👴⚡"
                        "LAUGH" -> "🤣⚡"
                        else -> "👴"
                    },
                    fontSize = 32.sp
                )
            }
            Text(
                text = "ZEUS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = Color(0xFFFFD700),
                letterSpacing = 2.sp
            )

            // Multiplier Strike Bubble
            AnimatedVisibility(
                visible = multiplier != null,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                if (multiplier != null) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-6).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF311B92))
                            .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ x$multiplier",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// WITHDRAW SIMULATOR TAB
// ==========================================
@Composable
fun WithdrawTab(viewModel: SimViewModel, balance: Long) {
    val wallet by viewModel.selectedWallet.collectAsStateWithLifecycle()
    val name by viewModel.recipientName.collectAsStateWithLifecycle()
    val phone by viewModel.recipientPhone.collectAsStateWithLifecycle()
    val amount by viewModel.withdrawAmount.collectAsStateWithLifecycle()

    val statusMsg by viewModel.withdrawStatusMessage.collectAsStateWithLifecycle()
    var agreeToTerms by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        Text(
            text = "Simulasi Tarik Dana/Withdraw",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        // Important Watermark Disclaimer Board
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1924)),
            border = BorderStroke(1.5.dp, Color(0xFFFF2D55)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = "Peringatan", tint = Color(0xFFFF2D55))
                    Text(
                        text = "DISCLAIMER PENDIDIKAN & KEAMANAN",
                        color = Color(0xFFFF2D55),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Aplikasi ini hanyalah SIMULATOR GAME. Kami TIDAK mengumpulkan uang sungguhan, atau mengirimkan uang asli ke akun e-wallet Anda. Segala proses penarikan di halaman ini murni hiburan edukatif untuk menirukan kebahagiaan memiliki struk sukses transfer agar terhindar dari kerugian judi slot riil.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Available Balance Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13112E)),
            border = BorderStroke(1.dp, Color(0xFF2C244C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SALDO VIRTUAL ANDA", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        "Rp ${viewModel.formatRupiah(balance)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
            }
        }

        // SELECT WALLET TABS
        Text("Pilih E-Wallet Penerima:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("DANA", "OVO", "GoPay").forEach { item ->
                val isSelected = wallet == item
                val themeColor = when (item) {
                    "DANA" -> Color(0xFF118EEA)
                    "OVO" -> Color(0xFF4C2A86)
                    else -> Color(0xFF00C73C)
                }

                Surface(
                    color = if (isSelected) themeColor else Color(0xFF0F0E20),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.5.dp, themeColor),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedWallet.value = item }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            color = if (isSelected) Color.White else themeColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // INPUT FORMS
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Recipient Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 13) viewModel.recipientPhone.value = it },
                label = { Text("Nomor HP E-Wallet (Contoh: 08123456789)") },
                placeholder = { Text("Mulai dengan 08...") },
                singleLine = true,
                prefix = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.padding(end = 4.dp).size(20.dp), tint = Color.LightGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF2C244C),
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Recipient Full Name
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.recipientName.value = it },
                label = { Text("Nama Lengkap Penerima di Akun") },
                placeholder = { Text("Nama sesuai KTP/Profil") },
                singleLine = true,
                prefix = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(end = 4.dp).size(20.dp), tint = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF2C244C),
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Nominal Withdraw
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.withdrawAmount.value = it },
                label = { Text("Jumlah Penarikan Virtual (Rp)") },
                placeholder = { Text("Contoh: 250000") },
                singleLine = true,
                prefix = { Text("Rp ", color = Color.LightGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF2C244C),
                    focusedLabelColor = Color(0xFFFFD700),
                    unfocusedLabelColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Status Error/success message
        if (statusMsg != null) {
            Text(
                text = "⚠️ $statusMsg",
                color = Color(0xFFFF6F00),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Terms Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { agreeToTerms = !agreeToTerms }
                .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
            Checkbox(
                checked = agreeToTerms,
                onCheckedChange = { agreeToTerms = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFFD700),
                    checkmarkColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Saya sadar ini sepenuhnya simulasi visual dan tidak ada uang riil yang benar-benar ditransfer.",
                color = Color.LightGray,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        // WD SUBMIT BUTTON
        Button(
            onClick = { viewModel.submitSimulatedWithdrawal() },
            enabled = agreeToTerms,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                disabledContainerColor = Color(0x3B4CAF50)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("PROSES WD SIMULASI NOW", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// PREVIOUS TRANSACTION HISTORY TAB
// ==========================================
@Composable
fun HistoryTab(viewModel: SimViewModel, transactions: List<SimTransaction>) {
    var selectedTxForReceipt by remember { mutableStateOf<SimTransaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Simulasi Transaksi & Struk",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (transactions.isNotEmpty()) {
                Text(
                    text = "Hapus Riwayat",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.clearHistory() }
                )
            }
        }

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("😪", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum Ada Riwayat Tarik Dana!",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Putar slot, kumpulkan kemenangan, lalu lakukan simulasi transfer saldo di tab Simulasi WD.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { tx ->
                    Surface(
                        color = Color(0xFF13112E),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF2C244C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTxForReceipt = tx }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Wallet colored brand circle
                                val color = when (tx.walletType) {
                                    "DANA" -> Color(0xFF118EEA)
                                    "OVO" -> Color(0xFF4C2A86)
                                    else -> Color(0xFF00C73C)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tx.walletType.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Tarik Dana " + tx.walletType,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Penerima: " + tx.recipientName,
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                                        .format(Date(tx.timestamp))
                                    Text(
                                        text = dateStr,
                                        color = Color.Gray,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Rp " + viewModel.formatRupiah(tx.amount),
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                // Success Badge simulating success receipt
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(11.dp))
                                    Text("Berhasil", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog for viewing receipt from historical items
        if (selectedTxForReceipt != null) {
            Dialog(onDismissRequest = { selectedTxForReceipt = null }) {
                ReceiptModal(
                    transaction = selectedTxForReceipt!!,
                    onDismiss = { selectedTxForReceipt = null }
                )
            }
        }
    }
}

// ==========================================
// SLOTS & HEALTH EDUCATION TAB
// ==========================================
@Composable
fun InfoTab() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edukasi Bahaya Judi Slot",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13112E)),
            border = BorderStroke(1.dp, Color(0xFF2C244C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Mengapa Slot Asli Selalu Menguras Uang?",
                    color = Color(0xFFFF7043),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "1. RTP (Return To Player) Selalu Kurang dari 100%: Dalam perjudian slot asli, mesin dirancang untuk mengembalikan persentase uang yang lebih sedikit dari total taruhan. Secara jangka panjang, bandar pasti menang (The House Always Wins).\n\n" +
                            "2. Hormon Dopamin Palsu: Efek suara megah, kelap-kelip lampu, dan Zeus yang menyambar petir melepaskan dopamin di otak Anda, membuat Anda ketagihan bahkan saat Anda sedang kalah besar.\n\n" +
                            "3. Efek Kekalahan Nyaris Menang (Near-Miss Effect): Munculnya dua lambang Scatter sering disengaja oleh algoritme asli untuk memicu sensasi bahwa Anda 'hampir menang besar', memaksa Anda mengisi ulang saldo lagi dan lagi.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B15)),
            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Gunakan Game Ini Sebagai Terapi!",
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Simulasi ini dirancang dengan winrate tinggi dan sensasi kemajuan virtual untuk memberikan Anda kepuasan visual putaran Pragmatic secara GRATIS. Anda dapat melatih otak untuk merasakan kesenangan visual tanpa perlu menyetor uang sepeser pun ke bandar slot asli.\n\nSimulasikan WD sukses sepuasnya secara aman tanpa membahayakan ekonomi dan kesehatan mental keluarga Anda!",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Text(
            text = "Pragmatic Play Slot Sim v1.0 • Aman & Transparan",
            color = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==========================================
// STUNNING E-WALLET SUCCESSFUL RECEIPT CREATOR
// ==========================================
@Composable
fun ReceiptModal(
    transaction: SimTransaction,
    onDismiss: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        .format(Date(transaction.timestamp))

    // Colors mapping based on Wallet brand
    val primaryColor = when (transaction.walletType) {
        "DANA" -> Color(0xFF118EEA) // DANA Royal Blue
        "OVO" -> Color(0xFF4C2A86)  // OVO Purple
        else -> Color(0xFF00C73C)   // GoPay Green
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .border(2.dp, primaryColor, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Receipt Header Brand
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(primaryColor)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transaction.walletType,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "BUKTI TRANSFER",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Successful Status Animation Circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Transaksi Berhasil",
                color = Color(0xFF333333),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )

            Text(
                text = dateStr,
                color = Color.Gray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Simulated Watermark overlay warning (Keep transparency & security)
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB74D))
            ) {
                Text(
                    text = "⚠️ STRUK SIMULASI GAME • TANPA NILAI RIIL (VIRTUAL)",
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detailed Receipts Field Row block
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Divider line style
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.6f))

                // Received Nominal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Jumlah Transfer", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = "Rp " + String.format("%,d", transaction.amount).replace(',', '.'),
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.6f))

                // Recipient details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Penerima", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = transaction.recipientName,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Phone details obscured
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("No. HP / ID", color = Color.Gray, fontSize = 12.sp)
                    val originalPhone = transaction.recipientPhone
                    val obscured = if (originalPhone.length >= 8) {
                        originalPhone.substring(0, 4) + "-****-" + originalPhone.substring(originalPhone.length - 3)
                    } else {
                        originalPhone
                    }
                    Text(
                        text = obscured,
                        color = Color(0xFF555555),
                        fontSize = 12.sp
                    )
                }

                // Source of fund
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sumber Dana", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = "Kemenangan Slot (Simulasi)",
                        color = Color(0xFF555555),
                        fontSize = 12.sp
                    )
                }

                // Reference Codes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nomor Referensi", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = transaction.referenceNum,
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer safety disclaimer label
            Text(
                text = "Bukti transfer ini dibuat secara acak & otomatis untuk keperluan dokumentasi simulasi game. Transaksi ini murni bersifat fiktif/simulasi.",
                color = Color.Gray,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dismiss Button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bagikan & Tutup Struk", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
