package com.codeartify.customerservice

 import org.axonframework.commandhandling.gateway.CommandGateway
 import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("customer-order-processing")
class CustomerOrderEventHandler(
    private val commandGateway: CommandGateway
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun on(evt: OrderPlacedEvent) {
        log.info("========================================")
        log.info("Customer service received OrderPlacedEvent: {}", evt)
        log.info("Order ID: {}, Customer ID: {}, Amount: {}", evt.orderId, evt.customerId, evt.amount)
        log.info("========================================")

        try {
            commandGateway.sendAndWait<Any>(AddOrderCommand(evt.customerId, evt.orderId, evt.amount.toDouble()))
            log.info("Successfully processed order {} for customer {}", evt.orderId, evt.customerId)
        } catch (e: Exception) {
            log.error("Failed to add order {} for customer {}: {}", evt.orderId, evt.customerId, e.message)
            // Don't rethrow - acknowledge the message to prevent infinite retry
        }
    }
}
