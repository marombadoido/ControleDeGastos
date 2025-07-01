package com.example.controledegastos

import androidx.room.*

@Dao
interface GastoDao {

    @Insert
    fun inserir(gasto: Gasto)

    @Update
    fun atualizar(gasto: Gasto)

    @Delete
    fun excluir(gasto: Gasto)

    @Query("SELECT * FROM gasto")
    fun listarTodos(): List<Gasto>

    @Query("SELECT SUM(valor) FROM gasto WHERE categoria = :categoria")
    fun totalPorCategoria(categoria: String): Double?

    @Query("SELECT SUM(valor) FROM gasto")
    fun totalMensal(): Double?
}

