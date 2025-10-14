package com.streaem.products.decoder

import com.jsoniter.JsonIterator
import com.jsoniter.spi.Decoder
import com.streaem.products.entity.Product
import java.math.BigDecimal
import java.util.UUID

class ProductDecoder : Decoder {

    override fun decode(iter: JsonIterator): Product {
        val id = UUID.randomUUID().toString()
        var name = ""
        var category: String? = null
        var description: String? = null
        var price: BigDecimal? = null

        var field = iter.readObject()
        while (field != null) {
            when (field) {
                "name" -> name = iter.readString()
                "category" -> category = iter.readString()
                "description" -> description = iter.readString()
                "price" -> {
                    val priceStr = iter.readString()
                    if (priceStr.isNotBlank()) {
                        price = BigDecimal(priceStr)
                    }
                }
                else -> iter.skip()
            }
            field = iter.readObject()
        }

        return Product(id, name, category, description, price)
    }

}