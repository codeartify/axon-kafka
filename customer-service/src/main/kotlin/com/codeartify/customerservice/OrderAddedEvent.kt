package com.codeartify.customerservice

data class OrderAddedEvent(val customerId: String, val orderId: String, val amount: Double)
