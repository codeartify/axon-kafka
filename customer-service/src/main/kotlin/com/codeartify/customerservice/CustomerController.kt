package com.codeartify.customerservice

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.util.UUID

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val customerRepository: CustomerRepository
) {

    @PostMapping("/register")
    fun registerCustomer(@RequestBody request: RegisterCustomerRequest): ResponseEntity<CustomerResponse> {
        val customerId = UUID.randomUUID().toString()

        val subscriptionQuery = queryGateway.subscriptionQuery(
            GetCustomerQuery(customerId),
            ResponseTypes.instanceOf(CustomerResponse::class.java),
            ResponseTypes.instanceOf(CustomerResponse::class.java)
        )

        try {
            commandGateway.sendAndWait<String>(RegisterCustomerCommand(customerId, request.name))

            val customer = subscriptionQuery.updates()
                .blockFirst(Duration.ofSeconds(5))
                ?: throw RuntimeException("Timeout waiting for customer projection")

            return ResponseEntity.ok(customer)
        } finally {
            subscriptionQuery.close()
        }
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: String): ResponseEntity<CustomerResponse> {
        val customer = customerRepository.findById(id).orElseThrow()
        return ResponseEntity.ok(CustomerResponse(
            id = customer.id,
            name = customer.name,
            orderIds = customer.orders.map { it.orderId }
        ))
    }
}

data class RegisterCustomerRequest(
    val name: String
)

data class CustomerResponse(
    val id: String,
    val name: String,
    val orderIds: List<String> = emptyList()
)
