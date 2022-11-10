package com.github.peacetrue.spring.beans;

import org.jeasy.random.EasyRandom;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Bean 转换性能测试。
 *
 * @author peace
 **/
@State(Scope.Benchmark)
public class BeanConverterBenchmark {
    private final EasyRandom easyRandom = new EasyRandom();
    private User user;

    @Setup(Level.Iteration)
    public void setupUser() {
        user = easyRandom.nextObject(User.class);
    }

    @Benchmark
    public void baseline(Blackhole bh) {
        bh.consume(null);
    }

    @Benchmark
    public void byManual(Blackhole bh) {
        bh.consume(BeanConverters.ByManual.INSTANCE.convert(user, UserVO.class));
    }

    @Benchmark
    public void byJackson(Blackhole bh) {
        bh.consume(BeanConverters.ByJackson.INSTANCE.convert(user, UserVO.class));
    }

    @Benchmark
    public void bySpringCopyProperties(Blackhole bh) {
        bh.consume(BeanConverters.BySpringCopyProperties.INSTANCE.convert(user, UserVO.class));
    }

    @Benchmark
    public void bySpringBeanCopierManual(Blackhole bh) {
        bh.consume(BeanConverters.BySpringBeanCopierManual.INSTANCE.convert(user, UserVO.class));
    }

    @Benchmark
    public void bySpringBeanCopierGeneric(Blackhole bh) {
        bh.consume(BeanConverters.BySpringBeanCopierGeneric.INSTANCE.convert(user, UserVO.class));
    }

    @Benchmark
    public void bySpringBeanCopierGenericUseConverter(Blackhole bh) {
        bh.consume(BeanConverters.BySpringBeanCopierGenericUseConverter.INSTANCE.convert(user, UserVO.class));
    }

    //Benchmark                                                            Mode  Cnt     Score     Error  Units
    //BeanConverterBenchmark.baseline                                      avgt    5     2.262 ±   0.264  ns/op
    //BeanConverterBenchmark.baseline:·stack                               avgt            NaN              ---
    //BeanConverterBenchmark.byJackson                                     avgt    5  1218.008 ± 125.152  ns/op
    //BeanConverterBenchmark.byJackson:·stack                              avgt            NaN              ---
    //BeanConverterBenchmark.byManual                                      avgt    5     7.134 ±   0.133  ns/op
    //BeanConverterBenchmark.byManual:·stack                               avgt            NaN              ---
    //BeanConverterBenchmark.bySpringBeanCopierGeneric                     avgt    5   152.511 ±  25.261  ns/op
    //BeanConverterBenchmark.bySpringBeanCopierGeneric:·stack              avgt            NaN              ---
    //BeanConverterBenchmark.bySpringBeanCopierGenericUseConverter         avgt    5   169.870 ±  26.536  ns/op
    //BeanConverterBenchmark.bySpringBeanCopierGenericUseConverter:·stack  avgt            NaN              ---
    //BeanConverterBenchmark.bySpringBeanCopierManual                      avgt    5     5.495 ±   0.420  ns/op
    //BeanConverterBenchmark.bySpringBeanCopierManual:·stack               avgt            NaN              ---
    //BeanConverterBenchmark.bySpringCopyProperties                        avgt    5   319.071 ±  10.365  ns/op
    //BeanConverterBenchmark.bySpringCopyProperties:·stack                 avgt            NaN              ---
}
