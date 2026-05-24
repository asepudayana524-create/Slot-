package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SimRepository(private val simDao: SimDao) {

    val balanceFlow: Flow<SimulateBalance?> = simDao.getBalance()
    val transactionsFlow: Flow<List<SimTransaction>> = simDao.getAllTransactions()

    suspend fun updateBalance(newBalance: Long) {
        simDao.updateBalance(SimulateBalance(balance = newBalance))
    }

    suspend fun addTransaction(
        amount: Long,
        walletType: String,
        recipientName: String,
        recipientPhone: String,
        referenceNum: String
    ) {
        val currentBalance = balanceFlow.firstOrNull()?.balance ?: 10000000L
        val nextBalance = maxOf(0L, currentBalance - amount)
        simDao.updateBalance(SimulateBalance(balance = nextBalance))
        
        simDao.insertTransaction(
            SimTransaction(
                amount = amount,
                walletType = walletType,
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                referenceNum = referenceNum
            )
        )
    }

    suspend fun clearHistory() {
        simDao.clearHistory()
    }

    suspend fun initBalance() {
        val current = balanceFlow.firstOrNull()
        if (current == null) {
            simDao.updateBalance(SimulateBalance())
        }
    }
}
