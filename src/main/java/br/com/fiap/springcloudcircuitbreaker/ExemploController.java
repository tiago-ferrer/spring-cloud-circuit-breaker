package br.com.fiap.springcloudcircuitbreaker;

import java.util.Map;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ExemploController {

    private final RestClient restClient = RestClient.create();

    private CircuitBreakerFactory circuitBreakerFactory;

    public ExemploController(CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/get")
    public Object getHttpBin(){
        return this.restClient.get().uri("https://httpbin.org/get").retrieve().toEntity(Object.class).getBody();
    }

    @GetMapping("/no-cb/delayed")
    public Object getHttpBinDelayedWithoutCB(){
        return getWithDelay(4);
    }


    @GetMapping("/delayed/{delay}")
    public Object getHttpBinDelayedWithCB(@PathVariable("delay") int delay){
        return circuitBreakerFactory.create("httpbinDelay").run(() -> this.getWithDelay(delay), t -> Map.of(
                "origin","origemMockada",
                "url","https://httpbin.org/delay/"+delay));
    }

    private Object getWithDelay(int delay) {
        return this.restClient.get().uri("https://httpbin.org/delay/"+delay).retrieve().toEntity(Object.class).getBody();
    }




}
