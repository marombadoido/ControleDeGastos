package com.example.controledegastos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LimiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun definirLimite(limite: Limite)

    @Query("SELECT * FROM Limite")
    suspend fun listarTodos(): List<Limite>

    @Query("SELECT * FROM Limite WHERE categoria = :categoria")
    suspend fun buscarPorCategoria(categoria: String): Limite?
}
