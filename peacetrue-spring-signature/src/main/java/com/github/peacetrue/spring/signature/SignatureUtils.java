package com.github.peacetrue.spring.signature;

import com.github.peacetrue.codec.Codec;
import com.github.peacetrue.net.URLQueryUtils;
import com.github.peacetrue.servlet.ContentTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 签名工具类。
 *
 * @author peace
 **/
@Slf4j
class SignatureUtils {

    private SignatureUtils() {
    }

    static String buildMessage(@Nullable MediaType contentType,
                               Map<String, List<String>> queryParams, byte[] body) {
        return contentType == null ? joinMessage(queryParams) : joinMessage(contentType, queryParams, body);
    }

    static String joinMessage(MediaType contentType, Map<String, List<String>> queryParams, byte[] body) {
        Map<String, List<String>> params = new HashMap<>(queryParams);
        boolean isForm = ContentTypeUtils.isForm(contentType);
        if (isForm) {
            Map<String, List<String>> formParams = URLQueryUtils.parseQuery(Codec.CHARSET_UTF8.encode(body));
            log.debug("got body form params: {}", formParams);
            params.putAll(formParams);
        }
        String message = joinMessage(params);
        if (!isForm) {
            String bodyMessage = Codec.BASE64.encode(body);
            log.debug("got body message(base64): {}", bodyMessage);
            message += bodyMessage;
        }
        return message;
    }

    static String joinMessage(Map<String, List<String>> params) {
        return params.keySet().stream().sorted()
                .flatMap(key -> params.get(key).stream().map(value -> key + Objects.toString(value, "")))
                .collect(Collectors.joining());
    }
}
