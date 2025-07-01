package com.example.controledegastos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Limite(
    @PrimaryKey val categoria: String,
    val limiteCategoria: Double,
    val limiteMensal: Double
)
