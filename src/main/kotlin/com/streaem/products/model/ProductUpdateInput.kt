package com.streaem.products.model

import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class ProductUpdateInput(
    val name: String,
    val category: String?,
    val description: String?,
    val price: BigDecimal?
)
