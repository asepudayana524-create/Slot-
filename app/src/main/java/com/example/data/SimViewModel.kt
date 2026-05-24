package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

// Represent a Symbol on the Slot
data class SlotSymbol(
    val name: String,
    val displayLabel: String,
    val multiplier: Double,
    val iconColor: Long, // Hex color for M3 styling
    val isScatter: Boolean = false,
    val description: String
)

class SimViewModel(
    application: Application,
    private val repository: SimRepository
) : AndroidViewModel(application) {

    // List of Slot Symbols
    val symbols = listOf(
        SlotSymbol("Scatter Zeus", "⚡", 5.0, 0xFFFFD700, true, "Trigger Freespin & Bayar di mana saja"),
        SlotSymbol("Mahkota Emas", "👑", 20.0, 0xFFFFB300, false, "Kombinasi Mahkota Dewa Olympus"),
        SlotSymbol("Cincin Zeus", "💍", 10.0, 0xFFE040FB, false, "Cincin Emas Kerajaan"),
        SlotSymbol("Jam Pasir", "⏳", 6.0, 0xFF00E676, false, "Waktu Keberuntungan"),
        SlotSymbol("Cawan Anggur", "🏆", 4.0, 0xFF29B6F6, false, "Piala Kemenangan Emas"),
        SlotSymbol("Gem Merah", "❤️", 2.0, 0xFFFF1744, false, "Permata Merah Delima"),
        SlotSymbol("Gem Ungu", "💜", 1.5, 0xFFAA00FF, false, "Permata Ametis"),
        SlotSymbol("Gem Biru", "💙", 1.0, 0xFF2979FF, false, "Permata Safir"),
        SlotSymbol("Gem Hijau", "💚", 0.6, 0xFF00E676, false, "Permata Zamrud")
    )

    // Database Flows
    val balance: StateFlow<Long> = repository.balanceFlow
        .map { it?.balance ?: 10000000L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000000L)

    val transactions: StateFlow<List<SimTransaction>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Slot States
    private val _reels = MutableStateFlow<List<SlotSymbol>>(List(9) { symbols[Random.nextInt(1, symbols.size)] })
    val reels = _reels.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning = _isSpinning.asStateFlow()

    private val _winAmount = MutableStateFlow(0L)
    val winAmount = _winAmount.asStateFlow()

    private val _activeMultiplier = MutableStateFlow<Int?>(null)
    val activeMultiplier = _activeMultiplier.asStateFlow()

    private val _zeusAction = MutableStateFlow<String?>("IDLE") // IDLE, LAUGH, LIGHTNING
    val zeusAction = _zeusAction.asStateFlow()

    private val _winNotification = MutableStateFlow<String?>(null) // "BIG WIN", "SENSATIONAL", "MEGA WIN"
    val winNotification = _winNotification.asStateFlow()

    // Free Spins States
    private val _freeSpinsRemaining = MutableStateFlow(0)
    val freeSpinsRemaining = _freeSpinsRemaining.asStateFlow()

    private val _isFreeSpinsMode = MutableStateFlow(false)
    val isFreeSpinsMode = _isFreeSpinsMode.asStateFlow()

    private val _freeSpinsTotalWin = MutableStateFlow(0L)
    val freeSpinsTotalWin = _freeSpinsTotalWin.asStateFlow()

    private val _freeSpinsAccumMultiplier = MutableStateFlow(0)
    val freeSpinsAccumMultiplier = _freeSpinsAccumMultiplier.asStateFlow()

    // Game Adjustment States
    private val _betAmount = MutableStateFlow(20000L) // Default Rp 20K
    val betAmount = _betAmount.asStateFlow()

    private val _doubleChance = MutableStateFlow(false)
    val doubleChance = _doubleChance.asStateFlow()

    private val _autoSpinsRemaining = MutableStateFlow(0)
    val autoSpinsRemaining = _autoSpinsRemaining.asStateFlow()

    // E-Wallet simulated state
    var selectedWallet = MutableStateFlow("DANA")
    var recipientName = MutableStateFlow("")
    var recipientPhone = MutableStateFlow("")
    var withdrawAmount = MutableStateFlow("")
    
    private val _showReceipt = MutableStateFlow<SimTransaction?>(null)
    val showReceipt = _showReceipt.asStateFlow()

    private val _withdrawStatusMessage = MutableStateFlow<String?>(null)
    val withdrawStatusMessage = _withdrawStatusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initBalance()
        }
    }

    // Set bet size
    fun setBet(amount: Long) {
        if (!_isSpinning.value && !_isFreeSpinsMode.value) {
            _betAmount.value = maxOf(1000L, amount)
        }
    }

    fun toggleDoubleChance() {
        if (!_isSpinning.value && !_isFreeSpinsMode.value) {
            _doubleChance.value = !_doubleChance.value
        }
    }

    // Free Balance refill
    fun refillBalance() {
        viewModelScope.launch {
            repository.updateBalance(10000000L) // Reset to 10 Million
        }
    }

    // Trigger standard spin
    fun spin() {
        if (_isSpinning.value) return

        val cost = getSpinCost()
        if (balance.value < cost && !_isFreeSpinsMode.value) {
            _withdrawStatusMessage.value = "Saldo tidak cukup! Silakan isi ulang saldo di menu Profil secara gratis."
            return
        }

        viewModelScope.launch {
            _isSpinning.value = true
            _winAmount.value = 0
            _activeMultiplier.value = null
            _winNotification.value = null
            _zeusAction.value = "IDLE"

            // Deduct cost
            if (!_isFreeSpinsMode.value) {
                repository.updateBalance(balance.value - cost)
            }

            // Simulate spinning effect (reels changing rapidly)
            repeat(16) { stage ->
                _reels.value = List(9) {
                    val pool = if (_doubleChance.value) {
                        // Increase Scatter spawning rates
                        if (Random.nextFloat() < 0.20f) listOf(symbols[0]) else symbols.subList(1, symbols.size)
                    } else {
                        symbols
                    }
                    pool.random()
                }
                delay(60 + (stage * 15L)) // Gradiated slow down
            }

            // Decide Final Spin Board!
            val finalReels = List(9) {
                val pool = if (_doubleChance.value) {
                    if (Random.nextFloat() < 0.18f) listOf(symbols[0]) else symbols.subList(1, symbols.size)
                } else {
                    if (Random.nextFloat() < 0.08f) listOf(symbols[0]) else symbols.subList(1, symbols.size)
                }
                pool.random()
            }
            _reels.value = finalReels

            // Calculate Wins
            calculateSpinResults(finalReels)

            _isSpinning.value = false

            // Process auto spins if any
            if (_autoSpinsRemaining.value > 0 && !_isFreeSpinsMode.value) {
                _autoSpinsRemaining.value--
                if (_autoSpinsRemaining.value > 0) {
                    delay(1500)
                    spin()
                }
            }
        }
    }

    private suspend fun calculateSpinResults(board: List<SlotSymbol>) {
        val currentBet = _betAmount.value
        var grossWin = 0.0

        // Paylines on 3x3 matrix:
        // Horizontal: 0-1-2, 3-4-5, 6-7-8
        // Vertical: 0-3-6, 1-4-7, 2-5-8
        // Diagonal: 0-4-8, 2-4-6
        val paylines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Horizontals
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Verticals
            listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonals
        )

        val winningSymbols = mutableListOf<String>()

        for (line in paylines) {
            val sym1 = board[line[0]]
            val sym2 = board[line[1]]
            val sym3 = board[line[2]]

            if (!sym1.isScatter && sym1.name == sym2.name && sym2.name == sym3.name) {
                grossWin += currentBet * sym1.multiplier
                winningSymbols.add(sym1.name)
            }
        }

        // Count Scatters (⚡) anywhere on the grid
        val scatterCount = board.count { it.isScatter }
        if (scatterCount >= 3) {
            // Scatter pay!
            grossWin += currentBet * 5.0 // Direct Scatter pay multiplication
            _zeusAction.value = "LAUGH"
            delay(500)
            
            // Trigger 10 Free Spins!
            if (!_isFreeSpinsMode.value) {
                triggerFreeSpins()
            } else {
                // Retrigger 5 additional free spins!
                _freeSpinsRemaining.value += 5
                _winNotification.value = "RE-TRIGGER +5!"
                delay(800)
            }
        }

        // Apply lightning Zeus multiplier if we won SOMETHING
        if (grossWin > 0.0) {
            val randVal = Random.nextFloat()
            // 45% chance to drop multiplier in regular spin, 80% in free spin mode
            val multiplierChance = if (_isFreeSpinsMode.value) 0.80f else 0.45f

            if (randVal < multiplierChance) {
                _zeusAction.value = "LIGHTNING"
                delay(600) // Animating lightning

                val multOptions = listOf(2, 3, 5, 8, 10, 15, 25, 50, 100, 250, 500)
                val chosenMult = multOptions.random()
                _activeMultiplier.value = chosenMult

                if (_isFreeSpinsMode.value) {
                    _freeSpinsAccumMultiplier.value += chosenMult
                }
                delay(800)
            }

            // Total spin calculation
            val finalMultiplier = if (_isFreeSpinsMode.value) {
                // If we got a new multiplier this spin, we use the active accumulated. Else we use current spin mult.
                if (_freeSpinsAccumMultiplier.value > 0) _freeSpinsAccumMultiplier.value else 1
            } else {
                _activeMultiplier.value ?: 1
            }

            val finalWin = (grossWin * finalMultiplier).toLong()
            _winAmount.value = finalWin

            // Add notifications for massive wins
            val winMultiple = finalWin.toDouble() / currentBet
            _winNotification.value = when {
                winMultiple >= 100.0 -> "🥳 SENSATIONAL WIN! 🥳"
                winMultiple >= 30.0 -> "✨ MEGA WIN! ✨"
                winMultiple >= 10.0 -> "💫 BIG WIN! 💫"
                else -> "MENANG! 🎉"
            }

            // Persistence update
            if (_isFreeSpinsMode.value) {
                _freeSpinsTotalWin.value += finalWin
            } else {
                repository.updateBalance(balance.value + finalWin)
            }
            delay(1000)
        } else {
            // Check if Zeus should laugh at losses (15% chance to tease the player)
            if (Random.nextFloat() < 0.15f) {
                _zeusAction.value = "LAUGH"
                delay(1200)
                _zeusAction.value = "IDLE"
            }
        }

        // Post free-spins calculation transitions
        if (_isFreeSpinsMode.value) {
            _freeSpinsRemaining.value--
            if (_freeSpinsRemaining.value <= 0) {
                // Free spins finished! Apply total bonus to real balance
                delay(1000)
                _winNotification.value = "TOTAL FREESPIN WIN: Rp " + formatRupiah(_freeSpinsTotalWin.value)
                repository.updateBalance(balance.value + _freeSpinsTotalWin.value)
                delay(3000)
                _isFreeSpinsMode.value = false
                _freeSpinsTotalWin.value = 0
                _freeSpinsAccumMultiplier.value = 0
                _winNotification.value = null
            } else {
                // Next free spin auto trigger
                delay(2000)
                _winAmount.value = 0
                _activeMultiplier.value = null
                _winNotification.value = null
                _zeusAction.value = "IDLE"
                spin()
            }
        }
    }

    private fun triggerFreeSpins() {
        _isFreeSpinsMode.value = true
        _freeSpinsRemaining.value = 10
        _freeSpinsTotalWin.value = 0
        _freeSpinsAccumMultiplier.value = 0
        _winNotification.value = "⚡ MASUK BABAK FREESPIN (10x)! ⚡"
    }

    fun buyFreeSpins() {
        if (_isSpinning.value || _isFreeSpinsMode.value) return
        val cost = getBuyFreeSpinsCost()
        if (balance.value < cost) {
            _withdrawStatusMessage.value = "Saldo tidak cukup untuk Beli Freespin!"
            return
        }

        viewModelScope.launch {
            _isSpinning.value = true
            repository.updateBalance(balance.value - cost)
            _winAmount.value = 0
            _activeMultiplier.value = null
            _winNotification.value = "MEMBELI FREESPIN UNTUK 10X PUTARAN!"
            delay(1500)
            
            _isSpinning.value = false
            triggerFreeSpins()
            delay(1500)
            spin() // Automatically launch first free spin
        }
    }

    fun startAutoSpins(spinsCount: Int) {
        if (_isSpinning.value || _isFreeSpinsMode.value) return
        _autoSpinsRemaining.value = spinsCount
        spin()
    }

    fun stopAutoSpins() {
        _autoSpinsRemaining.value = 0
    }

    fun getSpinCost(): Long {
        val base = _betAmount.value
        return if (_doubleChance.value) (base * 1.25).toLong() else base
    }

    fun getBuyFreeSpinsCost(): Long {
        return _betAmount.value * 100
    }

    // Process Simulated E-Wallet Withdrawal
    fun submitSimulatedWithdrawal() {
        val name = recipientName.value.trim()
        val phone = recipientPhone.value.trim()
        val amountStr = withdrawAmount.value.trim()

        if (name.isEmpty()) {
            _withdrawStatusMessage.value = "Masukkan nama penerima!"
            return
        }
        if (phone.length < 9 || !phone.startsWith("08")) {
            _withdrawStatusMessage.value = "Nomor telepon harus diawali '08' dan minimal 9 digit!"
            return
        }
        val amount = amountStr.toLongOrNull() ?: 0L
        if (amount < 25000L) {
            _withdrawStatusMessage.value = "Minimum penarikan adalah Rp 25.000!"
            return
        }
        if (balance.value < amount) {
            _withdrawStatusMessage.value = "Kemenangan virtual Anda tidak cukup!"
            return
        }

        viewModelScope.launch {
            _withdrawStatusMessage.value = null
            // Generate simulated ref ID
            val refCode = "REF-" + System.currentTimeMillis() + "-" + (1000..9999).random()
            
            // Add simulates withdrawal
            repository.addTransaction(
                amount = amount,
                walletType = selectedWallet.value,
                recipientName = name,
                recipientPhone = phone,
                referenceNum = refCode
            )

            // Show receipt
            _showReceipt.value = SimTransaction(
                amount = amount,
                walletType = selectedWallet.value,
                recipientName = name,
                recipientPhone = phone,
                referenceNum = refCode,
                timestamp = System.currentTimeMillis()
            )

            // Reset inputs
            withdrawAmount.value = ""
        }
    }

    fun clearReceipt() {
        _showReceipt.value = null
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Utility formatter helper
    fun formatRupiah(value: Long): String {
        return String.format("%,d", value).replace(',', '.')
    }
}

// ViewModel Factory
class SimViewModelFactory(
    private val application: Application,
    private val repository: SimRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SimViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
