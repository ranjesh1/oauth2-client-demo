package com.example.`oauth2-client-demo`.config

import com.example.`oauth2-client-demo`.util.JwtTokenUtil
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI
import java.time.Instant

@Configuration
@EnableConfigurationProperties(SwiftApiProperties::class)
class OAuth2ClientConfig(
    val clientRegistrationRepository: ClientRegistrationRepository,
    val authorizedClientRepository: OAuth2AuthorizedClientRepository,
    val jwtTokenUtil: JwtTokenUtil,
    val swiftApiProperties: SwiftApiProperties
) {


    @Bean
    fun webClient(authorizedClientManager: OAuth2AuthorizedClientManager): WebClient {
        val oauth2Filter = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2Filter.setDefaultClientRegistrationId("swift-oauth-client")
        return WebClient.builder()
            .apply(oauth2Filter.oauth2Configuration())
            .build()
    }


    @Bean
    fun authorizedClientManager(): OAuth2AuthorizedClientManager {
        val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientRepository
        )


        authorizedClientManager.setAuthorizedClientProvider { context ->
            val clientRegistration = context.clientRegistration
            val jwtBearerToken = jwtTokenUtil.createJwtToken()
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
            formData.add("grant_type", swiftApiProperties.grantType)
            formData.add("assertion", jwtBearerToken)
            formData.add("scope", swiftApiProperties.scope)

            val body = BodyInserters
                .fromFormData(formData)
            WebClient.builder()
                .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                .defaultHeaders { headers ->
                    headers.setBasicAuth(
                        swiftApiProperties.consumerKey,
                        swiftApiProperties.consumerSecret
                    )
                }
                .build()
                .post()
                .uri(URI(clientRegistration.providerDetails.tokenUri))
                .body(body)
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .map { responseMap ->
                    OAuth2AuthorizedClient(
                        clientRegistration, context.principal.name, OAuth2AccessToken(
                            OAuth2AccessToken.TokenType.BEARER,
                            responseMap?.let {
                                it["access_token"] as String
                            },
                            Instant.now(),
                            Instant.now().plusSeconds(1799)
                        )
                    )
                }
                .block()

        }

        return authorizedClientManager
    }


}