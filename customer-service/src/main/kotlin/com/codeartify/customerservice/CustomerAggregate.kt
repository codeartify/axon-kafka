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

    @CommandHandler
    constructor(cmd: RegisterCustomerCommand) : this() {
        apply(CustomerRegisteredEvent(cmd.customerId, cmd.name))
    }

    @CommandHandler
    fun handle(cmd: AddOrderCommand) {
        apply(OrderAddedEvent(cmd.customerId, cmd.orderId, cmd.amount))
    }

    @EventSourcingHandler
    fun on(evt: CustomerRegisteredEvent) {
        customerId = evt.customerId
        name = evt.name
    }
}
