package com.example.controledegastos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Gasto::class, Limite::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao
    abstract fun limiteDao(): LimiteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gasto_db"
                )
                    .fallbackToDestructiveMigration() // Descarta dados antigos se schema mudar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
