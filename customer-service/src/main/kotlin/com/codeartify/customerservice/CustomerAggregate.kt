package com.codeartify.customerservice

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class CustomerAggregate() {

    @AggregateIdentifier
    lateinit var customerId: String

    lateinit var name: String

    private val orders = mutableListOf<Order>()

    @CommandHandler
    constructor(cmd: RegisterCustomerCommand) : this() {
        require(cmd.customerId.isNotBlank()) { "Customer ID must not be blank" }
        require(cmd.name.isNotBlank()) { "Name must not be blank" }

        apply(CustomerRegisteredEvent(cmd.customerId, cmd.name))
    }

    @CommandHandler
    fun handle(cmd: AddOrderCommand) {
        require(cmd.customerId.isNotBlank()) { "Customer ID must not be blank" }
        require(cmd.orderId.isNotBlank()) { "Order ID must not be blank" }
        require(cmd.amount > 0) { "Amount must be greater than zero" }

        apply(OrderAddedEvent(cmd.customerId, cmd.orderId, cmd.amount))
    }

    @EventSourcingHandler
    fun on(evt: CustomerRegisteredEvent) {
        customerId = evt.customerId
        name = evt.name
    }

    @EventSourcingHandler
    fun on(evt: OrderAddedEvent) {
        this.orders.add(Order(evt.orderId, evt.amount))
    }
}
