package me.danielpf.springreactivetips;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

@SpringBootApplication
public class SpringReactiveTipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringReactiveTipsApplication.class, args);
    }

    @Bean
    ApplicationRunner multiplicationTablesRunnerWithStream() {
        return args ->
                IntStream.rangeClosed(1, 9)
                         .boxed()
                         .flatMap(m -> IntStream.rangeClosed(1, m)
                                                .boxed()
                                                .map(n -> String.format("%d * %d = %d%s", m, n, m * n, m.equals(n) ? "\n" : "\t")))
                         .forEach(System.out::print);
    }

    @Bean
    ApplicationRunner multiplicationTablesRunnerWithFlux() {
        return args ->
                Flux.range(1, 9)
                    .concatMap(m -> Flux.range(1, m)
                                        .map(n -> String.format("%d * %d = %d%s", m, n, m * n, m.equals(n) ? "\n" : "\t")))
                    .subscribe(System.out::print);

    }

}
