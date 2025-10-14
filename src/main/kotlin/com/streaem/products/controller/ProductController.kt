package com.streaem.products.controller

import com.streaem.products.entity.Product
import com.streaem.products.model.ProductPatchInput
import com.streaem.products.model.ProductUpdateInput
import com.streaem.products.service.ProductService
import io.micronaut.http.annotation.*

@Controller("/products")
class ProductController(private val productService: ProductService) {

    @Get("/")
    fun all(): List<Product> {
        return productService.fetchAll()
    }

    @Get("/{id}")
    fun one(@PathVariable id: String): Product {
        return productService.fetch(id)
    }

    @Put("/{id}")
    fun replace(@PathVariable id: String, @Body request: ProductUpdateInput): Product {
        return productService.replace(id, request)
    }

    @Patch("/{id}")
    fun update(@PathVariable id: String, @Body request: ProductPatchInput): Product {
        return productService.update(id, request)
    }

}