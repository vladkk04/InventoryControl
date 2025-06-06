package com.example.inventorycotrol.domain.model.order

data class OrderProductSelectedData(
    val productSelectedId: String,
    val image: String?,
    val name: String,
    val quantity: Double,
    val rate: Double,
)
