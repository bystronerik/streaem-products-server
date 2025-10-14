package com.streaem.products.model

import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class ProductPatchInput(
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null
)
