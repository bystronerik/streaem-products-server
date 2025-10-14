package com.streaem.products.service

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import com.streaem.products.entity.Product
import jakarta.inject.Singleton
import net.openhft.chronicle.map.ChronicleMap
import org.objenesis.strategy.StdInstantiatorStrategy
import java.math.BigDecimal

@Singleton
class DataStore() {

    private val kryo = Kryo().apply {
        instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
        register(Product::class.java)
        register(BigDecimal::class.java)
    }

    private val products = ChronicleMap.of(String::class.java, ByteArray::class.java)
        .averageKey("Average product title")
        .averageValueSize(256.0)
        .entries(1000)
        .create()

    private val categoryIndex = ChronicleMap.of(String::class.java, MutableSet::class.java as Class<MutableSet<String>>)
        .averageKey("Category")
        .entries(1000)
        .averageValueSize(256.0)
        .create()

    fun saveProduct(entity: Product) {
        val existing = findProductById(entity.id)

        Output(1024, -1).use {
            kryo.writeObject(it, entity)
            products[entity.id] = it.buffer
        }

        existing?.let { existing ->
            if (existing.category != entity.category) {
                categoryIndex[existing.category]?.remove(entity.id)
            }
        }

        entity.category?.let {
            val category = categoryIndex.getOrDefault(it, mutableSetOf())
            category.add(entity.id)
            categoryIndex[it] = category
        }
    }

    fun findAllProducts(): List<Product> {
        return products.values.map {
            Input(it).use {
                kryo.readObject(it, Product::class.java)
            }
        }.toList()
    }

    fun findAllCategories(): List<String> {
        return categoryIndex.keys.toList()
    }

    fun findAllProductsByCategory(category: String): List<Product> {
        return categoryIndex[category]?.mapNotNull {
            findProductById(it)
        }?.toList() ?: emptyList()
    }

    fun findProductById(id: String): Product? {
        return products[id]?.let {
            Input(it).use {
                kryo.readObject(it, Product::class.java)
            }
        }
    }

}