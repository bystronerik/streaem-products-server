package com.streaem.products.service

import com.streaem.products.entity.Product
import com.streaem.products.model.ProductFilters
import jakarta.inject.Singleton

@Singleton
class CategoryService(private val dataStore: DataStore) {

    fun fetchAll(): List<String> {
        return dataStore.findAllCategories()
    }

    fun fetchProducts(id: String, filters: ProductFilters): List<Product> {
        return dataStore.findAllProductsByCategory(id)
    }

}