package com.streaem.products.controller

import com.streaem.products.entity.Product
import com.streaem.products.model.ProductFilters
import com.streaem.products.service.CategoryService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @Get("/")
    fun all(): List<String> {
        return categoryService.fetchAll()
    }

    @Get("/{id}/products{?filters*}")
    fun one(@PathVariable id: String, filters: ProductFilters): List<Product> {
        return categoryService.fetchProducts(id, filters)
    }

}