package com.streaem.products.entity

import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class Product(
    val id: String,
    val name: String,
    val category: String?,
    val description: String?,
    val price: BigDecimal?
)