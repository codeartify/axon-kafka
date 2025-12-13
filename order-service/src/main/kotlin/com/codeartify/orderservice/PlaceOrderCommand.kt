package com.codeartify.orderservice

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class PlaceOrderCommand(
    @TargetAggregateIdentifier val orderId: String,
    val customerId: String,
    val amount: BigDecimal
)

