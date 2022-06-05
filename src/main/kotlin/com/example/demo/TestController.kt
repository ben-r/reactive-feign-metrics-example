package com.example.demo

import feign.Param
import feign.QueryMap
import feign.RequestLine
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactivefeign.webclient.WebReactiveFeign
import reactor.core.publisher.Mono

@RestController
class TestController(webClientBuilder: WebClient.Builder) {

    val webClient = webClientBuilder
        .baseUrl("http://localhost:8080")
//            .filter(MetricsWebClientFilterFunction(Metrics.globalRegistry, ))
        .build()

    val reactiveFeignClient: ReactiveFeignClient = WebReactiveFeign.builder<ReactiveFeignClient>(webClientBuilder)
        .target(ReactiveFeignClient::class.java, "http://localhost:8080")

    @GetMapping("/api/test/{id}")
    @ResponseBody
    fun entryPoint(@PathVariable id: String) = webClient
        .get()
        .uri { uriBuilder ->
            uriBuilder
                .path("/api/test/{id}/webclient")
                .queryParam("param", "value")
                .build(id)
        }
        .retrieve()
        .bodyToMono(String::class.java)

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

interface ReactiveFeignClient {

    @RequestLine("GET /api/test/{id}/feign")
    fun getRequest(@Param("id") id: String, @QueryMap params: Query): Mono<String>

    data class Query(val param: String)
}