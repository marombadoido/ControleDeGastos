package com.example.controledegastos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descricao: String,
    val valor: Double,
    val categoria: String
)

