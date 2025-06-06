package com.example.inventorycotrol.data.remote.dto

import com.example.inventorycotrol.domain.model.order.Attachment
import com.example.inventorycotrol.domain.model.order.OrderDiscount
import com.example.inventorycotrol.domain.model.order.OrderProduct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val products: List<OrderProduct>,
    val discount: OrderDiscount,
    val comment: String? = null,
    val attachments: List<Attachment>,
    @SerialName("organisation_id")
    val organisationId: String,
    @SerialName("ordered_by")
    val orderedBy: String,
    @SerialName("ordered_at")
    val orderedAt: Long
)

