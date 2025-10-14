package com.streaem.products.exception

class ProductNotFoundException(productId: String) : RuntimeException("Product with id '$productId' not found")
