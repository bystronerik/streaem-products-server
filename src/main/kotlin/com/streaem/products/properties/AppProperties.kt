package com.streaem.products.properties

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("app")
class AppProperties {
    var productsSource: String = "http://localhost:4001/productdata"
}