package com.github.peacetrue.spring.signature;

import com.github.peacetrue.result.builder.ResultBuilderAutoConfiguration;
import com.github.peacetrue.result.builder.ResultMessageSourceAutoConfiguration;
import com.github.peacetrue.result.exception.ResultExceptionAutoConfiguration;
import com.github.peacetrue.result.exception.ResultExceptionSupportAutoConfiguration;
import com.github.peacetrue.result.exception.signature.SignatureResultExceptionAutoConfiguration;
import com.github.peacetrue.signature.ClientInvalidException;
import com.github.peacetrue.signature.SignatureParameterNames;
import com.github.peacetrue.spring.web.servlet.CachedBodyFilterAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

/**
 * @author peace
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SignatureTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
class SignatureSpringTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /** 测试一般情况下 /echo 接口可以正常调用 */
    @Test
    void echo() {
        echo(restTemplate);
    }

    private static void echo(TestRestTemplate restTemplate) {
        String input = "我";

        // for URL
        log.debug("------getForObject--------");
        String output = restTemplate.getForObject("/echo?input={0}", String.class, input);
        Assertions.assertEquals(input, output);
        log.debug("------delete--------");
        restTemplate.delete("/echo?input={0}", String.class, input);

        // for Form
        log.debug("------Form--------");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("input", input);
        log.debug("------postForObject--------");
        output = restTemplate.postForObject("/echo", new HttpEntity<>(params, headers), String.class, input);
        Assertions.assertEquals(input, output);
        log.debug("------put--------");
        restTemplate.put("/echo", new HttpEntity<>(params, headers), String.class, input);
        log.debug("------patchForObject--------");
        output = restTemplate.patchForObject("/echo", new HttpEntity<>(params, headers), String.class, input);
        Assertions.assertEquals(input, output);

        // for JSON
        log.debug("------JSON--------");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        SignatureTestController.TestBean testBean = new SignatureTestController.TestBean(input);
        log.debug("------postForObject--------");
        output = restTemplate.postForObject("/echo", new HttpEntity<>(testBean, headers), String.class, input);
        Assertions.assertEquals(input, output);
        log.debug("------patchForObject--------");
        output = restTemplate.patchForObject("/echo", new HttpEntity<>(testBean, headers), String.class, input);
        Assertions.assertEquals(input, output);
        log.debug("------put--------");
        restTemplate.put("/echo", new HttpEntity<>(params, headers), String.class, input);
    }

    private static void echoError(TestRestTemplate restTemplate) {
        String input = RandomStringUtils.randomAlphanumeric(10);
        // for JSON
        log.debug("------JSON--------");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        SignatureTestController.TestBean testBean = new SignatureTestController.TestBean(input);
        log.debug("------postForObject--------");
        String output = restTemplate.postForObject("/echo", new HttpEntity<>(testBean, headers), String.class, input);
        Assertions.assertNotEquals(input, output);
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {SignatureTestConfiguration.class, CachedBodyFilterAutoConfiguration.class, SignatureAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("correctClientSecret")
    static class CorrectClientSecret {

        @Autowired
        private TestRestTemplate restTemplate;
        @Autowired
        private SignatureProperties signatureProperties;

        @Test
        void echo() {
            SignatureSpringTest.echo(this.restTemplate);
            String clientId = signatureProperties.getParameterNames().getClientId();
            Assertions.assertThrows(ClientInvalidException.class, () -> restTemplate.getForObject("/echo?{0}={1}", String.class, clientId, "missing"));
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {SignatureTestConfiguration.class, CachedBodyFilterAutoConfiguration.class, SignatureAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("customClientSecretProvider")
    static class CustomClientSecretProvider {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void echo() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> restTemplate.getForObject("/echo", String.class), "missing clientId");
        }
    }


    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {SignatureTestConfiguration.class, SignatureAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("correctClientSecret")
    static class CorrectClientSecretMissingCachedBodyFilter {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void echo() {
            echoError(restTemplate);
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {SignatureTestConfiguration.class, SignatureAutoConfiguration.class, CachedBodyFilterAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles({"correctClientSecret", "mismatchedSignPath"})
    static class CorrectClientSecretMismatchedSignPath {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void echo() {
            echoError(restTemplate);
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {
            SignatureTestConfiguration.class,
            ResultMessageSourceAutoConfiguration.class, ResultBuilderAutoConfiguration.class, ResultExceptionAutoConfiguration.class, ResultExceptionSupportAutoConfiguration.class, SignatureResultExceptionAutoConfiguration.class,
            SignatureAutoConfiguration.class, CachedBodyFilterAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class
    }, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles({"correctClientSecret", "customClientSecretProvider", "customStringSignerFactory", "customPropertyValuesGenerator"})
    static class CorrectClientSecretResult {

        @Autowired
        private TestRestTemplate restTemplate;
        private SignatureParameterNames signatureParameterNames;

        @Autowired
        public void setSignatureProperties(SignatureProperties properties) {
            this.signatureParameterNames = properties.getParameterNames();
        }

        @Test
        void echo() {
            String input = RandomStringUtils.randomAlphanumeric(10);
            // for JSON
            log.debug("------JSON--------");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            SignatureTestController.TestBean testBean = new SignatureTestController.TestBean(input);
            String clientId = signatureParameterNames.getClientId();

            log.debug("------for ClientId--------");
            String output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class, clientId, "serverMissing");
            Assertions.assertNotEquals(input, output);

            log.debug("------for Timestamp--------");
            output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class,
                    signatureParameterNames.getTimestamp(), System.currentTimeMillis() - 100_000, clientId);
            Assertions.assertNotEquals(input, output);
            output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class,
                    signatureParameterNames.getTimestamp(), "not a number", clientId);
            Assertions.assertNotEquals(input, output);

            log.debug("------for Nonce--------");
            String nonce = UUID.randomUUID().toString();
            output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class,
                    signatureParameterNames.getNonce(), nonce);
            Assertions.assertEquals(input, output);
            output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class,
                    signatureParameterNames.getNonce(), nonce);
            Assertions.assertNotEquals(input, output);

            log.debug("------for Signature--------");
            output = restTemplate.postForObject("/echo?{0}={1}", new HttpEntity<>(testBean, headers), String.class, clientId, "random");
            Assertions.assertNotEquals(input, output);
        }
    }

/*
    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {SignatureAutoConfiguration.class, CachedBodyFilterAutoConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class, SignatureTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("correctPublicPrivateKey")
    static class CorrectPublicPrivateKey {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void echo() {
            SignatureSpringTest.echo(this.restTemplate);
        }
    }
*/

/*
    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = {
            ResultMessageSourceAutoConfiguration.class, ResultBuilderAutoConfiguration.class, ResultExceptionAutoConfiguration.class, ResultExceptionSupportAutoConfiguration.class, SignatureResultExceptionAutoConfiguration.class,
            SignatureAutoConfiguration.class, SignatureTestConfiguration.class, SignatureClientAutoConfiguration.class, SignatureServerAutoConfiguration.class
    }, webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("errorPublicPrivateKey")
    static class ErrorPublicPrivateKey {

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        void echo() {
            String input = RandomStringUtils.randomAlphanumeric(10);
            ResultImpl result = this.restTemplate.getForObject("/echo?input={0}", ResultImpl.class, input);
            Assertions.assertNotNull(result);
        }
    }
*/


}
