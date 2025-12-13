package com.codeartify.customerservice

data class CustomerRegisteredEvent(
    val customerId: String,
    val name: String
)
