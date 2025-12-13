package com.codeartify.customerservice

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val commandGateway: CommandGateway
) {

    @PostMapping("/register")
    fun registerCustomer(@RequestBody request: RegisterCustomerRequest): CompletableFuture<String> {
        val customerId = UUID.randomUUID().toString()
        return commandGateway.send(RegisterCustomerCommand(customerId, request.name))
    }
}

data class RegisterCustomerRequest(
    val name: String
)
