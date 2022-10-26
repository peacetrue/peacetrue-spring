package com.github.peacetrue.spring.data.r2dbc.convert;

import io.r2dbc.spi.Clob;
import org.reactivestreams.Publisher;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * {@link  Clob} 转换器。
 *
 * @author peace
 **/
public enum ClobToStringConverter implements Converter<Clob, String> {

    INSTANCE;

    @Override
    public String convert(Clob clob) {
        Publisher<CharSequence> publisher = clob.stream();
        if (publisher instanceof Mono) {
            return ((Mono<CharSequence>) publisher).map(CharSequence::toString).block();
        } else if (publisher instanceof Flux) {
            return ((Flux<CharSequence>) publisher).map(CharSequence::toString).collect(Collectors.joining()).block();
        } else {
            throw new UnsupportedOperationException(
                    String.format("can't convert [%s] to String", publisher.getClass().getName())
            );
        }
    }
}
