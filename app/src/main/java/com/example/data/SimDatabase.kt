package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "balance_table")
data class SimulateBalance(
    @PrimaryKey val id: Int = 1,
    val balance: Long = 10000000L // 10,000,000 starting virtual balance (Rp 10 Million)
)

@Entity(tableName = "transaction_table")
data class SimTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Long,
    val walletType: String, // "DANA", "OVO", "GoPay"
    val recipientName: String,
    val recipientPhone: String,
    val referenceNum: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface SimDao {
    @Query("SELECT * FROM balance_table WHERE id = 1 LIMIT 1")
    fun getBalance(): Flow<SimulateBalance?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBalance(balance: SimulateBalance)

    @Query("SELECT * FROM transaction_table ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<SimTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SimTransaction)

    @Query("DELETE FROM transaction_table")
    suspend fun clearHistory()
}

@Database(entities = [SimulateBalance::class, SimTransaction::class], version = 1, exportSchema = false)
abstract class SimDatabase : RoomDatabase() {
    abstract fun simDao(): SimDao

    companion object {
        @Volatile
        private var INSTANCE: SimDatabase? = null

        fun getDatabase(context: Context): SimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimDatabase::class.java,
                    "sim_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
