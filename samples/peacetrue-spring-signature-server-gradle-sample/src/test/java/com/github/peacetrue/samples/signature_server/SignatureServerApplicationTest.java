package com.github.peacetrue.samples.signature_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author peace
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class SignatureServerApplicationTest {

    @Autowired
    private List<RestTemplateCustomizer> restTemplateCustomizers;

    @Test
    void basic() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizers.forEach(item -> item.customize(restTemplate));
        String input = "1";
        String response = restTemplate.getForObject("http://localhost:8080/echo?input={0}", String.class, input);
        Assertions.assertEquals(response, input);
    }
}
