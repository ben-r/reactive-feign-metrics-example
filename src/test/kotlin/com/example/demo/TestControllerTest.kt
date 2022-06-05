package com.example.demo

import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestControllerTest(
    @Autowired val webClient: WebTestClient,
    @Autowired val meterRegistry: MeterRegistry,
) {

    @Test
    fun metricsTest() {
        webClient.get()
            .uri("/api/test/1")
            .exchange()

        meterRegistry.meters
            .filter { it.id.name == "http.client.requests" || it.id.name == "http.server.requests" }
            .forEach { println(it.id) }
    }
}