package com.codeartify.customerservice

import java.math.BigDecimal

data class OrderPlacedEvent(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal
)
