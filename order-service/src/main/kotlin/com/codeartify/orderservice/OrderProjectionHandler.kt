package com.codeartify.orderservice

import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("order-projection")
class OrderProjectionHandler(
    private val orderRepository: OrderRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun on(evt: OrderPlacedEvent) {
        log.info("Creating order projection for: {}", evt.orderId)
        orderRepository.save(OrderEntity(evt.orderId, evt.customerId, evt.amount.toDouble(), "Order ${evt.amount}"))
    }
}
