package com.streaem.products.listener

import com.streaem.products.service.ProductService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton

@Singleton
class StartupListener(private val productService: ProductService) : ApplicationEventListener<ServerStartupEvent> {

    private val log = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ServerStartupEvent) {
        log.info { "Running startup tasks" }
        productService.importProducts()
    }

}
