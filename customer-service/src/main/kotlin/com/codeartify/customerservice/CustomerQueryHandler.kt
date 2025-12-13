package com.codeartify.customerservice

import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("customer-query-processing")
class CustomerQueryHandler(
    private val customerRepository: CustomerRepository,
    private val queryUpdateEmitter: QueryUpdateEmitter
) {

    @QueryHandler
    fun handle(query: GetCustomerQuery): CustomerResponse? {
        return customerRepository.findById(query.customerId)
            .map { CustomerResponse(
                id = it.id,
                name = it.name,
                orderIds = it.orders.map { order -> order.orderId }
            )}
            .orElse(null)
    }

    @EventHandler
    fun on(evt: CustomerRegisteredEvent) {
        val customer = customerRepository.findById(evt.customerId).orElseThrow()
        val response = CustomerResponse(
            id = customer.id,
            name = customer.name,
            orderIds = emptyList()
        )
        queryUpdateEmitter.emit(GetCustomerQuery::class.java, { it.customerId == evt.customerId }, response)
    }

    @EventHandler
    fun on(evt: OrderAddedEvent) {
        val customer = customerRepository.findById(evt.customerId).orElseThrow()
        val response = CustomerResponse(
            id = customer.id,
            name = customer.name,
            orderIds = customer.orders.map { it.orderId }
        )
        queryUpdateEmitter.emit(GetCustomerQuery::class.java, { it.customerId == evt.customerId }, response)
    }
}

data class GetCustomerQuery(val customerId: String)
