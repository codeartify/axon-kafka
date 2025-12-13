package com.codeartify.orderservice

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class OrderAggregate() {

    @AggregateIdentifier
    private lateinit var orderId: String
    private lateinit var customerId: String
    private lateinit var status: String

    @CommandHandler
    constructor(cmd: PlaceOrderCommand) : this() {
        require(cmd.orderId.isNotBlank()) { "Order ID must not be blank" }
        require(cmd.customerId.isNotBlank()) { "Customer ID must not be blank" }
        require(cmd.amount.toDouble() > 0) { "Amount must be greater than zero" }

        apply(OrderPlacedEvent(orderId = cmd.orderId, customerId = cmd.customerId, amount = cmd.amount))
    }

    @EventSourcingHandler
    fun on(evt: OrderPlacedEvent) {
        orderId = evt.orderId
        customerId = evt.customerId
        status = "PLACED"
    }
}
