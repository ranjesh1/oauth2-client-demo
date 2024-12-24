package com.example.`oauth2-client-demo`.util

import com.example.`oauth2-client-demo`.config.SwiftApiProperties
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.bouncycastle.asn1.ASN1StreamParser
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.RSAPrivateCrtKeySpec
import java.util.*


@Component
@EnableConfigurationProperties(SwiftApiProperties::class)
class JwtTokenUtil(val swiftApiProperties: SwiftApiProperties) {

    fun createJwtToken(): String {
        val certFactory = CertificateFactory.getInstance("X.509")
        val certificateInputStream = loadPublicCertificateAsStream(swiftApiProperties.certPath)
        val certificate: X509Certificate = certFactory.generateCertificate(certificateInputStream) as X509Certificate
        val base64EncodedCert = com.nimbusds.jose.util.Base64.encode(certificate.encoded)

        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .x509CertChain(listOf(base64EncodedCert))
            .build()

        val jti = (1..12)
            .map { "abcdefghijklmnopqrstuvwxyz0123456789".random() }
            .joinToString("")

        val currentTimeMillis = System.currentTimeMillis()
        val issuedAt = currentTimeMillis / 1000
        val expiration = issuedAt + 900 // 15 minutes from issuance

        val claims = JWTClaimsSet.Builder()
            .issuer(swiftApiProperties.issuer)
            .audience(swiftApiProperties.audience)
            .subject(swiftApiProperties.subject)
            .jwtID(jti)
            .expirationTime(Date(expiration * 1000))
            .issueTime(Date(issuedAt * 1000))
            .build()

        val signedJWT = SignedJWT(
            header,
            claims
        )
        val privateKey = loadPrivateKey(swiftApiProperties.privateKeyPath)
        signedJWT.sign(RSASSASigner(privateKey))

        return signedJWT.serialize()
    }

    fun loadPrivateKey(privateKeyPath: String): PrivateKey {
        Security.addProvider(BouncyCastleProvider())
        // Load the private key from the PEM file
        val resource = ClassPathResource(privateKeyPath)
        val keyContentPKCS1 = resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val keyBytes: ByteArray = Base64.getDecoder().decode(keyContentPKCS1)

        val asn1Parser = ASN1StreamParser(keyBytes)
        val asn1Object = asn1Parser.readObject()
        val rsaKey = RSAPrivateKey.getInstance(asn1Object.toASN1Primitive())
        val keySpec = RSAPrivateCrtKeySpec(
            rsaKey.modulus,
            rsaKey.publicExponent,
            rsaKey.privateExponent,
            rsaKey.prime1,
            rsaKey.prime2,
            rsaKey.exponent1,
            rsaKey.exponent2,
            rsaKey.coefficient
        )
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec)
        return privateKey
    }

    fun loadPublicCertificateAsStream(filePath: String): InputStream {
        val resource = ClassPathResource(filePath)
        return resource.inputStream
    }
}