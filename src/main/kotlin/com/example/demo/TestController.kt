package com.example.demo

import feign.Feign
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
class TestController(webClient: WebClient.Builder) {

    val reactiveFeignClient: ReactiveFeignClient = WebReactiveFeign.builder<ReactiveFeignClient>(webClient)
        .target(ReactiveFeignClient::class.java, "http://localhost:8080")

    val feignClient: FeignClient = Feign.builder()
        .target(FeignClient::class.java, "http://localhost:8080")


    @GetMapping("/api/test/{id}")
    @ResponseBody
    fun entryPoint(@PathVariable id: String) = feignClient.getRequest(
        id = id,
        params = FeignClient.Query(param = "hello")
    )

    @GetMapping("/api/test/{id}/response")
    @ResponseBody
    fun response(@PathVariable id: String) = reactiveFeignClient.getRequest(
        id = id,
        params = ReactiveFeignClient.Query(param = "world")
    )

    @GetMapping("/api/test/{id}/response/reactive")
    @ResponseBody
    fun reactiveResponse(@PathVariable id: String) = "ok"

}

interface FeignClient {

    @RequestLine("GET /api/test/{id}/response")
    fun getRequest(@Param("id") id: String, @QueryMap params: Query): String

    data class Query(val param: String)
}

interface ReactiveFeignClient {

    @RequestLine("GET /api/test/{id}/response/reactive")
    fun getRequest(@Param("id") id: String, @QueryMap params: Query): Mono<String>

    data class Query(val param: String)
}