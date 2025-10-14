package com.streaem.products.service

import com.streaem.products.entity.Product
import com.streaem.products.model.ProductFilters
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.math.BigDecimal

@MicronautTest
class CategoryServiceTest : StringSpec({

    val mockDataStore = mockk<DataStore>()
    
    val categoryService = CategoryService(mockDataStore)

    beforeEach {
        clearAllMocks()
    }

    "fetchAll should return all categories from dataStore" {
        // Given
        val expectedCategories = listOf("Electronics", "Clothing", "Books", "Home & Garden")

        every { mockDataStore.findAllCategories() } returns expectedCategories

        // When
        val result = categoryService.fetchAll()

        // Then
        result shouldBe expectedCategories
        verify { mockDataStore.findAllCategories() }
    }

    "fetchAll should return empty list when no categories exist" {
        // Given
        every { mockDataStore.findAllCategories() } returns emptyList()

        // When
        val result = categoryService.fetchAll()

        // Then
        result shouldBe emptyList()
        verify { mockDataStore.findAllCategories() }
    }

    "fetchProducts should return products for existing category" {
        // Given
        val categoryId = "Electronics"
        val filters = ProductFilters(instock = null)
        val expectedProducts = listOf(
            Product("1", "Laptop", "Electronics", "Gaming laptop", BigDecimal("999.99")),
            Product("2", "Phone", "Electronics", "Smartphone", BigDecimal("699.99"))
        )

        every { mockDataStore.findAllProductsByCategory(categoryId) } returns expectedProducts

        // When
        val result = categoryService.fetchProducts(categoryId, filters)

        // Then
        result shouldBe expectedProducts
        verify { mockDataStore.findAllProductsByCategory(categoryId) }
    }

    "fetchProducts should return empty list for non-existing category" {
        // Given
        val categoryId = "NonExistentCategory"
        val filters = ProductFilters(instock = null)

        every { mockDataStore.findAllProductsByCategory(categoryId) } returns emptyList()

        // When
        val result = categoryService.fetchProducts(categoryId, filters)

        // Then
        result shouldBe emptyList()
        verify { mockDataStore.findAllProductsByCategory(categoryId) }
    }

    "fetchProducts should handle filters parameter" {
        // Given
        val categoryId = "Electronics"
        val filters = ProductFilters(instock = true)
        val expectedProducts = listOf(
            Product("1", "Laptop", "Electronics", "Gaming laptop", BigDecimal("999.99"))
        )

        every { mockDataStore.findAllProductsByCategory(categoryId) } returns expectedProducts

        // When
        val result = categoryService.fetchProducts(categoryId, filters)

        // Then
        result shouldBe expectedProducts
        verify { mockDataStore.findAllProductsByCategory(categoryId) }
    }

    "fetchProducts should handle null filters" {
        // Given
        val categoryId = "Electronics"
        val filters = ProductFilters(instock = null)
        val expectedProducts = listOf(
            Product("1", "Laptop", "Electronics", "Gaming laptop", BigDecimal("999.99"))
        )

        every { mockDataStore.findAllProductsByCategory(categoryId) } returns expectedProducts

        // When
        val result = categoryService.fetchProducts(categoryId, filters)

        // Then
        result shouldBe expectedProducts
        verify { mockDataStore.findAllProductsByCategory(categoryId) }
    }

})
