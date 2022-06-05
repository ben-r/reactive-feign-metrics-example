# reactive-feign-metrics-example

1. Start application `./gradlew bootrun`
2. Trigger request `GET localhost:8080/api/test/1?example=value`
3. Find metrics under `http://localhost:8080/actuator/prometheus`
   1. Only URIs from the reactive client are expanded, therefore creating a new metric for every new value (cardinality explosion)