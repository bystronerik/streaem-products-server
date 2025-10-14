package com.streaem.products.exception

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Controller

@Controller
class GlobalExceptionHandler {

    @Error(status = HttpStatus.NOT_FOUND)
    fun handleProductNotFound(exception: ProductNotFoundException): HttpResponse<String> {
        return HttpResponse.notFound(exception.message)
    }
}
