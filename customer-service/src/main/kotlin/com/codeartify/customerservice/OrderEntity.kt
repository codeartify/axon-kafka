package com.codeartify.customerservice

import jakarta.persistence.*

@Entity
@Table(name = "customer_orders")
class OrderEntity() {
    @Id
    var orderId: String = ""
    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: CustomerEntity? = null

    constructor(orderId: String, customer: CustomerEntity) : this() {
        this.orderId = orderId
        this.customer = customer
    }
}
