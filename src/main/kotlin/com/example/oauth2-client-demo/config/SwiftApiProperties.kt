package com.example.`oauth2-client-demo`.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "swift-api")
data class SwiftApiProperties(
    var certPath: String,
    var privateKeyPath: String,
    var issuer: String,
    var subject: String,
    var audience: String,
    var consumerKey: String,
    var consumerSecret: String,
    var scope: String,
    var resourceUrl: String,
    var grantType: String,

    )