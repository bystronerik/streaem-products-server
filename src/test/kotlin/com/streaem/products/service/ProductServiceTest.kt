package com.streaem.products.service

import com.jsoniter.JsonIterator
import com.streaem.products.entity.Product
import com.streaem.products.exception.ProductNotFoundException
import com.streaem.products.model.ProductPatchInput
import com.streaem.products.model.ProductUpdateInput
import com.streaem.products.properties.AppProperties
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.BlockingHttpClient
import io.mockk.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.math.BigDecimal

@MicronautTest
class ProductServiceTest : StringSpec({

    val mockDataStore = mockk<DataStore>()
    val mockHttpClient = mockk<HttpClient>()
    val mockAppProperties = mockk<AppProperties>()
    val mockJsonIterator = mockk<JsonIterator>()
    val mockBlockingHttpClient = mockk<BlockingHttpClient>()
    
    val productService = ProductService(mockDataStore, mockHttpClient, mockAppProperties)

    beforeEach {
        clearAllMocks()
    }

    "importProducts should successfully import products from HTTP source" {
        // Given
        val mockResponse = """[{"name":"Test Product","category":"Electronics","description":"Test Description","price":"99.99"}]"""
        val expectedProduct = Product(
            id = "any-uuid",
            name = "Test Product",
            category = "Electronics",
            description = "Test Description",
            price = BigDecimal("99.99")
        )

        every { mockAppProperties.productsSource } returns "http://test.com/products"
        every { mockHttpClient.toBlocking() } returns mockBlockingHttpClient
        every { mockBlockingHttpClient.retrieve<String>("http://test.com/products", String::class.java) } returns mockResponse
        every { mockDataStore.saveProduct(any()) } just Runs

        // Mock JsonIterator behavior
        mockkStatic(JsonIterator::class)
        every { JsonIterator.parse(mockResponse) } returns mockJsonIterator
        every { mockJsonIterator.readArray() } returnsMany listOf(true, false) // First call returns true, second false
        every { mockJsonIterator.read(Product::class.java) } returns expectedProduct

        // When
        productService.importProducts()

        // Then
        verify { mockDataStore.saveProduct(expectedProduct) }
    }

    "fetchAll should return all products from dataStore" {
        // Given
        val expectedProducts = listOf(
            Product("1", "Product 1", "Category 1", "Description 1", BigDecimal("10.00")),
            Product("2", "Product 2", "Category 2", "Description 2", BigDecimal("20.00"))
        )

        every { mockDataStore.findAllProducts() } returns expectedProducts

        // When
        val result = productService.fetchAll()

        // Then
        result shouldBe expectedProducts
        verify { mockDataStore.findAllProducts() }
    }

    "fetch should return product when it exists" {
        // Given
        val productId = "test-id"
        val expectedProduct = Product(productId, "Test Product", "Electronics", "Test Description", BigDecimal("99.99"))

        every { mockDataStore.findProductById(productId) } returns expectedProduct

        // When
        val result = productService.fetch(productId)

        // Then
        result shouldBe expectedProduct
        verify { mockDataStore.findProductById(productId) }
    }

    "fetch should throw ProductNotFoundException when product does not exist" {
        // Given
        val productId = "non-existent-id"

        every { mockDataStore.findProductById(productId) } returns null

        // When & Then
        val exception = shouldThrow<ProductNotFoundException> {
            productService.fetch(productId)
        }

        exception.message shouldBe "Product with id 'non-existent-id' not found"
        verify { mockDataStore.findProductById(productId) }
    }

    "replace should update product with provided data" {
        // Given
        val productId = "test-id"
        val updateInput = ProductUpdateInput(
            name = "Updated Product",
            category = "Updated Category",
            description = "Updated Description",
            price = BigDecimal("149.99")
        )
        val expectedProduct = Product(
            id = productId,
            name = "Updated Product",
            category = "Updated Category",
            description = "Updated Description",
            price = BigDecimal("149.99")
        )

        every { mockDataStore.saveProduct(any()) } just Runs

        // When
        val result = productService.replace(productId, updateInput)

        // Then
        result shouldBe expectedProduct
        verify { mockDataStore.saveProduct(expectedProduct) }
    }

    "replace should handle null values in update input" {
        // Given
        val productId = "test-id"
        val updateInput = ProductUpdateInput(
            name = "Updated Product",
            category = null,
            description = null,
            price = null
        )
        val expectedProduct = Product(
            id = productId,
            name = "Updated Product",
            category = null,
            description = null,
            price = null
        )

        every { mockDataStore.saveProduct(any()) } just Runs

        // When
        val result = productService.replace(productId, updateInput)

        // Then
        result shouldBe expectedProduct
        verify { mockDataStore.saveProduct(expectedProduct) }
    }

    "update should merge partial data with existing product" {
        // Given
        val productId = "test-id"
        val existingProduct = Product(
            id = productId,
            name = "Original Name",
            category = "Original Category",
            description = "Original Description",
            price = BigDecimal("99.99")
        )
        val patchInput = ProductPatchInput(
            name = "Updated Name",
            category = null, // Keep original
            description = "Updated Description",
            price = null // Keep original
        )
        val expectedProduct = Product(
            id = productId,
            name = "Updated Name",
            category = "Original Category", // Kept from original
            description = "Updated Description",
            price = BigDecimal("99.99") // Kept from original
        )

        every { mockDataStore.findProductById(productId) } returns existingProduct
        every { mockDataStore.saveProduct(any()) } just Runs

        // When
        val result = productService.update(productId, patchInput)

        // Then
        result shouldBe expectedProduct
        verify { mockDataStore.findProductById(productId) }
        verify { mockDataStore.saveProduct(expectedProduct) }
    }

    "update should throw ProductNotFoundException when product does not exist" {
        // Given
        val productId = "non-existent-id"
        val patchInput = ProductPatchInput(name = "Updated Name")

        every { mockDataStore.findProductById(productId) } returns null

        // When & Then
        val exception = shouldThrow<ProductNotFoundException> {
            productService.update(productId, patchInput)
        }

        exception.message shouldBe "Product with id 'non-existent-id' not found"
        verify { mockDataStore.findProductById(productId) }
        verify(exactly = 0) { mockDataStore.saveProduct(any()) }
    }

})
