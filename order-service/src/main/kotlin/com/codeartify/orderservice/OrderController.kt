package com.codeartify.orderservice

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val commandGateway: CommandGateway
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
}
