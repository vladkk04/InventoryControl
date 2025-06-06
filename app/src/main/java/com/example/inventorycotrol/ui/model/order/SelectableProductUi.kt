package com.example.inventorycotrol.ui.model.order

import com.example.inventorycotrol.domain.model.product.ProductUnit

data class SelectableProductUi(
    val id: String,
    val name: String,
    val image: String?,
    val barcode: String? = null,
    val currentStock: Double,
    val minStockLevel: Double,
    val unit: ProductUnit,
    val isSelected: Boolean = false
)
