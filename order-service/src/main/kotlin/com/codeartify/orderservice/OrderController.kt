package com.codeartify.orderservice

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val commandGateway: CommandGateway,
    private val orderRepository: OrderRepository
) {

    @PostMapping
    fun placeOrder(
        @RequestParam customerId: String,
        @RequestParam amount: BigDecimal
    ): String {
        val orderId = UUID.randomUUID().toString()
        commandGateway.sendAndWait<String>(
            PlaceOrderCommand(
                orderId = orderId,
                customerId = customerId,
                amount = amount
            )
        )
        return orderId
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): ResponseEntity<OrderResponse> {
        val order = orderRepository.findById(orderId).orElseThrow()
        return ResponseEntity.ok(OrderResponse(
            orderId = order.orderId,
            customerId = order.customerId,
            amount = order.amount,
            title = order.title
        ))
    }
}

data class OrderResponse(
    val orderId: String,
    val customerId: String,
    val amount: Double,
    val title: String
)
