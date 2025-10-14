package com.streaem.products.service

import com.streaem.products.entity.Product
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldBeEmpty
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.math.BigDecimal

@MicronautTest
class DataStoreTest : StringSpec({

    lateinit var dataStore: DataStore

    beforeEach {
        // Create a fresh DataStore instance for each test
        dataStore = DataStore()
    }

    "saveProduct should save a new product" {
        // Given
        val product = Product(
            id = "test-id-1",
            name = "Test Product",
            category = "Electronics",
            description = "Test Description",
            price = BigDecimal("99.99")
        )

        // When
        dataStore.saveProduct(product)

        // Then
        val retrieved = dataStore.findProductById("test-id-1")
        retrieved shouldNotBe null
        retrieved shouldBe product
    }

    "saveProduct should update an existing product" {
        // Given
        val originalProduct = Product(
            id = "test-id-2",
            name = "Original Product",
            category = "Electronics",
            description = "Original Description",
            price = BigDecimal("99.99")
        )
        val updatedProduct = Product(
            id = "test-id-2",
            name = "Updated Product",
            category = "Electronics",
            description = "Updated Description",
            price = BigDecimal("149.99")
        )

        // When
        dataStore.saveProduct(originalProduct)
        dataStore.saveProduct(updatedProduct)

        // Then
        val retrieved = dataStore.findProductById("test-id-2")
        retrieved shouldNotBe null
        retrieved shouldBe updatedProduct
    }

    "saveProduct should handle null category" {
        // Given
        val product = Product(
            id = "test-id-3",
            name = "Product without category",
            category = null,
            description = "No category",
            price = BigDecimal("50.00")
        )

        // When
        dataStore.saveProduct(product)

        // Then
        val retrieved = dataStore.findProductById("test-id-3")
        retrieved shouldNotBe null
        retrieved shouldBe product
        dataStore.findAllCategories().shouldBeEmpty()
    }

    "saveProduct should update category index when product category changes" {
        // Given
        val originalProduct = Product(
            id = "test-id-4",
            name = "Product",
            category = "Electronics",
            description = "Description",
            price = BigDecimal("99.99")
        )
        val updatedProduct = Product(
            id = "test-id-4",
            name = "Product",
            category = "Clothing",
            description = "Description",
            price = BigDecimal("99.99")
        )

        // When
        dataStore.saveProduct(originalProduct)
        dataStore.saveProduct(updatedProduct)

        // Then
        val electronicsProducts = dataStore.findAllProductsByCategory("Electronics")
        val clothingProducts = dataStore.findAllProductsByCategory("Clothing")

        electronicsProducts.size shouldBe 0

        clothingProducts.size shouldBe 1
        clothingProducts.shouldContain(updatedProduct)

        val retrieved = dataStore.findProductById("test-id-4")
        retrieved shouldBe updatedProduct
    }

    "saveProduct should handle category change from null to category" {
        // Given
        val originalProduct = Product(
            id = "test-id-5",
            name = "Product",
            category = null,
            description = "Description",
            price = BigDecimal("99.99")
        )
        val updatedProduct = Product(
            id = "test-id-5",
            name = "Product",
            category = "Electronics",
            description = "Description",
            price = BigDecimal("99.99")
        )

        // When
        dataStore.saveProduct(originalProduct)
        dataStore.saveProduct(updatedProduct)

        // Then
        val electronicsProducts = dataStore.findAllProductsByCategory("Electronics")

        electronicsProducts.size shouldBe 1
        electronicsProducts.shouldContain(updatedProduct)

        val retrieved = dataStore.findProductById("test-id-5")
        retrieved shouldBe updatedProduct
    }

    "saveProduct should handle category change from category to null" {
        // Given
        val originalProduct = Product(
            id = "test-id-6",
            name = "Product",
            category = "Electronics",
            description = "Description",
            price = BigDecimal("99.99")
        )
        val updatedProduct = Product(
            id = "test-id-6",
            name = "Product",
            category = null,
            description = "Description",
            price = BigDecimal("99.99")
        )

        // When
        dataStore.saveProduct(originalProduct)
        dataStore.saveProduct(updatedProduct)

        // Then
        val retrieved = dataStore.findProductById("test-id-6")
        retrieved shouldBe updatedProduct
    }

    "findAllProducts should return all saved products" {
        // Given
        val product1 = Product("1", "Product 1", "Electronics", "Description 1", BigDecimal("99.99"))
        val product2 = Product("2", "Product 2", "Clothing", "Description 2", BigDecimal("49.99"))
        val product3 = Product("3", "Product 3", null, "Description 3", BigDecimal("29.99"))

        dataStore.saveProduct(product1)
        dataStore.saveProduct(product2)
        dataStore.saveProduct(product3)

        // When
        val allProducts = dataStore.findAllProducts()

        // Then
        allProducts shouldHaveSize(3)
        allProducts shouldContain(product1)
        allProducts shouldContain(product2)
        allProducts shouldContain(product3)
    }

    "findAllProducts should return empty list when no products exist" {
        // When
        val allProducts = dataStore.findAllProducts()

        // Then
        allProducts.shouldBeEmpty()
    }

    "findAllCategories should return all category names" {
        // Given
        val product1 = Product("1", "Product 1", "Electronics", "Description 1", BigDecimal("99.99"))
        val product2 = Product("2", "Product 2", "Clothing", "Description 2", BigDecimal("49.99"))
        val product3 = Product("3", "Product 3", "Electronics", "Description 3", BigDecimal("29.99"))

        dataStore.saveProduct(product1)
        dataStore.saveProduct(product2)
        dataStore.saveProduct(product3)

        // When
        val categories = dataStore.findAllCategories()

        // Then
        categories shouldHaveSize(2)
        categories shouldContain("Electronics")
        categories shouldContain("Clothing")
    }

    "findAllCategories should return empty list when no categories exist" {
        // When
        val categories = dataStore.findAllCategories()

        // Then
        categories.shouldBeEmpty()
    }

    "findProductById should return product when it exists" {
        // Given
        val product = Product("test-id-7", "Test Product", "Electronics", "Description", BigDecimal("99.99"))
        dataStore.saveProduct(product)

        // When
        val retrieved = dataStore.findProductById("test-id-7")

        // Then
        retrieved shouldNotBe null
        retrieved shouldBe product
    }

    "findProductById should return null when product does not exist" {
        // When
        val retrieved = dataStore.findProductById("non-existent-id")

        // Then
        retrieved shouldBe null
    }

    "findAllProductsByCategory should return products for existing category" {
        // Given
        val product1 = Product("1", "Product 1", "Electronics", "Description 1", BigDecimal("99.99"))
        val product2 = Product("2", "Product 2", "Electronics", "Description 2", BigDecimal("149.99"))
        val product3 = Product("3", "Product 3", "Clothing", "Description 3", BigDecimal("49.99"))

        dataStore.saveProduct(product1)
        dataStore.saveProduct(product2)
        dataStore.saveProduct(product3)

        // When
        val electronicsProducts = dataStore.findAllProductsByCategory("Electronics")

        // Then
        electronicsProducts shouldHaveSize(2)
        electronicsProducts shouldContain(product1)
        electronicsProducts shouldContain(product2)
    }

    "findAllProductsByCategory should return empty list for non-existing category" {
        // When
        val products = dataStore.findAllProductsByCategory("NonExistentCategory")

        // Then
        products.shouldBeEmpty()
    }

    "saveProduct should handle multiple products in same category" {
        // Given
        val product1 = Product("1", "Laptop", "Electronics", "Gaming laptop", BigDecimal("999.99"))
        val product2 = Product("2", "Phone", "Electronics", "Smartphone", BigDecimal("699.99"))
        val product3 = Product("3", "Tablet", "Electronics", "iPad", BigDecimal("499.99"))

        // When
        dataStore.saveProduct(product1)
        dataStore.saveProduct(product2)
        dataStore.saveProduct(product3)

        // Then
        val electronicsProducts = dataStore.findAllProductsByCategory("Electronics")
        electronicsProducts shouldHaveSize(3)
        electronicsProducts shouldContain(product1)
        electronicsProducts shouldContain(product2)
        electronicsProducts shouldContain(product3)
    }

})
