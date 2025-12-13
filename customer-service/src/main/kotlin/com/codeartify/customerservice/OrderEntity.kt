package com.codeartify.customerservice

import jakarta.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity() {
    @Id
    var id: String = ""
    var amount: Double = 0.0
    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: CustomerEntity? = null

    constructor(id: String, amount: Double, customer: CustomerEntity) : this() {
        this.id = id
        this.amount = amount
        this.customer = customer
    }
}
