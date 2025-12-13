package com.codeartify.customerservice

import jakarta.persistence.*

@Entity
@Table(name = "customers")
class CustomerEntity() {
    @Id
    var id: String = ""
    var name: String = ""
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var orders: MutableList<OrderEntity> = mutableListOf()

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}
