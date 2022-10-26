package com.github.peacetrue.spring.signature;

import com.github.peacetrue.codec.Codec;
import com.github.peacetrue.net.URLQueryUtils;
import com.github.peacetrue.range.LongRange;
import com.github.peacetrue.servlet.CachedBodyUtils;
import com.github.peacetrue.servlet.ContentTypeUtils;
import com.github.peacetrue.signature.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 签名服务端验证服务。
 *
 * @author peace
 **/
@Slf4j
public class SignatureServerService {

    private final SignatureParameterNames propertyNames;
    private final ClientSecretProvider clientSecretProvider;
    private final StringSignerFactory stringSignerFactory;
    /** @see SignatureProperties#getTimestampOffset() */
    private final LongRange timestampOffset;
    private final NonceVerifier nonceVerifier;

    public SignatureServerService(SignatureParameterNames propertyNames,
                                  ClientSecretProvider clientSecretProvider,
                                  StringSignerFactory stringSignerFactory,
                                  LongRange timestampOffset,
                                  NonceVerifier nonceVerifier) {
        this.propertyNames = Objects.requireNonNull(propertyNames);
        this.clientSecretProvider = Objects.requireNonNull(clientSecretProvider);
        this.stringSignerFactory = Objects.requireNonNull(stringSignerFactory);
        this.timestampOffset = Objects.requireNonNull(timestampOffset);
        this.nonceVerifier = Objects.requireNonNull(nonceVerifier);
    }

    public HttpServletRequest verify(HttpServletRequest request) throws IOException, ServletException {
        log.info("verify request: {}", request.getRequestURL());

        String clientId = getParameter(request, propertyNames.getClientId());
        String nonce = getParameter(request, propertyNames.getNonce());
        Long timestamp = getLongParameter(request, propertyNames.getTimestamp());
        String signature = getParameter(request, propertyNames.getSignature());
        log.debug("got clientId: {}, nonce: {}, timestamp: {}, signature: {}", clientId, nonce, timestamp, signature);

        String clientSecret = clientSecretProvider.getClientSecret(clientId);
        if (clientSecret == null) throw new ClientInvalidException(clientId);

        long serverTimestamp = System.currentTimeMillis();
        if (!timestampOffset.increase(serverTimestamp).contains(timestamp)) {
            throw new TimestampInvalidException(timestamp, serverTimestamp, timestampOffset);
        }

        if (nonceVerifier.exists(clientId, nonce)) throw new NonceInvalidException(nonce);

        Map<String, List<String>> params = URLQueryUtils.fromBeanMap(request.getParameterMap());
        log.debug("got form params with signature addition params: {}", params);
        params.remove(propertyNames.getSignature());

        String message = SignatureUtils.joinMessage(params);
        if (ContentTypeUtils.isRaw(request.getContentType())) {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            String bodyMessage = Codec.BASE64.encode(bodyBytes);
            log.debug("got body message(base64): {}", bodyMessage);
            message += bodyMessage;
            request = CachedBodyUtils.wrapper(request, bodyBytes);
        }
        log.debug("build server message: {}", message);

        Signer<String, String> signatureSigner = this.stringSignerFactory.createSigner(clientSecret);
        if (!signatureSigner.verify(message, signature)) throw new SignatureInvalidException(signature, message);
        log.trace("signature verify passed");
        return request;
    }

    private static Long getLongParameter(ServletRequest request, String name) throws MissingServletRequestParameterException {
        String timestamp = getParameter(request, name);
        try {
            return Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            TypeMismatchException typeMismatchException = new TypeMismatchException(timestamp, Long.class, e);
            typeMismatchException.initPropertyName(name);
            throw typeMismatchException;
        }
    }

    private static String getParameter(ServletRequest request, String name) throws MissingServletRequestParameterException {
        String value = request.getParameter(name);
        if (value != null) return value;
        throw new MissingServletRequestParameterException(name, String.class.getName());
    }

}
