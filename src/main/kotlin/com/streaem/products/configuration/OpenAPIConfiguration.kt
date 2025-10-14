package com.streaem.products.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
    info = Info(
        title = "Streaem Products API",
        version = "1.0",
        description = "Simple products API for streaem demo application",
        license = License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
        contact = Contact(name = "John Snow", email = "snow@email.com", url = "https://mywebsite.com")
    )
)
class OpenAPIConfiguration