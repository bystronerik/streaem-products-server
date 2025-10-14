package com.streaem.products.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@Introspected
data class ProductFilters(val instock: Boolean?)