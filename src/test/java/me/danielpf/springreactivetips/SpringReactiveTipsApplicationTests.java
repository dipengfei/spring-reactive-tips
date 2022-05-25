package me.danielpf.springreactivetips;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class SpringReactiveTipsApplicationTests {
    @Test
    void testPossibleInterference() {
        var list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        var stream = list.stream();
        list.add("d");
        stream.forEach(System.out::println);
    }

    @Test
    void testInterference() {
        var list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        list.stream().peek(s -> list.add("d")).forEach(System.out::println);
    }

    @Test
    void testNonInterference() {
        var set = ConcurrentHashMap.<String>newKeySet();
        set.addAll(Arrays.asList("A", "B", "C"));
        set.stream().peek(s -> set.add("D")).forEach(System.out::println);
    }

    /*
     * The notable exception to this are streams whose sources are concurrent collections,
     * which are specifically designed to handle concurrent modification.
     * Concurrent stream sources are those whose Spliterator reports the CONCURRENT characteristic.
     * */
    @Test
    void testSpliterator() {
        var set = ConcurrentHashMap.<String>newKeySet();
        System.out.println(set.spliterator().hasCharacteristics(Spliterator.CONCURRENT));

        var list = new ArrayList<String>();
        System.out.println(list.spliterator().hasCharacteristics(Spliterator.CONCURRENT));

    }

    /*
     * An intermediate operation is short-circuiting if,
     * when presented with infinite input, it may produce a finite stream as a result.
     * A terminal operation is short-circuiting if,
     * when presented with infinite input, it may terminate in finite time.
     * */
    @Test
    void testShortCircuiting() {
        new Random().ints().limit(1).forEach(System.out::println);
        new Random().ints().peek(System.out::println).findFirst();
    }

    @Test
    void testLottery7From35() {
        IntStream.range(0, 5)
                 .boxed()
                 .map(n -> new Random().ints(1, 36)
                                       .distinct()
                                       .limit(7)
                                       .sorted()
                                       .mapToObj(i -> String.format("%02d", i))
                                       .collect(Collectors.joining("\t")))
                 .forEach(System.out::println);
    }

    @Test
    void testMultiplicationTables() {
        IntStream.rangeClosed(1, 9)
                 .boxed()
                 .flatMap(m -> IntStream.rangeClosed(1, m)
                                        .boxed()
                                        .map(n -> String.format("%d * %d = %d%s", m, n, m * n, m.equals(n) ? "\n" : "\t")))
                 .forEach(System.out::print);
    }

    @Test
    void multiplicationTablesRunnerWithFlux() {
        Flux.range(1, 9)
            .concatMap(m -> Flux.range(1, m)
                                .map(n -> String.format("%d * %d = %d%s", m, n, m * n, m.equals(n) ? "\n" : "\t")))
            .subscribe(System.out::print);
    }

}
