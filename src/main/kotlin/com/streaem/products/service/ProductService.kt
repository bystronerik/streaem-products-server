package com.streaem.products.service

import com.jsoniter.JsonIterator
import com.jsoniter.spi.JsoniterSpi
import com.streaem.products.properties.AppProperties
import com.streaem.products.decoder.ProductDecoder
import com.streaem.products.entity.Product
import com.streaem.products.exception.ProductNotFoundException
import com.streaem.products.model.ProductPatchInput
import com.streaem.products.model.ProductUpdateInput
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.http.client.HttpClient
import jakarta.inject.Singleton

@Singleton
class ProductService(
    private val dataStore: DataStore,
    private val httpClient: HttpClient,
    private val properties: AppProperties
) {

    private val log = KotlinLogging.logger {}

    init {
        JsoniterSpi.registerTypeDecoder(Product::class.java, ProductDecoder())
    }

    fun importProducts() {
        log.info { "Importing products..." }

        log.info { "Fetching products feed..." }

        val response = httpClient.toBlocking().retrieve(properties.productsSource, String::class.java)

        log.info { "Parsing products..." }

        val iter = JsonIterator.parse(response)

        while (iter.readArray()) {
            try {
                val product = iter.read(Product::class.java)
                dataStore.saveProduct(product)
            } catch (e: Exception) {
                log.error(e) { "Failed to parse product" }
            }
        }

        log.info { "Products imported successfully" }
    }

    fun fetchAll(): List<Product> {
        return dataStore.findAllProducts()
    }

    fun fetch(id: String): Product {
        return dataStore.findProductById(id) ?: throw ProductNotFoundException(id)
    }

    fun replace(id: String, data: ProductUpdateInput): Product {
        val entity = Product(
            id = id,
            name = data.name,
            category = data.category,
            description = data.description,
            price = data.price
        )

        dataStore.saveProduct(entity)

        return entity
    }

    fun update(id: String, data: ProductPatchInput): Product {
        val existingEntity = dataStore.findProductById(id) ?: throw ProductNotFoundException(id)

        val entity = Product(
            id = id,
            name = data.name ?: existingEntity.name,
            category = data.category ?: existingEntity.category,
            description = data.description ?: existingEntity.description,
            price = data.price ?: existingEntity.price
        )

        dataStore.saveProduct(entity)

        return entity
    }

}