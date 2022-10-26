package com.github.peacetrue.spring.data.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.springframework.data.domain.Sort.by;

/**
 * @author peace
 **/
@Slf4j
class SortUtilsTest {

    @Getter
    @Setter
    public static class SortTestBean {
        private Integer column1;
        private Integer column2;
        private Integer column3;

        public SortTestBean(Integer column1, Integer column2, Integer column3) {
            this.column1 = format(column1);
            this.column2 = format(column2);
            this.column3 = format(column3);
        }

        private Integer format(Integer column) {
            return column == 2 ? null : column;
        }

        @Override
        public String toString() {
            return Stream.of(column1, column2, column3)
                    .map(item -> item == null ? 2 : item)
                    .map(String::valueOf)
                    .collect(Collectors.joining("|"));
        }
    }

    @Test
    void comparing() {
        //准备一个排好序的集合
        List<SortTestBean> sortedBeans = stream().collect(Collectors.toList());
        Assertions.assertEquals(3 * 3 * 3, sortedBeans.size());
        log.info("sortedBeans:\n{}", joining(sortedBeans));

        List<SortTestBean> shuffledBeans = new ArrayList<>(sortedBeans);
        Assertions.assertEquals(sortedBeans, shuffledBeans);

        Collections.shuffle(shuffledBeans);
        Assertions.assertNotEquals(sortedBeans, shuffledBeans);

        Sort sort = by(
                Stream.of("column1", "column2", "column3")
                        .map(item -> new Sort.Order(Sort.Direction.ASC, item, Sort.NullHandling.NULLS_FIRST))
                        .collect(Collectors.toList())
        );

        Comparator<SortTestBean> comparator = SortUtils.comparing(sort);
        shuffledBeans.sort(comparator);
        log.info("shuffledBeans:\n{}", joining(shuffledBeans));
        Assertions.assertEquals(sortedBeans, shuffledBeans);

        Collections.reverse(sortedBeans);
        log.info("sortedBeans:\n{}", joining(sortedBeans));
        sort = by(
                Stream.of("column1", "column2", "column3")
                        .map(item -> new Sort.Order(Sort.Direction.DESC, item, Sort.NullHandling.NULLS_LAST))
                        .collect(Collectors.toList())
        );
        comparator = SortUtils.comparing(sort);
        shuffledBeans.sort(comparator);
        log.info("shuffledBeans:\n{}", joining(shuffledBeans));
        Assertions.assertEquals(sortedBeans, shuffledBeans);
    }

    private static String joining(List<SortTestBean> beans) {
        return beans.stream().map(SortTestBean::toString).collect(Collectors.joining("\n"));
    }

    private Stream<SortTestBean> stream() {
        return IntStream.range(0, 3).boxed().flatMap(this::stream);
    }

    private Stream<SortTestBean> stream(Integer column1) {
        return IntStream.range(0, 3).boxed()
                .flatMap(column2 -> stream(column1, column2));
    }

    private Stream<SortTestBean> stream(Integer column1, Integer column2) {
        return IntStream.range(0, 3).mapToObj(column3 -> new SortTestBean(column1, column2, column3));
    }

}
