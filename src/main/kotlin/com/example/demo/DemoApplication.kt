package com.example.demo

import feign.Param
import feign.QueryMap
import feign.RequestLine
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactivefeign.webclient.WebReactiveFeign
import reactor.core.publisher.Mono

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@RestController
class TestController(webClientBuilder: WebClient.Builder, val testWebClient: TestWebClient) {

    val reactiveFeignClient: ReactiveFeignClient = WebReactiveFeign.builder<ReactiveFeignClient>(webClientBuilder)
        .target(ReactiveFeignClient::class.java, "http://localhost:8080")

    @GetMapping("/api/test/{id}")
    @ResponseBody
    fun entryPoint(@PathVariable id: String) = testWebClient.getRequest(id)

    @GetMapping("/api/test/{id}/webclient")
    @ResponseBody
    fun response(@PathVariable id: String) = reactiveFeignClient.getRequest(
        id = id,
        params = ReactiveFeignClient.Query(param = "world")
    )

    @GetMapping("/api/test/{id}/feign")
    @ResponseBody
    fun reactiveResponse(@PathVariable id: String) = "ok"

}

@Component
class TestWebClient(webClientBuilder: WebClient.Builder) {

    private val webClient = webClientBuilder
        .baseUrl("http://localhost:8080")
        .build()

    fun getRequest(id: String): Mono<String> {
        val query = TestWebClient.Query(param = "hello world", nullableParam = null)
        return webClient.get()
            .uri("/api/test/{id}/webclient") {
                // just to show how to append a query
                it.queryParams(query.toMultiValueMap()).build(id)
            }
            .retrieve()
            .bodyToMono(String::class.java)
    }

    data class Query(val param: String, val nullableParam: Int?) {

        fun toMultiValueMap(): MultiValueMap<String, String> = buildMap<String, List<String>> {
            put("param", listOf(param))
            nullableParam?.let { put("nullableParam", listOf(nullableParam.toString())) }
        }.let { map -> MultiValueMapAdapter(map) }
    }
}

interface ReactiveFeignClient {

    @RequestLine("GET /api/test/{id}/feign")
    fun getRequest(@Param("id") id: String, @QueryMap params: Query): Mono<String>

    data class Query(val param: String)
}
