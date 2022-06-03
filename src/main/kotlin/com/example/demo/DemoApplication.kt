package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableFeignClients
@EnableWebFlux
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
