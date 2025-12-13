package com.codeartify.customerservice

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddOrderCommand(
    @TargetAggregateIdentifier
    val customerId: String,
    val orderId: String,
    val amount: Double
)
