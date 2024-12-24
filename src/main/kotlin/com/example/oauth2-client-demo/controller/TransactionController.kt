package com.example.`oauth2-client-demo`.controller

import com.example.`oauth2-client-demo`.model.TransactionStatus
import com.example.`oauth2-client-demo`.service.SwiftTransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class TransactionController(val swiftTransactionService: SwiftTransactionService) {
    @GetMapping("/transactions/{uetr}")
    fun getTransactionStatus(@PathVariable uetr: String): ResponseEntity<TransactionStatus> {
        // sample uetr: d2ecb184-b622-41e9-a2a3-2a2ae2dbcce4
        val status = swiftTransactionService.getTransactionStatus(uetr)
        return ResponseEntity.ok(TransactionStatus(status))
    }
}