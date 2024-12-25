package com.example.`oauth2-client-demo`.service

import com.example.`oauth2-client-demo`.config.SwiftApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Service
@EnableConfigurationProperties(SwiftApiProperties::class)
class SwiftTransactionService(
        private val webClient: WebClient,
        private val swiftApiProperties: SwiftApiProperties
) {

    fun getTransactionStatus(uetr: String): String {

        val swiftResponse = webClient
                .get()
                .uri(URI(swiftApiProperties.resourceUrl.format(uetr)))
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .block() ?: emptyMap()
        val status = swiftResponse["transaction_status"] as String
        return status
    }
}