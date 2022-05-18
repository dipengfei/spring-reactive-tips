package me.danielpf.springreactivetips;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
@Slf4j
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

    @Bean
    ApplicationRunner lottery7From35() {
        return args -> IntStream.range(0, 5)
                                .boxed()
                                .map(n -> new Random().ints(1, 36)
                                                      .distinct()
                                                      .limit(7)
                                                      .sorted()
                                                      .mapToObj(String::valueOf)
                                                      .collect(Collectors.joining("\t")))
                                .forEach(System.out::println);
    }

    @Bean
    ApplicationRunner nonInterferenceRunner() {
        return args -> {
            var list = new ArrayList<>(Arrays.asList("a", "b", "c"));

            try {
                list.stream().peek(s -> list.add("d")).forEach(System.out::println);
            } catch (Exception e) {
                System.out.println("ConcurrentModificationException occurred here");
            }

            var set = ConcurrentHashMap.newKeySet();
            set.addAll(Arrays.asList("A", "B", "C"));
            set.stream().peek(s -> set.add("D")).forEach(System.out::println);

        };
    }

}
