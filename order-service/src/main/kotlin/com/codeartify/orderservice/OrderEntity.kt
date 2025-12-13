package com.codeartify.orderservice

import jakarta.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity() {
    @Id
    var orderId: String = ""
    var customerId: String = ""
    var amount: Double = 0.0
    var title: String = ""

    constructor(orderId: String, customerId: String, amount: Double, title: String) : this() {
        this.orderId = orderId
        this.customerId = customerId
        this.amount = amount
        this.title = title
    }
}
